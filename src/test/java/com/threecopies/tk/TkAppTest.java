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

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import com.jcabi.matchers.XhtmlMatchers;
import com.threecopies.base.FkBase;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkApp}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class TkAppTest {

    @Test
    public void rendersHomePage() throws Exception {
        final Take take = new TkApp(new FkBase());
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    take.act(
                        new RqWithHeaders(
                            new RqFake("GET", "/"),
                            "Accept: text/xml"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/@date",
                "/page/@sla",
                "/page/millis",
                "/page/links/link[@rel='self']"
            )
        );
    }

    @Test
    public void rendersHomePageViaHttp() throws Exception {
        final Take app = new TkApp(new FkBase());
        new FtRemote(app).exec(
            home -> {
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/xhtml:html");
                new JdkRequest(home)
                    .through(VerboseWire.class)
                    .header("accept", "application/xml")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/page/version");
            }
        );
    }

    @Test
    public void rendersNotFoundPage() throws Exception {
        final Take take = new TkApp(new FkBase());
        MatcherAssert.assertThat(
            new RsPrint(
                take.act(new RqFake("HEAD", "/not-found"))
            ).printBody(),
            Matchers.containsString("not-found")
        );
    }

}
