package org.cru.godtools.api.translations.model;

import javax.xml.bind.annotation.XmlAttribute;
import java.math.BigDecimal;

/**
 * Represents a resource element in a @see Content file that has 'meta' information about a God Tools resource.
 *
 * There are various attibutes here within that have meaning in a God Tools resource
 * *
 * Its XML representation looks like this example (some pages omitted):
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 *     <content>
 *         <resource config="2c510fda-fb80-4c22-9792-195f36232b48.xml" icon="30adeee865dd2ff568b11715e9077ffbb851bb65.png" language="fr" name="Connaître Dieu Personnellement" package="kgp" status="live" version="1.1"/>
 *     </content>
 */
public class Resource
{
	/**
	 * Name of the config file.  This is the file that iOS or Android clients will use to know the structure of the God Tools resource
	 */
	String config;
	/**
	 * Name of the icon file that is used in this resource.
	 */
	String icon;
	/**
	 * Language code of language that this resource is translated into
	 */
	String language;
	/**
	 * The name of the resource 'Knowing God Personally'
	 */
	String name;
	/**
	 * The code representing the resource 'kgp'
	 */
	String packageCode;
	/**
	 * The version number of the resource
	 */
	String version;
	/**
	 * The status of the resource, if it is live or draft.
	 */
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
