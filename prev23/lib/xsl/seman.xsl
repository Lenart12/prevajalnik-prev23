<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="seman">
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

</xsl:stylesheet>
