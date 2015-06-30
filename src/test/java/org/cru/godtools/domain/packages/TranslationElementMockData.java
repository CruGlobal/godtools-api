package org.cru.godtools.domain.packages;

import org.cru.godtools.api.services.*;
import org.cru.godtools.domain.translations.TranslationServiceTest;
import org.cru.godtools.utils.XmlDocumentFromFile;
import org.testng.Assert;

import java.util.List;

/**
 * Created by ryancarlson on 7/31/14.
 */
public class TranslationElementMockData
{
	public static void persistPageStructure(PageStructureService pageStructureService)
	{
		PageStructure pageStructure = new PageStructure();
		pageStructure.setId(TranslationElementServiceTest.TEST_PAGE_STRUCTURE_ID);
		pageStructure.setTranslationId(TranslationServiceTest.TEST_TRANSLATION_ID);
		pageStructure.setFilename("page_1.xml");
		pageStructure.setXmlContent(XmlDocumentFromFile.get("/page_1.xml"));

		pageStructureService.insert(pageStructure);
	}

	public static void persistTranslationElements(TranslationElementService translationElementService)
	{
		TranslationElement translationElementOne = new TranslationElement();
		translationElementOne.setId(TranslationElementServiceTest.TEST_TRANSLATION_ELEMENT_ONE_ID);
		translationElementOne.setTranslationId(TranslationServiceTest.TEST_TRANSLATION_ID);
		translationElementOne.setPageStructureId(TranslationElementServiceTest.TEST_PAGE_STRUCTURE_ID);
		translationElementOne.setElementType("greeting");
		translationElementOne.setDisplayOrder(0);
		translationElementOne.setPageName("page_1.xml");
		translationElementOne.setBaseText("Hello World");
		translationElementOne.setTranslatedText("Bonjour tout le monde");
		translationElementService.insert(translationElementOne);

		TranslationElement translationElementTwo = new TranslationElement();
		translationElementTwo.setId(TranslationElementServiceTest.TEST_TRANSLATION_ELEMENT_TWO_ID);
		translationElementTwo.setTranslationId(TranslationServiceTest.TEST_TRANSLATION_ID);
		translationElementTwo.setPageStructureId(TranslationElementServiceTest.TEST_PAGE_STRUCTURE_ID);
		translationElementTwo.setElementType("question");
		translationElementTwo.setDisplayOrder(1);
		translationElementTwo.setPageName("page_1.xml");
		translationElementTwo.setBaseText("What time is it?");
		translationElementTwo.setTranslatedText("Quelle heure est-til?");
		translationElementService.insert(translationElementTwo);
	}

	public static void validateTranslationElements(List<TranslationElement> translationElementList)
	{
		for(TranslationElement translationElement : translationElementList)
		{
			if(translationElement.getId().equals(TranslationElementServiceTest.TEST_TRANSLATION_ELEMENT_ONE_ID))
			{
				validateTranslationElementsOne(translationElement);
			}
			else if(translationElement.getId().equals(TranslationElementServiceTest.TEST_TRANSLATION_ELEMENT_TWO_ID))
			{
				validateTranslationElementsTwo(translationElement);
			}
			else
			{
				Assert.fail("unknown ID");
			}
		}

	}

	public static void validateTranslationElementsOne(TranslationElement translationElement)
	{
		Assert.assertEquals(translationElement.getBaseText(), "Hello World");
		Assert.assertEquals((int)translationElement.getDisplayOrder(), 0);
		Assert.assertEquals(translationElement.getElementType(), "greeting");
		Assert.assertEquals(translationElement.getPageName(), "page_1.xml");
		Assert.assertEquals(translationElement.getPageStructureId(), TranslationElementServiceTest.TEST_PAGE_STRUCTURE_ID);
		Assert.assertEquals(translationElement.getTranslatedText(), "Bonjour tout le monde");
		Assert.assertEquals(translationElement.getTranslationId(),TranslationServiceTest.TEST_TRANSLATION_ID);
	}

	public static void validateTranslationElementsTwo(TranslationElement translationElement)
	{
		Assert.assertEquals(translationElement.getBaseText(), "What time is it?");
		Assert.assertEquals((int)translationElement.getDisplayOrder(), 1);
		Assert.assertEquals(translationElement.getElementType(), "question");
		Assert.assertEquals(translationElement.getPageName(), "page_1.xml");
		Assert.assertEquals(translationElement.getPageStructureId(), TranslationElementServiceTest.TEST_PAGE_STRUCTURE_ID);
		Assert.assertEquals(translationElement.getTranslatedText(), "Quelle heure est-til?");
		Assert.assertEquals(translationElement.getTranslationId(),TranslationServiceTest.TEST_TRANSLATION_ID);
	}

	public static void validateUpdatedTranslationElement(TranslationElement translationElement)
	{
		Assert.assertEquals(translationElement.getBaseText(), "Hello people");
		Assert.assertEquals((int)translationElement.getDisplayOrder(), 21);
		Assert.assertEquals(translationElement.getElementType(), "greeting");
		Assert.assertEquals(translationElement.getPageName(), "page_1.xml");
		Assert.assertEquals(translationElement.getPageStructureId(), TranslationElementServiceTest.TEST_PAGE_STRUCTURE_ID);
		Assert.assertEquals(translationElement.getTranslatedText(), "Bonjour touts les peuples");
		Assert.assertEquals(translationElement.getTranslationId(),TranslationServiceTest.TEST_TRANSLATION_ID);
	}
}
