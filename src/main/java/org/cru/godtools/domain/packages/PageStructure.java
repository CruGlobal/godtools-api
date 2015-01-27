package org.cru.godtools.domain.packages;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.cru.godtools.domain.images.Image;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PageStructure implements Serializable
{
	private static final Set<String> REMOVABLE_ATTRIBUTES = Sets.newHashSet("watermark", "tnt-trx-ref-value", "tnt-trx-translated", "translate");

	private UUID id;
	private UUID translationId;
	private Document xmlContent;
	private String description;
	private String filename;

	private BigDecimal percentCompleted;
	private Integer stringCount;
	private Integer wordCount;
	private DateTime lastUpdated;

	private final Logger logger = Logger.getLogger(PackageStructure.class);

	public static PageStructure copyOf(PageStructure pageStructure)
	{
		PageStructure pageStructureCopy = new PageStructure();
		pageStructureCopy.setId(pageStructure.getId());
		pageStructureCopy.setTranslationId(pageStructure.getTranslationId());
		pageStructureCopy.setXmlContent(pageStructure.getXmlContent());
		pageStructureCopy.setDescription(pageStructure.getDescription());
		pageStructureCopy.setFilename(pageStructure.getFilename());
		pageStructureCopy.setPercentCompleted(pageStructure.getPercentCompleted());
		pageStructureCopy.setStringCount(pageStructure.getStringCount());
		pageStructureCopy.setWordCount(pageStructure.getWordCount());
		pageStructureCopy.setLastUpdated(pageStructure.getLastUpdated());

		return pageStructureCopy;
	}

	public void setTranslatedFields(Map<UUID, TranslationElement> mapOfTranslationElements)
	{
		for(Element translatableElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "gtapi-trx-id"))
		{
			try
			{
				UUID translationElementId = UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"));

				if (mapOfTranslationElements.containsKey(UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"))))
				{
					String translatedText = mapOfTranslationElements.get(translationElementId).getTranslatedText();
					String elementType = translatableElement.getTagName();

					logger.debug(String.format("Setting translation element: %s with ID: %s to value: %s", elementType, translationElementId.toString(), translatedText));
					translatableElement.setTextContent(mapOfTranslationElements.get(UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"))).getTranslatedText());
				}
			}
			catch(IllegalArgumentException e)
			{
				logger.warn("Invalid UUID... oh well.  Move along");
			}
		}
	}

	public void replaceImageNamesWithImageHashes(Map<String, Image> images)
	{
//		for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "backgroundimage"))
//		{
//			String filenameFromXml = element.getAttribute("backgroundimage");
//			element.setAttribute("backgroundimage", GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
//		}
//
//		for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
//		{
//			String filenameFromXml = element.getAttribute("watermark");
//			element.setAttribute("watermark", GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
//		}
//
//		for(Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
//		{
//			String filenameFromXml = element.getTextContent();
//			element.setTextContent(GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
//		}
	}

	public static  Map<String, PageStructure> createMapOfPageStructures(List<PageStructure> pageStructureList)
	{
		Map<String, PageStructure> pageStructureMap = Maps.newHashMap();

		for(PageStructure pageStructure : pageStructureList)
		{
			pageStructureMap.put(pageStructure.getFilename(), pageStructure);
		}

		return pageStructureMap;
	}

	public void updateCachedStatus(BigDecimal percentCompleted, Integer wordCount, Integer stringCount, DateTime currentTime)
	{
		setPercentCompleted(percentCompleted);
		setWordCount(wordCount);
		setStringCount(stringCount);
		setLastUpdated(currentTime);
	}

	public void mergeXmlContent(Document updatedPageLayout)
	{
		List<Element> updatedLayoutElementsWithGtapiId = XmlDocumentSearchUtilities.findElementsWithAttribute(updatedPageLayout, "gtapi-trx-id");

		for(String attributeName : REMOVABLE_ATTRIBUTES)
		{
			if(xmlContent.getDocumentElement().getAttribute(attributeName) != null)
			{
				updatedPageLayout.getDocumentElement().setAttribute(attributeName, xmlContent.getDocumentElement().getAttribute(attributeName));
			}

			for(Element elementWithRemovableAttribute : XmlDocumentSearchUtilities.findElementsWithAttribute(xmlContent, attributeName))
			{
				for(Element element : updatedLayoutElementsWithGtapiId)
				{
					if(element.getAttribute("gtapi-trx-id").equals(elementWithRemovableAttribute.getAttribute("gtapi-trx-id")))
					{
						element.setAttribute(attributeName, elementWithRemovableAttribute.getAttribute(attributeName));
					}
				}
			}
		}

		xmlContent = updatedPageLayout;
	}

	public Document getXmlContent(boolean strippedDown)
	{
		if(strippedDown)
		{
			for(String attributeName : REMOVABLE_ATTRIBUTES)
			{
				if(xmlContent.getDocumentElement().getAttribute(attributeName) != null)
				{
					xmlContent.getDocumentElement().removeAttribute(attributeName);
				}
				for(Element elementWithRemovableAttribute : XmlDocumentSearchUtilities.findElementsWithAttribute(xmlContent, attributeName))
				{
					if(elementWithRemovableAttribute.getAttribute("gtapi-trx-id") != null)
					{
						elementWithRemovableAttribute.removeAttribute(attributeName);
					}
				}
			}
		}
		return xmlContent;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getTranslationId()
	{
		return translationId;
	}

	public void setTranslationId(UUID translationId)
	{
		this.translationId = translationId;
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

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public BigDecimal getPercentCompleted()
	{
		return percentCompleted;
	}

	public void setPercentCompleted(BigDecimal percentCompleted)
	{
		this.percentCompleted = percentCompleted;
	}

	public Integer getStringCount()
	{
		return stringCount;
	}

	public void setStringCount(Integer stringCount)
	{
		this.stringCount = stringCount;
	}

	public Integer getWordCount()
	{
		return wordCount;
	}

	public void setWordCount(Integer wordCount)
	{
		this.wordCount = wordCount;
	}

	public DateTime getLastUpdated()
	{
		return lastUpdated;
	}

	public void setLastUpdated(DateTime lastUpdated)
	{
		this.lastUpdated = lastUpdated;
	}
}
