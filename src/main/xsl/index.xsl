<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017 Yegor Bugayenko

Permission is hereby granted, free of charge,  to any person obtaining
a copy  of  this  software  and  associated  documentation files  (the
"Software"),  to deal in the Software  without restriction,  including
without limitation the rights to use,  copy,  modify,  merge, publish,
distribute,  sublicense,  and/or sell  copies of the Software,  and to
permit persons to whom the Software is furnished to do so,  subject to
the  following  conditions:   the  above  copyright  notice  and  this
permission notice  shall  be  included  in  all copies or  substantial
portions of the Software.  The software is provided  "as is",  without
warranty of any kind, express or implied, including but not limited to
the warranties  of merchantability,  fitness for  a particular purpose
and non-infringement.  In  no  event shall  the  authors  or copyright
holders be liable for any claim,  damages or other liability,  whether
in an action of contract,  tort or otherwise,  arising from, out of or
in connection with the software or  the  use  or other dealings in the
software.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>ThreeCopies</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <p>
      <xsl:text>We help you backup your server-side data.</xsl:text>
    </p>
    <p>
      <xsl:text>
        You create a bash script, which grabs your
        valuable data from your data sources, packages it,
        and uploads somewhere where it will be safe.
        We start that script every hour, every day, and
        every week (inside Docker container), record its output and lets you see it.
        It's nothing more than a good old crontab, but hosted.
      </xsl:text>
    </p>
    <p>
      <xsl:text>
        How you design your script depends on your specific data,
        their location, format, etc. Here are our recommendations
        for the most typical types of data and storages:
      </xsl:text>
      <a href="https://github.com/yegor256/threecopies#how-to-configure">
        <xsl:text>How to configure?</xsl:text>
      </a>
    </p>
    <p>
      <xsl:text>
        At the moment the system is free, please don't abuse it.
      </xsl:text>
    </p>
  </xsl:template>
</xsl:stylesheet>
