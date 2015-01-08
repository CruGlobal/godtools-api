package org.cru.godtools.api.translations.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by ryancarlson on 11/25/14.
 */
public class Page
{
	String filename;
	String title;

	@XmlAttribute
	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}
