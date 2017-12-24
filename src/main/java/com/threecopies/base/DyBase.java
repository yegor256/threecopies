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

import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import org.cactoos.iterable.Mapped;

/**
 * DynamoDB base.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class DyBase implements Base {

    /**
     * DynamoDB region.
     */
    private final Region region;

    /**
     * Ctor.
     * @param rgn The region
     */
    public DyBase(final Region rgn) {
        this.region = rgn;
    }

    @Override
    public User user(final String login) {
        if (!Base.USER_NAME.matcher(login).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid user name \"%s\", must match \"%s\" regexp",
                    login, Base.USER_NAME
                )
            );
        }
        return new DyUser(this.region, login);
    }

    @Override
    public Iterable<Script> scripts() {
        return new Mapped<>(
            item -> new DyScript(
                this.region,
                item.get("login").getS(),
                item.get("name").getS()
            ),
            this.table().frame()
        );
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("scripts");
    }

}
