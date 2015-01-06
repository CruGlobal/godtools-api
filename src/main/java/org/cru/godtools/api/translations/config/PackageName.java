package org.cru.godtools.api.translations.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by ryancarlson on 11/25/14.
 */
public class PackageName
{
	String title;

	@XmlAttribute
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}
