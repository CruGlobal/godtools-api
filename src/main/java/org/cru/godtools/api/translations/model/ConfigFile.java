package org.cru.godtools.api.translations.model;

import com.google.common.collect.Lists;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.translations.Translation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class ConfigFile
{
	@XmlElement(name = "page")
	List<PageElement> pageElements = Lists.newArrayList();

	@XmlElement(name = "about")
	AboutElement about = new AboutElement();

	@XmlElement(name = "packagename")
	PackagenameElement packageName = new PackagenameElement();

	public static ConfigFile createConfigFile(PackageStructure packageStructure)
	{
		ConfigFile configFile = new ConfigFile();

		Document xmlPackageStructure = packageStructure.getXmlContent();

		for(Element element : XmlDocumentSearchUtilities.findElements(xmlPackageStructure, "packagename"))
		{
			configFile.packageName.setTitle(element.getTextContent());
		}
		for(Element element : XmlDocumentSearchUtilities.findElements(xmlPackageStructure, "page"))
		{
			PageElement pageElement = new PageElement();
			pageElement.setFilename(element.getAttribute("filename").replace(".xml",""));
			pageElement.setTitle(element.getTextContent());
			configFile.pageElements.add(pageElement);
		}
		for(Element element : XmlDocumentSearchUtilities.findElements(xmlPackageStructure, "about"))
		{
			configFile.about.setFilename(element.getAttribute("filename").replace(".xml",""));
			configFile.about.setTitle(element.getTextContent());
		}

		return configFile;
	}

	@XmlElement(name = "page")
	public List<PageElement> getPageSet()
	{
		return pageElements;
	}

	public void setPageSet(List<PageElement> pageElements)
	{
		this.pageElements = pageElements;
	}

	@XmlElement
	public PackagenameElement getPackageName()
	{
		return packageName;
	}

	public void setPackageName(PackagenameElement packageName)
	{
		this.packageName = packageName;
	}

	@XmlElement
	public AboutElement getAbout()
	{
		return about;
	}

	public void setAbout(AboutElement about)
	{
		this.about = about;
	}
}
