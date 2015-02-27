package org.cru.godtools.api.translations.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents the package name element of a God Tools resource.
 *
 * This class PackageName isn't could be used to render content, but it's not likely that it would b/c its sibling elements
 * @see PageElement aren't build that way.
 *
 * It is currently used by @see Config to build a list of XML elements or JSON objects that
 * represent a Page in a God Tools config file.
 *
 * Its XML representation looks like this example:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <document lang="en">
 *  <packagename>Connaître Dieu Personnellement</packagename>
 * </document>
 */
public class PackagenameElement
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
