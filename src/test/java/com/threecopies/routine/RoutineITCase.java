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
package com.threecopies.routine;

import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import com.jcabi.s3.fake.FkBucket;
import com.jcabi.ssh.Shell;
import com.threecopies.base.Base;
import com.threecopies.base.DyBase;
import com.threecopies.base.Dynamo;
import com.threecopies.base.User;
import java.util.Date;
import org.cactoos.Func;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link Routine}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class RoutineITCase {

    @Test
    public void startsAndFinishes() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("jeff");
        user.script("test").update("echo 1");
        final Bucket bucket = new FkBucket();
        final Func<Void, Integer> routine = new Routine(
            base, new Shell.Fake(0, "0\nworks\nwell", ""), bucket
        );
        for (int idx = 0; idx < 2; ++idx) {
            MatcherAssert.assertThat(
                routine.apply(null),
                Matchers.greaterThan(0)
            );
        }
        MatcherAssert.assertThat(
            new Ocket.Text(
                bucket.ocket(
                    String.format(
                        "jeff-test-week-%tF-%1$tH-%1$tM",
                        new Date()
                    )
                )
            ).read(),
            Matchers.containsString("works\nwell")
        );
    }

}
