/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
package com.threecopies.base;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * DynamoDB script.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DyScript implements Script {

    /**
     * DynamoDB region.
     */
    private final Region region;

    /**
     * GitHub login.
     */
    private final String login;

    /**
     * Script name.
     */
    private final String name;

    /**
     * Ctor.
     * @param rgn The region
     * @param lgn GitHub login
     * @param spt Script name
     */
    DyScript(final Region rgn, final String lgn, final String spt) {
        this.region = rgn;
        this.login = lgn;
        this.name = spt;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        final Item item = this.item();
        return new Directives()
            .add("script")
            .add("login").set(item.get("login").getS()).up()
            .add("bash").set(item.get("bash").getS()).up()
            .add("name").set(item.get("name").getS()).up()
            .up();
    }

    @Override
    public void update(final String bash) throws IOException {
        this.item().put(
            "bash",
            new AttributeValueUpdate()
                .withValue(new AttributeValue().withS(bash))
                .withAction(AttributeAction.PUT)
        );
    }

    @Override
    public Iterable<Item> open() throws IOException {
        final Table table = this.region.table("logs");
        final Collection<Item> open = new LinkedList<>();
        open.addAll(
            table.frame()
                .through(new QueryValve().withLimit(1))
                .where("group", this.group())
                .where(
                    "finish",
                    new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(
                            new AttributeValue().withN(
                                Long.toString(Long.MAX_VALUE)
                            )
                    )
                )
        );
        if (open.isEmpty()) {
            open.addAll(this.create());
        }
        return open;
    }

    /**
     * Create the next log.
     * @return Log item
     * @throws IOException If fails
     */
    private Collection<Item> create() throws IOException {
        final Item item = this.item();
        final Collection<Item> created = new LinkedList<>();
        created.addAll(this.required(item, "week"));
        if (created.isEmpty()) {
            created.addAll(this.required(item, "day"));
        }
        if (created.isEmpty()) {
            created.addAll(this.required(item, "hour"));
        }
        return created;
    }

    /**
     * The date/time is expired?
     * @param item The item
     * @param period Either hour, day or week
     * @return Collection of items or empty
     * @throws IOException If fails
     */
    private Collection<Item> required(final Item item, final String period)
        throws IOException {
        final Collection<Item> created = new LinkedList<>();
        if (!item.has("period")
            || DyScript.expired(item.get(period).getN(), period)) {
            item.put(
                new AttributeUpdates()
                    .with(
                        period,
                        new AttributeValueUpdate().withValue(
                            new AttributeValue().withN(
                                Long.toString(System.currentTimeMillis())
                            )
                        ).withAction(AttributeAction.PUT)
                    )
            );
            created.add(
                this.region.table("logs").put(
                    new Attributes()
                        .with("group", new AttributeValue().withS(this.group()))
                        .with("login", new AttributeValue().withS(this.login))
                        .with("period", new AttributeValue().withS(period))
                        .with(
                            "finish",
                            new AttributeValue().withN(
                                Long.toString(Long.MAX_VALUE)
                            )
                        )
                        .with(
                            "ocket",
                            new AttributeValue().withS(
                                String.format(
                                    "%s-%d", this.login,
                                    System.currentTimeMillis()
                                )
                            )
                        )
                )
            );
        }
        return created;
    }

    /**
     * The date/time is expired?
     * @param msec Epoch msec
     * @param period Either hour, day or week
     * @return TRUE if expired
     */
    private static boolean expired(final String msec, final String period) {
        final long inc;
        if ("week".equals(period)) {
            // @checkstyle MagicNumber (1 line)
            inc = TimeUnit.DAYS.toMillis(7L);
        } else if ("day".equals(period)) {
            inc = TimeUnit.DAYS.toMillis(1L);
        } else if ("hour".equals(period)) {
            inc = TimeUnit.HOURS.toMillis(1L);
        } else {
            throw new IllegalArgumentException(
                String.format("What does \"%s\" mean?", period)
            );
        }
        return Long.parseLong(msec) + inc < System.currentTimeMillis();
    }

    /**
     * Group name.
     * @return Group name
     */
    private String group() {
        return String.format("%s/%s", this.login, this.name);
    }

    /**
     * Script item.
     * @return Item
     * @throws IOException If fails
     */
    private Item item() throws IOException {
        final Table table = this.region.table("scripts");
        final Iterator<Item> items = table.frame()
            .through(
                new QueryValve()
                    .withLimit(1)
                    .withSelect(Select.ALL_ATTRIBUTES)
            )
            .where("login", this.login)
            .where("name", this.name)
            .iterator();
        final Item item;
        if (items.hasNext()) {
            item = items.next();
        } else {
            item = table.put(
                new Attributes()
                    .with("login", this.login)
                    .with("name", this.name)
                    .with("bash", "echo 'Hello, worl!'")
            );
        }
        return item;
    }

}
