package org.cru.godtools.api.translations.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a page element of a God Tools resource.
 *
 * This class Page isn't actually a Page that's returned with content able to be rendered.  Rather it is used by @see Config to
 * build a list of XML elements or JSON objects that represent a Page in a God Tools config file.

 * Its XML representation looks like this example:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <document lang="en">
 *  <page filename="cfcf98c0-2626-4b8e-b6b9-088213209a7d.xml" thumb="PageThumb_01.png">Page d'Accueil</page>
 * </document>
 */
public class PageElement
{
	String originalFilename;
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

	public String getOriginalFilename()
	{
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename)
	{
		this.originalFilename = originalFilename;
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
