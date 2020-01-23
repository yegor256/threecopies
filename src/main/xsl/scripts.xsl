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
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/layout.xsl"/>
  <xsl:include href="/xsl/templates.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>scripts</xsl:text>
    </title>
    <xsl:apply-templates select="." mode="js"/>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <p>
      <a href="/script">Create new script</a>
    </p>
    <xsl:apply-templates select="scripts"/>
  </xsl:template>
  <xsl:template match="scripts[not(script)]">
    <p>
      <xsl:text>You don't have any scripts yet.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="scripts[script]">
    <form id="form" style="display:none" action="/pay" method="post">
      <input name="cents" id="cents" type="hidden"/>
      <input name="token" id="token" type="hidden"/>
      <input name="email" id="email" type="hidden"/>
      <input name="script" id="script" type="hidden"/>
      <input type="submit"/>
    </form>
    <p>
      <xsl:text>There </xsl:text>
      <xsl:choose>
        <xsl:when test="count(script) = 1">
          <xsl:text>is one script</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text> are </xsl:text>
          <xsl:value-of select="count(script)"/>
          <xsl:text> scripts</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>:</xsl:text>
    </p>
    <xsl:apply-templates select="script"/>
  </xsl:template>
  <xsl:template match="script">
    <p>
      <strong>
        <xsl:value-of select="name"/>
      </strong>
      <xsl:if test="used &gt; paid">
        <div>
          <span class="firebrick">
            <xsl:text>You are out of funds</xsl:text>
          </span>
        </div>
      </xsl:if>
      <span style="display:block;">
        <span class="item">
          <xsl:text>Hour</xsl:text>
          <xsl:text>: </xsl:text>
          <xsl:call-template name="when">
            <xsl:with-param name="script" select="name"/>
            <xsl:with-param name="time" select="hour"/>
          </xsl:call-template>
        </span>
        <span class="item">
          <xsl:text>Day: </xsl:text>
          <xsl:call-template name="when">
            <xsl:with-param name="script" select="name"/>
            <xsl:with-param name="time" select="day"/>
          </xsl:call-template>
        </span>
        <span class="item">
          <xsl:text>Week: </xsl:text>
          <xsl:call-template name="when">
            <xsl:with-param name="script" select="name"/>
            <xsl:with-param name="time" select="week"/>
          </xsl:call-template>
        </span>
      </span>
      <span style="display:block;">
        <span class="item">
          <xsl:text>Used: </xsl:text>
          <span>
            <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="used &gt; paid">
                  <xsl:text>firebrick</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>seagreen</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="format-number(used div (60 * 60), '0')"/>
            <xsl:text>s</xsl:text>
          </span>
        </span>
        <span class="item">
          <xsl:text>Paid: </xsl:text>
          <xsl:value-of select="format-number(paid div (60 * 60), '0')"/>
          <xsl:text>s</xsl:text>
        </span>
      </span>
      <span style="display:block;" class="smaller">
        <span class="item">
          <a href="/script?name={name}" title="Edit this script">
            <xsl:text>Edit</xsl:text>
          </a>
        </span>
        <span class="item">
          <a href="#" class="pay" data-name="{name}" title="Add funds for this script">
            <xsl:text>Add funds</xsl:text>
          </a>
        </span>
        <span class="item">
          <a href="/flush?name={name}" title="Reset the counter and run the script right now!">
            <xsl:text>Flush</xsl:text>
          </a>
        </span>
        <span class="item">
          <a href="/delete?name={name}" onclick="return confirm('Are you sure?');">
            <xsl:text>Delete</xsl:text>
          </a>
        </span>
      </span>
    </p>
  </xsl:template>
  <xsl:template name="when">
    <xsl:param name="script"/>
    <xsl:param name="time"/>
    <xsl:choose>
      <xsl:when test="$time = 0">
        <span class="firebrick">
          <xsl:text>never</xsl:text>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <a href="/log-link?script={$script}&amp;time={$time}">
          <xsl:call-template name="sec">
            <xsl:with-param name="sec" select="(/page/epoch - $time) div 1000"/>
          </xsl:call-template>
          <xsl:text> ago</xsl:text>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="page" mode="js">
    <xsl:element name="script">
      <xsl:attribute name="src">
        <xsl:text>https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js</xsl:text>
      </xsl:attribute>
    </xsl:element>
    <xsl:element name="script">
      <xsl:attribute name="src">
        <xsl:text>https://checkout.stripe.com/checkout.js</xsl:text>
      </xsl:attribute>
    </xsl:element>
    <xsl:element name="script">
      <xsl:attribute name="type">
        <xsl:text>text/javascript</xsl:text>
      </xsl:attribute>
      <xsl:text>var stripe_key='</xsl:text>
      <xsl:value-of select="stripe_key"/>
      <xsl:text>';</xsl:text>
      <xsl:text>var stripe_cents=</xsl:text>
      <xsl:value-of select="stripe_cents"/>
      <xsl:text>;</xsl:text>
    </xsl:element>
    <xsl:element name="script">
      <xsl:attribute name="type">
        <xsl:text>text/javascript</xsl:text>
      </xsl:attribute>
      <xsl:text>
        // <![CDATA[
        $(function() {
          var handler = StripeCheckout.configure({
            key: stripe_key,
            image: '/images/logo.png',
            token: function (token) {
              $('#token').val(token.id);
              $('#email').val(token.email);
              $('#form').submit();
            }
          });
          $('a.pay').on('click', function (e) {
            var script = $(this).attr('data-name');
            $('#script').val(script);
            $('#cents').val(stripe_cents);
            handler.open({
              name: 'Add funds to the script',
              description: script,
              amount: stripe_cents
            });
            e.preventDefault();
          });
          $(window).on('popstate', function () {
            handler.close();
          });
        });
        // ]]>
      </xsl:text>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
