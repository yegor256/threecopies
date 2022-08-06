/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2020 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge,  to any person obtaining
 * a copy  of  this  software  and  associated  documentation files  (the
 * "Software"),  to deal in the Software  without restriction,  including
 * without limitation the rights to use,  copy,  modify,  merge, publish,
 * distribute,  sublicense,  and/or sell  copies of the Software,  and to
 * permit persons to whom the Software is furnished to do so,  subject to
 * the  following  conditions:   the  above  copyright  notice  and  this
 * permission notice  shall  be  included  in  all copies or  substantial
 * portions of the Software.  The software is provided  "as is",  without
 * warranty of any kind, express or implied, including but not limited to
 * the warranties  of merchantability,  fitness for  a particular purpose
 * and non-infringement.  In  no  event shall  the  authors  or copyright
 * holders be liable for any claim,  damages or other liability,  whether
 * in an action of contract,  tort or otherwise,  arising from, out of or
 * in connection with the software or  the  use  or other dealings in the
 * software.
 */
package com.threecopies.routine;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Item;
import com.jcabi.log.Logger;
import com.jcabi.log.VerboseRunnable;
import com.jcabi.log.VerboseThreads;
import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import com.jcabi.ssh.Shell;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.threecopies.base.Base;
import com.threecopies.base.Script;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.cactoos.Func;
import org.cactoos.io.DeadInputStream;
import org.cactoos.io.DeadOutputStream;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.xembly.Xembler;

/**
 * Routine.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class Routine implements Func<Void, Integer> {

    /**
     * Dir.
     */
    private static final String DIR = "/tmp/threecopies";

    /**
     * Max duration in minutes for a script.
     */
    private static final long MAX_MINUTES = TimeUnit.HOURS.toMinutes(3L);

    /**
     * Service.
     */
    private final ScheduledExecutorService service;

    /**
     * Base.
     */
    private final Base base;

    /**
     * Shell to the server.
     */
    private final Shell shell;

    /**
     * S3 bucket.
     */
    private final Bucket bucket;

    /**
     * Ctor.
     * @param bse The base
     * @param ssh Shell
     * @param bkt Bucket
     */
    public Routine(final Base bse, final Shell ssh, final Bucket bkt) {
        this.base = bse;
        this.service = Executors.newSingleThreadScheduledExecutor(
            new VerboseThreads()
        );
        this.shell = ssh;
        this.bucket = bkt;
    }

    /**
     * Start it.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void start() {
        this.service.scheduleWithFixedDelay(
            new VerboseRunnable(
                () -> {
                    try {
                        this.apply(null);
                        // @checkstyle IllegalCatchCheck (1 line)
                    } catch (final Exception ex) {
                        throw new IllegalStateException(ex);
                    }
                },
                true, true
            ),
            1L, 1L, TimeUnit.MINUTES
        );
    }

    @Override
    public Integer apply(final Void none) throws Exception {
        int scripts = 0;
        int items = 0;
        for (final Script script : this.base.scripts()) {
            for (final Item item : script.open()) {
                if (item.has("container")) {
                    this.kill(item);
                    this.finish(script, item);
                } else {
                    this.start(script, item);
                }
                ++items;
            }
            ++scripts;
        }
        Logger.info(this, "%d log items seen in %d scripts", items, scripts);
        return items;
    }

    /**
     * Start a Docker container.
     * @param script The script
     * @param log The log
     * @throws IOException If fails
     */
    private void start(final Script script, final Item log)
        throws IOException {
        this.upload("start.sh");
        final XML xml = new XMLDocument(
            new Xembler(script.toXembly()).xmlQuietly()
        );
        final String login = xml.xpath("/script/login/text()").get(0);
        final String period = log.get("period").getS();
        final String container = log.get("ocket").getS();
        this.shell.exec(
            String.join(
                " && ",
                String.format("mkdir -p %s/%s", Routine.DIR, container),
                String.format("cat > %s/%s/script.sh", Routine.DIR, container)
            ),
            new UncheckedInput(
                new InputOf(
                    xml.xpath(
                        "/script/bash/text()"
                    ).get(0).replace("\r\n", "\n")
                )
            ).stream(),
            new DeadOutputStream(),
            new DeadOutputStream()
        );
        this.shell.exec(
            String.format(
                "%s/start.sh %s %s &",
                Routine.DIR, container, period
            ),
            new DeadInputStream(),
            new DeadOutputStream(),
            new DeadOutputStream()
        );
        log.put(
            new AttributeUpdates()
                .with(
                    "container",
                    new AttributeValueUpdate()
                        .withValue(new AttributeValue().withS(container))
                        .withAction(AttributeAction.PUT)
                )
        );
        Logger.info(this, "Started %s for %s", container, login);
    }

    /**
     * Kill it if it's running for too long.
     * @param log The log
     * @throws IOException If fails
     */
    private void kill(final Item log) throws IOException {
        final long mins = (System.currentTimeMillis()
            - Long.parseLong(log.get("start").getN()))
            / TimeUnit.MINUTES.toMillis(1L);
        if (mins > Routine.MAX_MINUTES) {
            this.upload("kill.sh");
            final String container = log.get("container").getS();
            new Shell.Plain(this.shell).exec(
                String.format("%s/kill.sh %s %d", Routine.DIR, container, mins)
            );
            Logger.info(this, "Killed %s, over %d minutes", container, mins);
        }
    }

    /**
     * Finish already running Docker container.
     * @param script The script
     * @param log The log
     * @throws IOException If fails
     */
    private void finish(final Script script, final Item log)
        throws IOException {
        this.upload("finish.sh");
        final String container = log.get("container").getS();
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        this.shell.exec(
            String.join(
                " && ",
                String.format("cd %s", Routine.DIR),
                String.format("./finish.sh %s", container)
            ),
            new DeadInputStream(),
            stdout,
            new DeadOutputStream()
        );
        final String[] parts = new String(
            stdout.toByteArray(), StandardCharsets.UTF_8
        ).split("\n", 2);
        if (parts.length > 1) {
            new Ocket.Text(
                this.bucket.ocket(log.get("ocket").getS())
            ).write(parts[1]);
            final int exit = Integer.parseInt(parts[0].trim());
            log.put(
                new AttributeUpdates()
                    .with(
                        "finish",
                        new AttributeValueUpdate().withValue(
                            new AttributeValue().withN(
                                Long.toString(System.currentTimeMillis())
                            )
                        ).withAction(AttributeAction.PUT)
                    )
                    .with(
                        "exit",
                        new AttributeValueUpdate().withValue(
                            new AttributeValue().withN(Integer.toString(exit))
                        ).withAction(AttributeAction.PUT)
                    )
            );
            script.track(
                (System.currentTimeMillis()
                    - Long.parseLong(log.get("start").getN()))
                    / TimeUnit.SECONDS.toMillis(1L)
            );
            Logger.info(
                this, "Finished %s with %s and %d log bytes",
                container, exit, parts[1].length()
            );
        }
    }

    /**
     * Upload resource.
     * @param res Name of it
     * @throws IOException If fails
     */
    private void upload(final String res) throws IOException {
        this.shell.exec(
            String.join(
                " && ",
                String.format("mkdir -p %s", Routine.DIR),
                String.format("cat > %s/%s", Routine.DIR, res),
                String.format("chmod a+x %s/%s", Routine.DIR, res)
            ),
            new UncheckedInput(
                new ResourceOf(
                    String.format(
                        "com/threecopies/routine/%s", res
                    )
                )
            ).stream(),
            new DeadOutputStream(),
            new DeadOutputStream()
        );
    }

}

