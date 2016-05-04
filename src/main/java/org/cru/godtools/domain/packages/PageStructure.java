package org.cru.godtools.domain.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.BadRequestException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.xerces.dom.DeferredTextImpl;
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
import java.util.Iterator;
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
			else if(!nodesMatch(baseNextSibling, updatedNextSibling))
			{
				logger.info(String.format("Adding %s before %s", updatedNextSibling.getNodeName(), baseNextSibling.getNodeName()));

				Node clonedNode = updatedNextSibling.cloneNode(true);
				this.xmlContent.importNode(clonedNode, true);
				baseContentElement.insertBefore(clonedNode, baseNextSibling);

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

	private boolean nodesMatch(Node baseNode, Node updatedNode)
	{
		if(!(baseNode instanceof Element) || !(updatedNode instanceof Element)) return true;
		if(!baseNode.getNodeName().equals(updatedNode.getNodeName())) return false;

		NamedNodeMap originalNamedNodeMap = baseNode.getAttributes();
		NamedNodeMap additionNamedNodeMap = updatedNode.getAttributes();

		if(originalNamedNodeMap.getLength() != additionNamedNodeMap.getLength()) return false;

		for (int n = 0; n < additionNamedNodeMap.getLength(); n++)
		{
			Attr a1 = (Attr) additionNamedNodeMap.item(n);
			Attr o1 = (Attr) originalNamedNodeMap.item(n);

			if(a1 == null ^ o1 ==null) return false;

			if (!o1.getName().equals(a1.getName()) || !o1.getValue().equals(a1.getValue()))
			{
				return false;
			}
		}

		return true;
	}

	public void removeXmlContent(Document documentWithRemovableElements) throws XMLStreamException, IOException,
			ParserConfigurationException,SAXException,TransformerException
	{
		Document xmlContent = getStrippedDownCopyOfXmlContent();
		XmlUtilities.verifyDifferentXml(xmlContent, documentWithRemovableElements);

		ByteArrayOutputStream originalXmlByteArrayOStream = XmlDocumentStreamConverter.writeToByteArrayStream(xmlContent);
		ByteArrayOutputStream removableElementsByteArrayOStream = XmlDocumentStreamConverter.writeToByteArrayStream(documentWithRemovableElements);

		XMLEventReader originalXmlReader = getXmlEventReaderFromByteArray(originalXmlByteArrayOStream);
		XMLEventReader removableElementsXmlReader = getXmlEventReaderFromByteArray(removableElementsByteArrayOStream);

		ByteArrayOutputStream byteArrayForDocument = new ByteArrayOutputStream();

		XMLEventWriter xmlEventWriter = XMLOutputFactory.newInstance().createXMLEventWriter(byteArrayForDocument);

		List<XMLEvent> updatedXMLEvents = Lists.newArrayList();
		List<XMLEvent> originalXMLEvents = Lists.newArrayList();

		//I'm sure there's a better way, but for now I write the results
		//to a list and in the next while loop, we check if the event (xml element)
		//exists.
		while (removableElementsXmlReader.hasNext())
		{
			XMLEvent xmlEvent = removableElementsXmlReader.nextEvent();
			updatedXMLEvents.add(xmlEvent);
		}

		while (originalXmlReader.hasNext())
		{
			XMLEvent event = originalXmlReader.nextEvent();
			originalXMLEvents.add(event);
		}

		Iterator<XMLEvent> originalIterator = originalXMLEvents.iterator();
		Iterator<XMLEvent> updatedIterator = updatedXMLEvents.iterator();

		XMLEvent updatedEvent = nextEventSkippingWhitespace(updatedIterator, null);
		XMLEvent originalEvent = nextEventSkippingWhitespace(originalIterator, xmlEventWriter);

		while(updatedEvent != null)
		{
			// same element name and same attributes.. move along
			if(updatedEvent.getEventType() == originalEvent.getEventType() &&
				getName(updatedEvent).equals(getName(originalEvent))/* && hasSameAttributes(null, null)*/)
			{
				xmlEventWriter.add(originalEvent);

				updatedEvent = nextEventSkippingWhitespace(updatedIterator, null);
				originalEvent = nextEventSkippingWhitespace(originalIterator, xmlEventWriter);

				continue;
			}

			// hold a reference to the start tag that's missing
			XMLEvent removedEvent = originalEvent;

			// something is amiss, so an element has been removed.
			while(!originalEvent.isEndElement() ||
					!(getName(originalEvent).equals(getName(removedEvent))))
			{
				originalEvent = originalIterator.next();
			}

			// one more forward so we get off the end tag of removed element and on to
			// start tag of the next element
			originalEvent = nextEventSkippingWhitespace(originalIterator, xmlEventWriter);
		}

		while(originalEvent != null)
		{
			xmlEventWriter.add(originalEvent);
			originalEvent = nextEventSkippingWhitespace(originalIterator, xmlEventWriter);
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

	private QName getName(XMLEvent event)
	{
		if(event instanceof StartElement)
		{
			return ((StartElement) event).getName();
		}
		else if(event instanceof EndElement)
		{
			return ((EndElement) event).getName();
		}
		else return QName.valueOf(event.toString());
	}

	private XMLEvent nextEventSkippingWhitespace(Iterator<XMLEvent> iterator, XMLEventWriter xmlEventWriter) throws XMLStreamException
	{
		while(iterator.hasNext())
		{
			XMLEvent nextEvent = iterator.next();
			if(nextEvent.isStartElement() || nextEvent.isEndElement()) return nextEvent;

			if(xmlEventWriter != null)
			{
				xmlEventWriter.add(nextEvent);
			}
		}

		return null;
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
