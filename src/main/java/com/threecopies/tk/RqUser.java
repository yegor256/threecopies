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
package com.threecopies.tk;

import com.threecopies.base.Base;
import com.threecopies.base.Script;
import com.threecopies.base.User;
import java.io.IOException;
import java.util.Locale;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.xembly.Directive;

/**
 * User in request.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
final class RqUser implements User {

    /**
     * The base.
     */
    private final Base base;

    /**
     * The request.
     */
    private final Request request;

    /**
     * Ctor.
     * @param bse Base
     * @param req Request
     */
    RqUser(final Base bse, final Request req) {
        this.base = bse;
        this.request = req;
    }

    @Override
    public Iterable<Iterable<Directive>> scripts() throws IOException {
        return this.user().scripts();
    }

    @Override
    public Iterable<Iterable<Directive>> logs() throws IOException {
        return this.user().logs();
    }

    @Override
    public Script script(final String name) throws IOException {
        return this.user().script(name);
    }

    @Override
    public void delete(final String group, final long start)
        throws IOException {
        this.user().delete(group, start);
    }

    /**
     * Get user name (GitHub handle).
     * @return The user found
     * @throws IOException If fails
     */
    private User user() throws IOException {
        final Identity identity = new RqAuth(this.request).identity();
        if (identity.equals(Identity.ANONYMOUS)) {
            throw new RsForward(
                new RsFlash("You must be logged in.")
            );
        }
        return this.base.user(
            identity.properties().get("login").toLowerCase(Locale.ENGLISH)
        );
    }

}
