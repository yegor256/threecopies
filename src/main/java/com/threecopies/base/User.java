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

import java.io.IOException;
import java.util.regex.Pattern;
import org.xembly.Directive;

/**
 * User.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface User {

    /**
     * Script regex.
     */
    Pattern SCRIPT_NAME = Pattern.compile("[a-z0-9A-Z-]{4,32}");

    /**
     * Get all scripts.
     * @return All scripts
     * @throws IOException If fails
     */
    Iterable<Directive> scripts() throws IOException;

    /**
     * Get all recent logs.
     * @return All scripts
     * @throws IOException If fails
     */
    Iterable<Directive> logs() throws IOException;

    /**
     * Get script by name.
     * @param name Script name
     * @return The script
     * @throws IOException If fails
     */
    Script script(String name) throws IOException;

}
