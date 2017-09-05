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
      <xsl:text>logs</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <xsl:apply-templates select="logs"/>
  </xsl:template>
  <xsl:template match="logs[not(log)]">
    <p>
      <xsl:text>You don't have any logs yet.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="logs[log]">
    <p>
      <xsl:text>There are your logs:</xsl:text>
    </p>
    <table>
      <thead>
        <tr>
          <th>
            <xsl:text>Group</xsl:text>
          </th>
          <th>
            <xsl:text>Period</xsl:text>
          </th>
          <th>
            <xsl:text>Finished</xsl:text>
          </th>
          <th>
            <xsl:text>Exit</xsl:text>
          </th>
          <th>
            <xsl:text>Options</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="log"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="log">
    <tr>
      <td>
        <xsl:value-of select="group"/>
      </td>
      <td>
        <xsl:value-of select="period"/>
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="finish = 9223372036854775807">
            <xsl:text>running...</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="finish"/>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:if test="finish != 9223372036854775807">
          <xsl:value-of select="exit"/>
        </xsl:if>
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="finish = 9223372036854775807">
          </xsl:when>
          <xsl:otherwise>
            <a href="/log?name={ocket}">
              <xsl:text>View</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
