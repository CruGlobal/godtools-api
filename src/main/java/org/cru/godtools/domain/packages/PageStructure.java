package org.cru.godtools.domain.packages;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import javax.ws.rs.BadRequestException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.api.utilities.XmlUtilities;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.cru.godtools.domain.images.Image;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PageStructure implements Serializable
{
	private static final Set<String> REMOVABLE_ATTRIBUTES = Sets.newHashSet("tnt-trx-ref-value", "tnt-trx-translated", "translate");

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
		for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "backgroundimage"))
		{
			String filenameFromXml = element.getAttribute("backgroundimage");
			Image image = images.get(filenameFromXml);

			element.setAttribute("backgroundimage", image != null ?
					(GuavaHashGenerator.calculateHash(image.getImageContent()) + ".png") : "<image not found>");
		}

		for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
		{
			String filenameFromXml = element.getAttribute("watermark");
			Image image = images.get(filenameFromXml);

			element.setAttribute("watermark", image != null ?
					(GuavaHashGenerator.calculateHash(image.getImageContent()) + ".png") : "<image not found>");
		}

		for(Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
		{
			String filenameFromXml = element.getTextContent();
			Image image = images.get(filenameFromXml);

			element.setTextContent(
					image != null ?
							(GuavaHashGenerator.calculateHash(image.getImageContent()) + ".png") : "<image not found>");
		}
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

	public void addXmlContent(Document updatedContent) throws IOException, TransformerException, ParserConfigurationException
	{
		Document baseContent = getStrippedDownCopyOfXmlContent();
		baseContent.setStrictErrorChecking(false);

		XmlUtilities.verifyDifferentXml(baseContent, updatedContent);

		addXmlContent(baseContent.getDocumentElement(), updatedContent.getDocumentElement());

		xmlContent = baseContent;
	}

	private void addXmlContent(Node baseContentElement, Node updatedContentElement)
	{
		Node updatedNextSibling = updatedContentElement;
		Node baseNextSibling = baseContentElement;

		logger.info(String.format("Checking: %s for new elements", baseContentElement.getNodeName()));

		while(updatedNextSibling != null)
		{
			if(baseNextSibling == null)
			{
				logger.info(String.format("Adding %s to %s", updatedNextSibling.getNodeName(), baseContentElement.getParentNode().getNodeName()));

				Node clonedNode = updatedNextSibling.cloneNode(true);
				this.xmlContent.importNode(clonedNode, true);
				baseContentElement.getParentNode().appendChild(clonedNode);
			}
			else if(!XmlUtilities.nodesMatch(baseNextSibling, updatedNextSibling))
			{
				logger.info(String.format("Adding %s before %s", updatedNextSibling.getNodeName(), baseNextSibling.getNodeName()));

				Node clonedNode = updatedNextSibling.cloneNode(true);
				this.xmlContent.importNode(clonedNode, true);
				baseNextSibling.getParentNode().insertBefore(clonedNode, baseNextSibling);

				updatedNextSibling = XmlUtilities.getNextSiblingElement(updatedNextSibling);
				continue;
			}
			else
			{
				if (XmlUtilities.hasChildNodes(baseNextSibling))
				{
					if (!XmlUtilities.hasChildNodes(updatedNextSibling))
					{
						throw new BadRequestException(String.format("Child nodes missing from updated %s that base %s has", updatedNextSibling.getNodeName(), baseNextSibling.getNodeName()));
					}
					logger.info(String.format("Checking children of: %s for new elements", baseNextSibling.getNodeName()));
					addXmlContent(XmlUtilities.getFirstChild(baseNextSibling), XmlUtilities.getFirstChild(updatedNextSibling));
				}
			}

			updatedNextSibling = XmlUtilities.getNextSiblingElement(updatedNextSibling);
			baseNextSibling = XmlUtilities.getNextSiblingElement(baseNextSibling);
		}

		return;
	}

	private boolean hasSameAttributes(Element oElement, Element aElement)
	{
		NamedNodeMap originalNamedNodeMap = oElement.getAttributes();
		NamedNodeMap additionNamedNodeMap = aElement.getAttributes();

		boolean attrMatch = true;

		for (int n = 0; n < additionNamedNodeMap.getLength(); n++)
        {
            Attr a1 = (Attr) additionNamedNodeMap.item(n);
            Attr o1 = (Attr) originalNamedNodeMap.item(n);

            if (!o1.getName().equals(a1.getName()) || !o1.getValue().equals(a1.getValue()))
            {
                attrMatch = false;
                break;
            }
        }
		return attrMatch;
	}

	public void removeXmlContent(Document updatedContent) throws XMLStreamException, IOException,
			ParserConfigurationException,SAXException,TransformerException
	{
		Document baseContent = getStrippedDownCopyOfXmlContent();
		XmlUtilities.verifyDifferentXml(baseContent, updatedContent);

		removeXmlContent(baseContent.getDocumentElement(), updatedContent.getDocumentElement());

		xmlContent = baseContent;
	}

	private void removeXmlContent(Node baseContentElement, Node updatedContentElement)
	{
		Node updatedNextSibling = updatedContentElement;
		Node baseNextSibling = baseContentElement;

		logger.info(String.format("Checking: %s for removed elements", baseContentElement.getNodeName()));

		while(baseNextSibling != null)
		{
			if(updatedNextSibling == null || !XmlUtilities.nodesMatch(baseNextSibling, updatedNextSibling))
			{
				logger.info(String.format("Removing %s from %s", baseNextSibling.getNodeName(), baseNextSibling.getParentNode().getNodeName()));

				Node nextSiblingOfNodeToBeRemoved = XmlUtilities.getNextSiblingElement(baseNextSibling);
				baseNextSibling.getParentNode().removeChild(baseNextSibling);

				baseNextSibling = nextSiblingOfNodeToBeRemoved;
				continue;
			}
			else
			{
				if (XmlUtilities.hasChildNodes(baseNextSibling))
				{
					if (!XmlUtilities.hasChildNodes(updatedNextSibling))
					{
						Iterator<Node> childNodeListIterator = XmlUtilities.getChildNodes(baseNextSibling).iterator();

						while(childNodeListIterator.hasNext())
						{
							Node childNode = childNodeListIterator.next();
							logger.info(String.format("Removing %s from %s", childNode.getNodeName(), baseNextSibling.getNodeName()));
							baseNextSibling.removeChild(childNode);
						}
					}
					else
					{
						logger.info(String.format("Checking children of: %s for removed elements", baseNextSibling.getNodeName()));
						removeXmlContent(XmlUtilities.getFirstChild(baseNextSibling), XmlUtilities.getFirstChild(updatedNextSibling));
					}
				}
			}

			updatedNextSibling = XmlUtilities.getNextSiblingElement(updatedNextSibling);
			baseNextSibling = XmlUtilities.getNextSiblingElement(baseNextSibling);
		}

		return;
		
	}

	public void updateXmlContentAttributes(Document updateXmlDocument) throws TransformerException, IOException
	{
		XmlUtilities.verifyDifferentXml(xmlContent, updateXmlDocument);

		String all_elements = "*";

		NodeList addXmlNodeList = updateXmlDocument.getElementsByTagName(all_elements);
		NodeList currentXmlContentNodeList = xmlContent.getElementsByTagName(all_elements);

		for(int i = 0; i < addXmlNodeList.getLength(); i++)
		{
			Node xmlContentNode = currentXmlContentNodeList.item(i);
			Node nodeWithNewAttributeValues =  addXmlNodeList.item(i);

			//The attributes are looped through by the node map
			NamedNodeMap originalNamedNodeMap = xmlContentNode.getAttributes();
			NamedNodeMap additionNamedNodeMap = nodeWithNewAttributeValues.getAttributes();

			for (int n = 0; n < additionNamedNodeMap.getLength(); n++)
			{
				Attr currentAttributes = (Attr) additionNamedNodeMap.item(n);
				Attr newAttributes = (Attr) originalNamedNodeMap.item(n);

				//if the attribute names are the same and the values
				//are different, the value is updated.
				if (newAttributes.getName().equals(currentAttributes.getName())
						&& !newAttributes.getValue().equals(currentAttributes.getValue()))
				{
					Node nodeToBeSet = xmlContentNode
							.getAttributes()
							.getNamedItem(newAttributes.getName());

					nodeToBeSet.setNodeValue(currentAttributes.getValue());
				}
			}
		}
	}
	
	@JsonIgnore
	public Document getStrippedDownCopyOfXmlContent() throws ParserConfigurationException
	{
		Document xmlDocumentCopy = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Node copiedRoot = xmlDocumentCopy.importNode(xmlContent.getDocumentElement(), true);
		xmlDocumentCopy.appendChild(copiedRoot);

		for(String attributeName : REMOVABLE_ATTRIBUTES)
		{
			if(xmlDocumentCopy.getDocumentElement().getAttribute(attributeName) != null)
			{
				xmlDocumentCopy.getDocumentElement().removeAttribute(attributeName);
			}
			for(Element elementWithRemovableAttribute : XmlDocumentSearchUtilities.findElementsWithAttribute(xmlDocumentCopy, attributeName))
			{
				if(elementWithRemovableAttribute.getAttribute("gtapi-trx-id") != null)
				{
					elementWithRemovableAttribute.removeAttribute(attributeName);
				}
			}
		}
		return xmlDocumentCopy;
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

	@JsonIgnore
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
