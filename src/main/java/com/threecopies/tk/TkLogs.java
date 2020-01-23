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
package com.threecopies.tk;

import com.jcabi.aspects.Tv;
import com.threecopies.base.Base;
import java.io.IOException;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Limited;
import org.cactoos.list.StickyList;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Logs.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class TkLogs implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkLogs(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request request) throws IOException {
        return new RsPage(
            "/xsl/logs.xsl",
            request,
            () -> new StickyList<>(
                new XeAppend("menu", "logs"),
                new XeDirectives(
                    new Directives().add("logs").append(
                        new Joined<>(
                            new Limited<Iterable<Directive>>(
                                Tv.TWENTY,
                                new RqUser(this.base, request).logs()
                            )
                        )
                    )
                )
            )
        );
    }
}
