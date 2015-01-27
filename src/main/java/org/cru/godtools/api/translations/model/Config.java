package org.cru.godtools.api.translations.model;

import com.google.common.collect.Lists;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.packages.PackageStructure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Represents a config file that is the structure of a God Tools resource.
 *
 * A God Tools resource is a collection of many @see Page s, a single @see About page
 * and a single @see PackageName.  Each of these contained items is represented by a single XML element or JSON object.
 *
 * Pages are not nested inside a Pages element -- FIXME: uhh they currently are.
 *
 * Its XML representation looks like this example (some pages omitted):
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <document lang="en">
 *  <packagename>Connaître Dieu Personnellement</packagename>
 *  <page filename="cfcf98c0-2626-4b8e-b6b9-088213209a7d.xml" thumb="PageThumb_01.png">Page d'Accueil</page>
 *  <page filename="65c7d3e3-7f9c-45d6-813c-b5c57e09c6fb.xml" thumb="PageThumb_02.png">1 Dieu Vous Aime Et Désire Que Vous Le Connaissiez Personnellement.</page>
 *  <about filename="57b47e36-0697-4618-b8dc-878f45c49586.xml">A propos</about>
 *  <instructions/>
 * </document>
 */
@XmlRootElement
public class Config
{
	List<Page> pages = Lists.newArrayList();
	org.cru.godtools.api.translations.model.About about = new org.cru.godtools.api.translations.model.About();
	PackageName packageName = new PackageName();

	public static Config createConfigFile(PackageStructure packageStructure)
	{
		Config config = new Config();

		Document xmlPackageStructure = packageStructure.getXmlContent();

		for(Element element : XmlDocumentSearchUtilities.findElements(xmlPackageStructure, "packagename"))
		{
			config.packageName.setTitle(element.getTextContent());
		}
		for(Element element : XmlDocumentSearchUtilities.findElements(xmlPackageStructure, "page"))
		{
			Page page = new Page();
			page.setFilename(element.getAttribute("filename"));
			page.setTitle(element.getTextContent());
			config.pages.add(page);
		}
		for(Element element : XmlDocumentSearchUtilities.findElements(xmlPackageStructure, "about"))
		{
			config.about.setFilename(element.getAttribute("about"));
			config.about.setTitle(element.getTextContent());
		}

		return config;
	}

	@XmlElementWrapper(name = "pages")
	@XmlElement(name = "page")
	public List<Page> getPageSet()
	{
		return pages;
	}

	public void setPageSet(List<Page> pages)
	{
		this.pages = pages;
	}

	@XmlElement
	public PackageName getPackageName()
	{
		return packageName;
	}

	public void setPackageName(PackageName packageName)
	{
		this.packageName = packageName;
	}

	@XmlElement
	public org.cru.godtools.api.translations.model.About getAbout()
	{
		return about;
	}

	public void setAbout(org.cru.godtools.api.translations.model.About about)
	{
		this.about = about;
	}
}
