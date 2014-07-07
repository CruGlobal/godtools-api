package org.cru.godtools.migration;

import com.google.common.collect.Maps;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class TranslatableElements
{
	private Document baseTranslationXmlContent;
	private String filename;
	private Map<UUID, Document> xmlDocumentMap;

	public TranslatableElements(Document baseTranslationXmlContent, String filename, Map<UUID, Document> xmlDocumentMap)
	{
		this.baseTranslationXmlContent = baseTranslationXmlContent;
		this.filename = filename;
		this.xmlDocumentMap = xmlDocumentMap;
	}

	public void save(TranslationElementService translationElementService)
	{
		List<Element> baseTranslationElements = XmlDocumentSearchUtilities.findElementsWithAttribute(baseTranslationXmlContent, "translate");

		Map<UUID, Iterator<Element>> translationXmlElementIteratorSet = Maps.newHashMap();

		for(UUID translationId : xmlDocumentMap.keySet())
		{
			translationXmlElementIteratorSet.put(translationId, XmlDocumentSearchUtilities.findElementsWithAttribute(xmlDocumentMap.get(translationId), "translate").iterator());
		}

		int elementNumber = 0;

		for(Element baseTranslationElement : baseTranslationElements)
		{
			if(Boolean.parseBoolean(baseTranslationElement.getAttribute("translate")))
			{
				UUID elementId = UUID.randomUUID();

				baseTranslationElement.setAttribute("gtapi-trx-id", elementId.toString());

				for (UUID translationId : translationXmlElementIteratorSet.keySet())
				{
					if(!translationXmlElementIteratorSet.get(translationId).hasNext())
					{
						System.out.println("Warning, missing element!");
						System.out.println("Translation: " + translationId);
						System.out.println("Element: " + elementId);
						System.out.println("************************************");
					}
					else
					{
						Element targetTranslationElement = translationXmlElementIteratorSet.get(translationId).next();

						if(!targetTranslationElement.getNodeName().equalsIgnoreCase(baseTranslationElement.getNodeName()))
						{
							System.out.println("Warning, node name mismatch!");
							System.out.println("Translation: " + translationId);
							System.out.println("Element: " + elementId);
							System.out.println("************************************");
						}

						TranslationElement translationElement = new TranslationElement();
						translationElement.setId(elementId);
						translationElement.setTranslationId(translationId);
						translationElement.setBaseText(baseTranslationElement.getTextContent());
						translationElement.setTranslatedText(targetTranslationElement.getTextContent());
						translationElement.setElementType(baseTranslationElement.getNodeName());
						translationElement.setPageName(filename);
						translationElement.setDisplayOrder(elementNumber);

						translationElementService.insert(translationElement);
					}
				}
				elementNumber++;
			}
		}
	}
}
