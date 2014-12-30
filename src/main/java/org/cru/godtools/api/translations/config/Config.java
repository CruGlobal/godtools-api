package org.cru.godtools.api.translations.config;

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
 * Created by ryancarlson on 11/25/14.
 */
@XmlRootElement
public class Config
{
	List<Page> pages = Lists.newArrayList();
	About about = new About();
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
	public About getAbout()
	{
		return about;
	}

	public void setAbout(About about)
	{
		this.about = about;
	}
}
