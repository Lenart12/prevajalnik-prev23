<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="memory">
  <html>
    <style>
      table, tr, td {
      text-align: center;
      vertical-align: top;
      }
    </style>
    <body>
      <table>
	<xsl:apply-templates select="node"/>
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template match="node">
  <td>
    <table width="100%">
      <tr bgcolor="FFEE00">
	<td colspan="1000">
	  <nobr>
	    <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	    (<xsl:value-of select="@id"/>)
	    <font style="font-family:arial black">
	      <xsl:value-of select="@label"/>
	    </font>
	    <xsl:if test="@spec!=''">
	      <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	      <font style="font-family:helvetica">
		<xsl:value-of select="@spec"/>
	      </font>
	    </xsl:if>
	    <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	  </nobr>
	  <br/>
	  <xsl:if test="location">
	    <nobr>
	      <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	      <xsl:apply-templates select="location"/>
	      <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	    </nobr>
	  </xsl:if>
	  <xsl:if test="@lexeme!=''">
	    <br/>
	    <nobr>
	      <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	      <font style="font-family:courier new">
		<xsl:value-of select="@lexeme"/>
	      </font>
	      <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	    </nobr>
	  </xsl:if>
	  <br/>
	  <table width="100%">
	    <xsl:apply-templates select="declaredAt"/>
	    <xsl:apply-templates select="declaresType"/>
	    <xsl:apply-templates select="isType"/>
	    <xsl:apply-templates select="ofType"/>
	    <xsl:apply-templates select="lvalue"/>
	    <xsl:apply-templates select="frame"/>
	    <xsl:apply-templates select="access"/>
	  </table>
	</td>
      </tr>
      <tr>
	<xsl:apply-templates select="node"/>
      </tr>
    </table>
  </td>
</xsl:template>

<xsl:template match="location">
  <nobr>
    <font style="font-family:helvetica">
      <xsl:value-of select="@loc"/>
    </font>
  </nobr>
</xsl:template>

<xsl:template match="declaredAt">
  <tr bgcolor="FFCF00">
    <td>
      <nobr>
	<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	(<xsl:value-of select="@idx"/>)
	[<xsl:value-of select="@location"/>]
	<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
      </nobr>
    </td>
  </tr>	
</xsl:template>

<xsl:template match="declaresType">
  <tr>
    <xsl:apply-templates select="semtype"/>
  </tr>
</xsl:template>

<xsl:template match="isType">
  <tr>
    <xsl:apply-templates select="semtype"/>
  </tr>
</xsl:template>

<xsl:template match="ofType">
  <tr>
    <xsl:apply-templates select="semtype"/>
  </tr>
</xsl:template>

<xsl:template match="semtype">
  <td>
    <table width="100%">
      <tr>
	<td bgcolor="EEBE00" colspan="1000">
	  <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	  <xsl:value-of select="@type"/>
	  <xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	</td>
      </tr>
      <tr>
	<xsl:apply-templates select="semtype"/>
      </tr>
    </table>
  </td>
</xsl:template>

<xsl:template match="lvalue">
  <tr>
    <td bgcolor="DDAC00">LVALUE</td>
  </tr>
</xsl:template>

<xsl:template match="frame">
  <tr>
    <td>
      <table width="100%" bgcolor="EECF00">
	<tr>
	  <td>
	    FRAME
	  </td>
	</tr>
	<tr>
	  <td>
	    <nobr>
	      label=<font style="font-family:courier new"><xsl:value-of select="@label"/></font>
	      depth=<xsl:value-of select="@depth"/> 
	      size=<xsl:value-of select="@size"/> 
	      locs=<xsl:value-of select="@locssize"/>
	      args=<xsl:value-of select="@argssize"/>
	      FP=<xsl:value-of select="@FP"/>
	      RV=<xsl:value-of select="@RV"/>
	    </nobr>
	  </td>
	</tr>
      </table>
    </td>
  </tr>
</xsl:template>

<xsl:template match="access">
  <tr>
    <td>
      <table width="100%" bgcolor="EECF00">
	<tr>
	  <td>
	    ACCESS
	  </td>
	</tr>
	<tr>
	  <td>
	    <nobr>
	      size=<xsl:value-of select="@size"/> 
	      <xsl:if test="@label!=''">
		label=<font style="font-family:courier new"><xsl:value-of select="@label"/></font>
	      </xsl:if>
	      <xsl:if test="@init!=''">
		init=<font style="font-family:courier new"><xsl:value-of select="@init"/></font>
	      </xsl:if>
	      <xsl:if test="@offset!=''">
		offset=<xsl:value-of select="@offset"/>
	      </xsl:if>
	      <xsl:if test="@depth!=''">
		depth=<xsl:value-of select="@depth"/>
	      </xsl:if>
	    </nobr>
	  </td>
	</tr>
      </table>
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
