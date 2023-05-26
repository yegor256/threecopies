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
package com.threecopies.tk;

import com.jcabi.manifests.Manifests;
import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import com.jcabi.s3.Region;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqHref;
import org.takes.rs.RsText;

/**
 * One log.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
final class TkLog implements Take {

    /**
     * Bucket.
     */
    private final Bucket bucket;

    /**
     * Ctor.
     */
    TkLog() {
        this(
            new Region.Simple(
                Manifests.read("ThreeCopies-S3Key"),
                Manifests.read("ThreeCopies-S3Secret")
            ).bucket("logs.threecopies.com")
        );
    }

    /**
     * Ctor.
     * @param bkt Bucket
     */
    TkLog(final Bucket bkt) {
        this.bucket = bkt;
    }

    @Override
    public Response act(final Request request) throws IOException {
        final Identity identity = new RqAuth(request).identity();
        if (identity.equals(Identity.ANONYMOUS)) {
            throw new RsForward(
                new RsFlash("You must be logged in to view logs.")
            );
        }
        final String login = identity.properties().get("login");
        final String name = new RqHref.Smart(request).single("name");
        if (!name.startsWith(String.format("%s_", login))) {
            throw new RsForward(
                new RsFlash(
                    String.format(
                        "Permission denied: \"%s\".", name
                    )
                )
            );
        }
        final Ocket ocket = this.bucket.ocket(name);
        if (!ocket.exists()) {
            throw new RsForward(
                new RsFlash(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "The log of \"%s\" doesn't exist, maybe the backup job is still in progress, please wait and check the Logs tab",
                        name
                    )
                )
            );
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ocket.read(baos);
        return new RsText(baos.toByteArray());
    }
}
