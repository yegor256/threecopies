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
import com.threecopies.base.User;
import java.io.IOException;
import java.util.Iterator;
import org.cactoos.list.StickyList;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeWhen;

/**
 * One script.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
final class TkScript implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkScript(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request request) throws IOException {
        final User user = new RqUser(this.base, request);
        final RqHref href = new RqHref.Base(request);
        final Iterator<String> name = href.href().param("name").iterator();
        return new RsPage(
            "/xsl/script.xsl",
            request,
            () -> new StickyList<>(
                new XeAppend("menu", "scripts"),
                new XeWhen(
                    name.hasNext(),
                    () -> new XeDirectives(
                        user.script(name.next()).toXembly()
                    )
                )
            )
        );
    }
}
