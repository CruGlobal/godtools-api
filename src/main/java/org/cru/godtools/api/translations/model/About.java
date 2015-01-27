package org.cru.godtools.api.translations.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents the "About" page element of a God Tools resource.
 *
 * This class About isn't actually a something that's returned with content able to be rendered.
 * Rather it is used by @see Config to build a list of XML elements or JSON objects
 * that represent the "About" page in a God Tools config file.
 *
 * Its XML representation looks like this example:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <document lang="en">
 *  <about filename="57b47e36-0697-4618-b8dc-878f45c49586.xml">A propos</about>
 * </document>
 */
public class About
{
	String filename;
	String title;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@XmlAttribute
	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}
}
