<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2020 Yegor Bugayenko

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
  <xsl:template name="sec">
    <xsl:param name="sec"/>
    <xsl:choose>
      <xsl:when test="$sec = 0">
        <xsl:text>millis</xsl:text>
      </xsl:when>
      <xsl:when test="$sec &lt; 60">
        <span title="{$sec} seconds">
          <xsl:value-of select="format-number($sec, '0')"/>
          <xsl:text>s</xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="$sec &lt; 60 * 60">
        <span title="{$sec} seconds">
          <xsl:value-of select="format-number($sec div 60, '0')"/>
          <xsl:text>m</xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="$sec &lt; 24 * 60 * 60">
        <span title="{format-number($sec div (60), '0')} minutes ({$sec} sec)">
          <xsl:value-of select="format-number($sec div (60*60), '0')"/>
          <xsl:text>h</xsl:text>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <span title="{format-number($sec div (60 * 60), '0')} hours ({$sec} sec)">
          <xsl:value-of select="format-number($sec div (60 * 60 * 24), '0')"/>
          <xsl:text>d</xsl:text>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
