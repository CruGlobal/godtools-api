package org.cru.godtools.api.packages.domain;

import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PageStructure
{
	private UUID id;
	private UUID packageStructureId;
	private Document xmlContent;
	private String description;

	public void setTranslatedFields(Map<UUID, TranslationElement> mapOfTranslationElements)
	{
		for(Element translatableElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "gtapi-trx-id"))
		{
			try
			{
				if (mapOfTranslationElements.containsKey(UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"))))
				{
					translatableElement.setTextContent(mapOfTranslationElements.get(UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"))).getTranslatedText());
				}
			}
			catch(IllegalArgumentException e)
			{
				System.out.println("Invalid UUID... oh well.  Move along");
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

	public UUID getPackageStructureId()
	{
		return packageStructureId;
	}

	public void setPackageStructureId(UUID packageStructureId)
	{
		this.packageStructureId = packageStructureId;
	}

	public Document getXmlContent()
	{
		return xmlContent;
	}

	public void setXmlContent(Document xmlContent)
	{
		this.xmlContent = xmlContent;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

}
