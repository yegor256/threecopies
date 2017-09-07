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

import com.jcabi.dynamo.Item;
import java.io.IOException;
import org.xembly.Directive;

/**
 * Script.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface Script {

    /**
     * Render it to Xembly.
     * @return The xembly
     * @throws IOException If fails
     */
    Iterable<Directive> toXembly() throws IOException;

    /**
     * Delete this script.
     * @throws IOException If fails
     */
    void delete() throws IOException;

    /**
     * Update.
     * @param bash The bash script
     * @throws IOException If fails
     */
    void update(String bash) throws IOException;

    /**
     * Log items which are currently open (usually either one or none).
     * @return The log items that require attention
     * @throws IOException If fails
     */
    Iterable<Item> open() throws IOException;

    /**
     * Drop the hourly execution, to start right now.
     * @throws IOException If fails
     */
    void flush() throws IOException;

    /**
     * Track usage.
     * @param seconds Seconds just used
     * @throws IOException If fails
     */
    void track(long seconds) throws IOException;

    /**
     * Pay for the seconds.
     * @param cents Amount in cents
     * @param token Stripe token
     * @param email The email
     * @throws IOException If fails
     */
    void pay(long cents, String token, String email) throws IOException;

}
