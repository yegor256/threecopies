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
      <xsl:text>scripts</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <p>
      <a href="/script">Create new script</a>
      <xsl:text>.</xsl:text>
    </p>
    <xsl:apply-templates select="scripts"/>
  </xsl:template>
  <xsl:template match="scripts[not(script)]">
    <p>
      <xsl:text>You don't have any scripts yet.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="scripts[script]">
    <p>
      <xsl:text>There are </xsl:text>
      <xsl:value-of select="count(script)"/>
      <xsl:text> scripts:</xsl:text>
    </p>
    <table>
      <thead>
        <tr>
          <th>
            <xsl:text>Name</xsl:text>
          </th>
          <th>
            <xsl:text>Options</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="script"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="script">
    <tr>
      <td>
        <a href="/script?name={name}">
          <xsl:value-of select="name"/>
        </a>
      </td>
      <td>
        <a href="/delete?name={name}">
          <xsl:text>Delete</xsl:text>
        </a>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
