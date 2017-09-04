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

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.xembly.Xembler;

/**
 * Integration case for {@link DyScript}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class DyScriptITCase {

    @Test
    public void savesBashScript() throws Exception {
        final Script script = new DyScript(new Dynamo(), "jeffrey", "test");
        script.update("echo 'hello'");
        MatcherAssert.assertThat(
            new Xembler(script.toXembly()).xml(),
            XhtmlMatchers.hasXPaths(
                "/script[name='test']",
                "/script/bash"
            )
        );
    }

}
