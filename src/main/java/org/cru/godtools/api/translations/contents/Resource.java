package org.cru.godtools.api.translations.contents;

import javax.xml.bind.annotation.XmlAttribute;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 11/24/14.
 */
public class Resource
{
	String config;
	String icon;
	String language;
	String name;
	String packageCode;
	String version;
	String status;

	@XmlAttribute
	public String getConfig()
	{
		return config;
	}

	public void setConfig(String config)
	{
		this.config = config;
	}

	@XmlAttribute
	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	@XmlAttribute
	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	@XmlAttribute
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	@XmlAttribute(name = "package")
	public String getPackageCode()
	{
		return packageCode;
	}

	public void setPackageCode(String packageCode)
	{
		this.packageCode = packageCode;
	}

	@XmlAttribute
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@XmlAttribute
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}
