<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2023 Yegor Bugayenko

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
      <xsl:text>script</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <form action="/save" method="post">
      <fieldset>
        <label>
          <xsl:text>Name:</xsl:text>
        </label>
        <input name="name" type="text" size="30" maxlength="32">
          <xsl:if test="script">
            <xsl:attribute name="value">
              <xsl:value-of select="script/name"/>
            </xsl:attribute>
          </xsl:if>
        </input>
        <label>
          <xsl:text>Bash (</xsl:text>
          <a href="https://github.com/yegor256/threecopies#how-to-configure">
            <xsl:text>how to?</xsl:text>
          </a>
          <xsl:text>):</xsl:text>
        </label>
        <textarea name="body" style="width:100%;height:34em;font-family:monospace;font-size:0.7em;line-height:1.2em;">
          <xsl:if test="script">
            <xsl:value-of select="script/bash"/>
          </xsl:if>
          <xsl:if test="not(script)">
            <xsl:text>#!/bin/bash</xsl:text>
          </xsl:if>
        </textarea>
        <button type="submit">
          <xsl:text>Save</xsl:text>
        </button>
      </fieldset>
    </form>
    <p>
      <xsl:text>Remember, if your script takes more than </xsl:text>
      <strong>
        <xsl:text>three hours</xsl:text>
      </strong>
      <xsl:text>, we kill it.</xsl:text>
    </p>
  </xsl:template>
</xsl:stylesheet>
