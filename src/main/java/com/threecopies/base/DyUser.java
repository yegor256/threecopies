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

import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import org.cactoos.iterable.Mapped;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * DynamoDB user.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DyUser implements User {

    /**
     * DynamoDB region.
     */
    private final Region region;

    /**
     * GitHub login.
     */
    private final String login;

    /**
     * Ctor.
     * @param rgn The region
     * @param lgn GitHub login
     */
    DyUser(final Region rgn, final String lgn) {
        this.region = rgn;
        this.login = lgn;
    }

    @Override
    public Iterable<Iterable<Directive>> scripts() {
        return new Mapped<Item, Iterable<Directive>>(
            this.region.table("scripts")
                .frame()
                .through(
                    new QueryValve()
                        // @checkstyle MagicNumber (1 line)
                        .withLimit(10)
                        .withSelect(Select.ALL_ATTRIBUTES)
                )
                .where("login", this.login),
            item -> new Directives()
                .add("script")
                .add("name").set(item.get("name").getS()).up()
                .add("bash").set(item.get("bash").getS()).up()
                .add("paid").set(item.get("paid").getN()).up()
                .add("used").set(item.get("used").getN()).up()
                .add("hour").set(item.get("hour").getN()).up()
                .add("day").set(item.get("day").getN()).up()
                .add("week").set(item.get("week").getN()).up()
                .up()
        );
    }

    @Override
    public Iterable<Iterable<Directive>> logs() {
        return new Mapped<Item, Iterable<Directive>>(
            this.region.table("logs")
                .frame()
                .through(
                    new QueryValve()
                        .withIndexName("mine")
                        // @checkstyle MagicNumber (1 line)
                        .withLimit(20)
                        .withConsistentRead(false)
                        .withScanIndexForward(false)
                        .withSelect(Select.ALL_ATTRIBUTES)
                )
                .where("login", this.login),
            item -> new Directives()
                .add("log")
                .add("group").set(item.get("group").getS()).up()
                .add("start").set(item.get("start").getN()).up()
                .add("finish").set(item.get("finish").getN()).up()
                .add("period").set(item.get("period").getS()).up()
                .add("ocket").set(item.get("ocket").getS()).up()
                .add("exit").set(item.get("exit").getN()).up()
                .up()
        );
    }

    @Override
    public Script script(final String name) {
        if (!User.SCRIPT_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid script name \"%s\", must match \"%s\" regexp",
                    name, User.SCRIPT_NAME
                )
            );
        }
        return new DyScript(this.region, this.login, name);
    }

    @Override
    public void delete(final String group, final long start)
        throws IOException {
        if (!group.startsWith(String.format("%s/", this.login))) {
            throw new IllegalArgumentException(
                String.format(
                    "This log is not yours: %s@%s",
                    group, start
                )
            );
        }
        this.region.table("logs").delete(
            new Attributes()
                .with("group", group)
                .with("start", start)
        );
    }

}
