package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.utils.*;
import org.testng.*;

import java.util.*;

/**
 * Created by justinsturm on 7/31/15.
 */
public class PageStructureMockData
{
    public static Translation persistTranslation(TranslationService translationService)
    {
        Translation translation = new Translation();
        translation.setId(PageStructureServiceTest.TEST_TRANSLATION_ID);
        translationService.insert(translation);

        return translation;
    }

    public static void persistPageStructures(PageStructureService pageStructureService, Translation translation)
    {
        PageStructure pageStructure1 = new PageStructure();
        pageStructure1.setId(PageStructureServiceTest.TEST_PAGE_STRUCTURE_ID_1);
        pageStructure1.setTranslation(translation);
        pageStructure1.setFilename("page_1.xml");
        pageStructure1.setXmlContent(XmlDocumentFromFile.get("/page_1.xml"));
        pageStructureService.insert(pageStructure1);

        PageStructure pageStructure2 = new PageStructure();
        pageStructure2.setId(PageStructureServiceTest.TEST_PAGE_STRUCTURE_ID_2);
        pageStructure2.setTranslation(translation);
        pageStructure2.setFilename("test_file_1.xml");
        pageStructure2.setXmlContent(XmlDocumentFromFile.get("/test_file_1.xml"));
        pageStructureService.insert(pageStructure2);

        PageStructure pageStructure3 = new PageStructure();
        pageStructure3.setId(PageStructureServiceTest.TEST_PAGE_STRUCTURE_ID_3);
        pageStructure3.setTranslation(null);
        pageStructure3.setFilename(null);
        pageStructure3.setXmlContent(null);
        pageStructureService.insert(pageStructure3);
    }

    public static void validatePageStructureById(PageStructure pageStructure)
    {
        Assert.assertNotNull(pageStructure);
        Assert.assertEquals(pageStructure.getId(), PageStructureServiceTest.TEST_PAGE_STRUCTURE_ID_1);
        Assert.assertEquals(pageStructure.getTranslation().getId(), PageStructureServiceTest.TEST_TRANSLATION_ID);
    }

    public static void validatePageStructuresByTranslation(List<PageStructure> pageStructureList)
    {
        Assert.assertEquals(pageStructureList.size(),2);
        Assert.assertEquals(pageStructureList.get(0).getTranslation().getId(),PageStructureServiceTest.TEST_TRANSLATION_ID);
    }

    public static void validatePageStructureByTranslationAndFile(PageStructure pageStructure)
    {
        Assert.assertNotNull(pageStructure);
        Assert.assertEquals(pageStructure.getId(), PageStructureServiceTest.TEST_PAGE_STRUCTURE_ID_2);
        Assert.assertEquals(pageStructure.getTranslation().getId(), PageStructureServiceTest.TEST_TRANSLATION_ID);
        Assert.assertEquals(pageStructure.getFilename(), "test_file_1.xml");
    }

    public static void validatePageStructureUpdate(PageStructure pageStructure, UUID translationId, String filename)
    {
        Assert.assertNotNull(pageStructure);
        Assert.assertEquals(pageStructure.getId(), PageStructureServiceTest.TEST_PAGE_STRUCTURE_ID_3);
        Assert.assertEquals(translationId != null ? pageStructure.getTranslation().getId() : null, translationId);
        Assert.assertEquals(pageStructure.getFilename(), filename);
    }
}
