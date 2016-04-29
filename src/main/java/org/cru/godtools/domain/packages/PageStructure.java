package org.cru.godtools.domain.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.ccci.util.xml.XmlDocumentStreamConverter;
import org.cru.godtools.api.utilities.XmlUtilities;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.cru.godtools.domain.images.Image;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.xml.sax.SAXException;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PageStructure implements Serializable
{
	private static final Set<String> REMOVABLE_ATTRIBUTES = Sets.newHashSet("watermark", "tnt-trx-ref-value", "tnt-trx-translated", "translate");
	private static final String ALL_ELEMENTS = "*";

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
			element.setAttribute("backgroundimage", GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
		}

		for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
		{
			String filenameFromXml = element.getAttribute("watermark");
			element.setAttribute("watermark", GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
		}

		for(Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
		{
			String filenameFromXml = element.getTextContent();
			element.setTextContent(GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
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

	public void addXmlContent(Document addXmlContentDocument) throws IOException, TransformerException, ParserConfigurationException
	{
		Document strippedDownCopyOfXmlContent = getStrippedDownCopyOfXmlContent();
		strippedDownCopyOfXmlContent.setStrictErrorChecking(false);

		XmlUtilities.verifyDifferentXml(strippedDownCopyOfXmlContent, addXmlContentDocument);

		NodeList addXmlNodeList = addXmlContentDocument.getElementsByTagName(ALL_ELEMENTS);
		NodeList currentXmlContentNodeList = strippedDownCopyOfXmlContent.getElementsByTagName(ALL_ELEMENTS);

		List<String> currentXmlStringListOfElements = Lists.newArrayList();

		//create a string list of xml elements of the current document
		for(int i=0; i < currentXmlContentNodeList.getLength(); i++)
		{
			String nodeString = XmlUtilities.xmlDocumentOrNodeToString(new DOMSource(currentXmlContentNodeList.item(i)));
			currentXmlStringListOfElements.add(nodeString);
		}

		for(int i = 0; i < addXmlNodeList.getLength(); i++)
		{
			Node nodeToAdd = addXmlNodeList.item(i);
			Node xmlContentNode = currentXmlContentNodeList.item(i);

			if (xmlContentNode != null)
			{
				Element originalElement = (Element) xmlContentNode;
				Element addElement = (Element) nodeToAdd;

				boolean attrMatch = hasSameAttributes(originalElement, addElement);

				//if the xml attributes are not the same but the node

				//name is the same it's probably a new node
				String originalElementNodeName = originalElement.getNodeName();
				String addElementNodeName = addElement.getNodeName();
				if (!originalElementNodeName.equals(addElementNodeName) || !attrMatch)
				{
					String nodeToBeAddedString = XmlUtilities.xmlDocumentOrNodeToString(new DOMSource(nodeToAdd));

					if (!currentXmlStringListOfElements.contains(nodeToBeAddedString))
					{
						Node targetNode = nodeToAdd.cloneNode(true);
						Node nodeToBeImported = strippedDownCopyOfXmlContent.importNode(targetNode, true);
						Node previousSibling = XmlUtilities.getPreviousSiblingElement(xmlContentNode);

						if(previousSibling == null)
						{
							strippedDownCopyOfXmlContent.getDocumentElement().insertBefore(nodeToBeImported, xmlContentNode);
						}
						else
						{
							strippedDownCopyOfXmlContent.getDocumentElement().insertBefore(nodeToBeImported, previousSibling);
						}
					}
				}
			}
			else
			{
				Node targetNode = strippedDownCopyOfXmlContent.importNode(nodeToAdd, true);
				currentXmlContentNodeList.item(1).appendChild(targetNode);
			}
		}

		xmlContent = strippedDownCopyOfXmlContent;
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

			if(a1 == null ^ o1 ==null) return false;

			if (!o1.getName().equals(a1.getName()) || !o1.getValue().equals(a1.getValue()))
			{
				attrMatch = false;
				break;
			}
		}
		return attrMatch;
	}

	public void removeXmlContent(Document documentWithRemovableElements) throws XMLStreamException, IOException,
			ParserConfigurationException,SAXException,TransformerException
	{
		XmlUtilities.verifyDifferentXml(xmlContent, documentWithRemovableElements);

		ByteArrayOutputStream originalXmlByteArrayOStream = XmlDocumentStreamConverter.writeToByteArrayStream(xmlContent);
		ByteArrayOutputStream removableElementsByteArrayOStream = XmlDocumentStreamConverter.writeToByteArrayStream(documentWithRemovableElements);

		XMLEventReader originalXmlReader = getXmlEventReaderFromByteArray(originalXmlByteArrayOStream);
		XMLEventReader removableElementsXmlReader = getXmlEventReaderFromByteArray(removableElementsByteArrayOStream);

		ByteArrayOutputStream byteArrayForDocument = new ByteArrayOutputStream();

		XMLEventWriter xmlEventWriter = XMLOutputFactory.newInstance().createXMLEventWriter(byteArrayForDocument);
		List<String> xmlEventArrayList = Lists.newArrayList();

		boolean isFound = true ;
		String deleteTagName = "";

		//I'm sure there's a better way, but for now I write the results
		//to a list and in the next while loop, we check if the event (xml element)
		//exists.
		while (removableElementsXmlReader.hasNext())
		{
			XMLEvent xmlEvent = removableElementsXmlReader.nextEvent();
			xmlEventArrayList.add(xmlEvent.toString());
		}

		while (originalXmlReader.hasNext())
		{
			XMLEvent event = originalXmlReader.nextEvent();

			//Loop through the array with the event to see
			//if it exists.  If it doesn't we don't add it.
			//to the stream. Thus removing it.
			if (xmlEventArrayList.contains(event.toString()) )
			{
				if(isFound)
				{
					if(!event.isStartDocument())
					{
						//The API tries to write the string, " ENDDOCUMENT" is at the end
						//of the stream resulting in an invalid xml doc
						//This condition keeps it out.
							xmlEventWriter.add(event);
					}
				}
				//Once the closing tag for the "removed" tag is skipped
				//we reset the variables used to skip over tags
				else if(event.isEndElement() &&
							deleteTagName.equals(event.asEndElement().getName().toString()))
				{
					isFound = true;
					deleteTagName = "";
				}
			}
			else
			{
				//This condition is to ensure that the
				// intended "removed" parent tag and all of it's children
				// are ignored.
				if(event.isStartElement())
				{
					deleteTagName = event.asStartElement().getName().toString();
					isFound = false;
				}
			}
		}

		xmlEventWriter.flush();
		xmlEventWriter.close();

		InputStream inputStream = new ByteArrayInputStream( byteArrayForDocument.toByteArray());
		Document document = XmlDocumentStreamConverter.readFromInputStream(inputStream);
		setXmlContent(document);

		originalXmlByteArrayOStream.close();
		removableElementsByteArrayOStream.close();
		byteArrayForDocument.close();
	}

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

	private XMLEventReader getXmlEventReaderFromByteArray(ByteArrayOutputStream byteArrayOutputStream) throws XMLStreamException
	{
		InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		return xmlInputFactory.createXMLEventReader(inputStream);
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
