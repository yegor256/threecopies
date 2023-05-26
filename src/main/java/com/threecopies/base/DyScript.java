/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 Yegor Bugayenko
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
import com.jcabi.manifests.Manifests;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * DynamoDB script.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings(
    {
        "PMD.AvoidDuplicateLiterals",
        "PMD.TooManyMethods",
        "PMD.ExcessiveImports"
    }
)
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
    public void delete() throws IOException {
        this.region.table("scripts").delete(
            new Attributes()
                .with("login", this.login)
                .with("name", this.name)
        );
    }

    @Override
    public Iterable<Item> open() throws IOException {
        final Collection<Item> open = new LinkedList<>();
        if (!this.overdue()) {
            final Table table = this.region.table("logs");
            open.addAll(
                table.frame()
                    .through(
                        new QueryValve()
                            .withLimit(1)
                            .withIndexName("open")
                            .withSelect(Select.ALL_ATTRIBUTES)
                            .withConsistentRead(false)
                    )
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
        }
        return open;
    }

    @Override
    public String ocket(final long time) throws IOException {
        final Iterator<Item> items = this.region.table("logs")
            .frame()
            .through(new QueryValve().withLimit(1))
            .where("group", this.group())
            .where(
                "start",
                new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(
                        new AttributeValue().withN(Long.toString(time))
                    )
            )
            .iterator();
        if (!items.hasNext()) {
            throw new RsForward(
                new RsFlash("Can't find log"),
                "/scripts"
            );
        }
        return items.next().get("ocket").getS();
    }

    @Override
    public void flush() throws IOException {
        this.item().put(
            "hour",
            new AttributeValueUpdate().withValue(
                new AttributeValue().withN("0")
            ).withAction(AttributeAction.PUT)
        );
    }

    @Override
    public void track(final long seconds) throws IOException {
        if (seconds > TimeUnit.MINUTES.toSeconds(2L)) {
            final Item item = this.item();
            item.put(
                "used",
                new AttributeValueUpdate().withValue(
                    new AttributeValue().withN(Long.toString(seconds))
                ).withAction(AttributeAction.ADD)
            );
            if (this.overdue() && item.has("stripe_customer")) {
                this.rebill();
            }
        }
    }

    @Override
    public void pay(final long cents, final String token, final String email)
        throws IOException {
        final String customer;
        try {
            customer = Customer.create(
                new MapOf<String, Object>(
                    new MapEntry<>("email", email),
                    new MapEntry<>("source", token)
                ),
                new RequestOptions.RequestOptionsBuilder().setApiKey(
                    Manifests.read("ThreeCopies-StripeSecret")
                ).build()
            ).getId();
        } catch (final StripeException ex) {
            throw new IOException(ex);
        }
        this.item().put(
            new AttributeUpdates()
                .with(
                    "stripe_cents",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withN(Long.toString(cents))
                    ).withAction(AttributeAction.PUT)
                )
                .with(
                    "stripe_customer",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withS(customer)
                    ).withAction(AttributeAction.PUT)
                )
        );
        this.rebill();
    }

    /**
     * It's overdue?
     * @return TRUE if there is not enough funds
     * @throws IOException If fails
     */
    private boolean overdue() throws IOException {
        final Item item = this.item();
        return Long.parseLong(item.get("used").getN())
            > Long.parseLong(item.get("paid").getN());
    }

    /**
     * Charge him again.
     * @throws IOException If fails
     */
    @SuppressWarnings("unchecked")
    private void rebill() throws IOException {
        final Item item = this.item();
        final Long cents = Long.parseLong(item.get("stripe_cents").getN());
        final String customer = item.get("stripe_customer").getS();
        try {
            Charge.create(
                new MapOf<>(
                    new MapEntry<>("amount", cents),
                    new MapEntry<>("currency", "usd"),
                    new MapEntry<>(
                        "description",
                        String.format("ThreeCopies: %s", this.name)
                    ),
                    new MapEntry<>("customer", customer)
                ),
                new RequestOptions.RequestOptionsBuilder().setApiKey(
                    Manifests.read("ThreeCopies-StripeSecret")
                ).build()
            );
        } catch (final StripeException ex) {
            throw new IOException(ex);
        }
        this.item().put(
            "paid",
            new AttributeValueUpdate().withValue(
                new AttributeValue().withN(
                    Long.toString(cents * TimeUnit.HOURS.toSeconds(1L))
                )
            ).withAction(AttributeAction.ADD)
        );
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
        if (!item.has(period)
            || DyScript.expired(item.get(period).getN(), period)) {
            final long start = System.currentTimeMillis();
            item.put(
                new AttributeUpdates()
                    .with(
                        period,
                        new AttributeValueUpdate().withValue(
                            new AttributeValue().withN(Long.toString(start))
                        ).withAction(AttributeAction.PUT)
                    )
            );
            created.add(
                this.region.table("logs").put(
                    new Attributes()
                        .with("group", new AttributeValue().withS(this.group()))
                        .with(
                            "start",
                            new AttributeValue().withN(Long.toString(start))
                        )
                        .with("login", new AttributeValue().withS(this.login))
                        .with("period", new AttributeValue().withS(period))
                        .with("exit", new AttributeValue().withN("0"))
                        .with(
                            "finish",
                            new AttributeValue().withN(
                                Long.toString(Long.MAX_VALUE)
                            )
                        )
                        .with(
                            "ttl",
                            new AttributeValue().withN(
                                Long.toString(
                                    // @checkstyle MagicNumber (2 lines)
                                    System.currentTimeMillis() / 1000L
                                        + TimeUnit.DAYS.toSeconds(14L)
                                )
                            )
                        )
                        .with(
                            "ocket",
                            new AttributeValue().withS(
                                String.format(
                                    "%s_%s-%s-%tF-%4$tH-%4$tM", this.login,
                                    this.name, period, new Date()
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
                    // @checkstyle MagicNumber (1 line)
                    .with("paid", TimeUnit.HOURS.toSeconds(25L))
                    .with("used", 0L)
                    .with("hour", 0L)
                    .with("day", 0L)
                    .with("week", 0L)
                    .with("bash", "echo 'Hello, world!'")
            );
        }
        return item;
    }

}
