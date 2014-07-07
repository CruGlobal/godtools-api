package org.cru.godtools.api.packages.domain;

import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PackageStructure
{
	private UUID id;
	private UUID packageId;
	private Integer versionNumber;
	private Document xmlContent;

	public void setTranslatedFields(Map<UUID, TranslationElement> translationElementMap)
	{
		for(Element translatableElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "gtapi-trx-id"))
		{
			try
			{
				if (translationElementMap.containsKey(UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"))))
				{
					translatableElement.setTextContent(translationElementMap.get(UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"))).getTranslatedText());
				}
			}
			catch(IllegalArgumentException e)
			{
				System.out.println("Invalid UUID... oh well.  Move along");
			}
		}
	}

	public void replacePageNamesWithPageHashes(Map<String, PageStructure> pageStructures)
	{
		for(Element pageElement : XmlDocumentSearchUtilities.findElements(getXmlContent(), "page"))
		{
			String filenameFromXml = pageElement.getAttribute("filename");
			pageElement.setAttribute("filename", ShaGenerator.calculateHash(pageStructures.get(filenameFromXml).getXmlContent()) + ".xml");
		}

		for(Element pageElement : XmlDocumentSearchUtilities.findElements(getXmlContent(), "about"))
		{
			String filenameFromXml = pageElement.getAttribute("filename");
			pageElement.setAttribute("filename", ShaGenerator.calculateHash(pageStructures.get(filenameFromXml).getXmlContent()) + ".xml");
		}
	}

	public void replaceImageNamesWithImageHashes(Map<String, Image> images)
	{
		for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "thumb"))
		{
			try
			{
				String filenameFromXml = pageElement.getAttribute("thumb");
				pageElement.setAttribute("thumb", ShaGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
			}
			catch (NullPointerException npe)
			{
				//missing image, life goes on...
			}
		}

		for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "about", "thumb"))
		{
			try
			{
				String filenameFromXml = pageElement.getAttribute("thumb");
				pageElement.setAttribute("thumb", ShaGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
			}
			catch(NullPointerException npe)
			{
				//missing image, life goes on...
			}
		}

	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getPackageId()
	{
		return packageId;
	}

	public void setPackageId(UUID packageId)
	{
		this.packageId = packageId;
	}

	public Integer getVersionNumber()
	{
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber)
	{
		this.versionNumber = versionNumber;
	}

	public Document getXmlContent()
	{
		return xmlContent;
	}

	public void setXmlContent(Document xmlContent)
	{
		this.xmlContent = xmlContent;
	}
}
