package org.cru.godtools.migration;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class TranslatableElements
{
	private Document baseTranslation;
	private Document translatedPage;
	private UUID translationId;
	private UUID pageStructureId;
	private String filename;

	public TranslatableElements(Document baseTranslation, Document translatedPage, String filename, UUID translationId)
	{
		this(baseTranslation, translatedPage, filename, translationId, null);
	}
	public TranslatableElements(Document baseTranslation, Document translatedPage, String filename, UUID translationId, UUID pageStructureId)
	{
		this.baseTranslation = baseTranslation;
		this.translatedPage = translatedPage;
		this.translationId = translationId;
		this.filename = filename;
		this.pageStructureId = pageStructureId;
	}

	public void save(TranslationElementService translationElementService)
	{
		List<Element> baseTranslationElements = XmlDocumentSearchUtilities.findElementsWithAttribute(baseTranslation, "translate");
		List<Element> translatedElements = XmlDocumentSearchUtilities.findElementsWithAttribute(translatedPage, "translate");

		int elementNumber = 0;

		for (Element baseTranslationElement : baseTranslationElements)
		{
			if (Boolean.parseBoolean(baseTranslationElement.getAttribute("translate")))
			{
				UUID elementId = UUID.randomUUID();

				baseTranslationElement.setAttribute("gtapi-trx-id", elementId.toString());

				Element targetTranslationElement;

				try
				{
					targetTranslationElement = translatedElements.get(elementNumber);
				}
				catch(IndexOutOfBoundsException e)
				{
					System.out.println("Expected element but wasn't there... ");
					System.out.println("Translation: " + translationId);
					System.out.println("Page name: " + filename);

					return;
				}

				if (!targetTranslationElement.getNodeName().equalsIgnoreCase(baseTranslationElement.getNodeName()))
				{
					System.out.println("Warning, node name mismatch!");
					System.out.println("Translation: " + translationId);
					System.out.println("Page name: " + filename);
					System.out.println("Element: " + elementId);
					System.out.println("************************************");
				}

				targetTranslationElement.setAttribute("gtapi-trx-id", elementId.toString());

				TranslationElement translationElement = new TranslationElement();
				translationElement.setId(elementId);
				translationElement.setTranslationId(translationId);
				translationElement.setBaseText(baseTranslationElement.getTextContent());
				translationElement.setTranslatedText(targetTranslationElement.getTextContent());
				translationElement.setElementType(baseTranslationElement.getNodeName());
				translationElement.setPageName(filename);
				translationElement.setDisplayOrder(elementNumber);
				translationElement.setPageStructureId(pageStructureId);

				translationElementService.insert(translationElement);
			}
			elementNumber++;
		}
	}
}
