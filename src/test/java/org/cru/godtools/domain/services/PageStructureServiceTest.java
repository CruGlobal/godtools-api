package org.cru.godtools.domain.services;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.utils.*;
import org.cru.godtools.utils.collections.*;
import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.*;
import org.jboss.shrinkwrap.api.spec.*;
import org.junit.*;
import org.junit.runner.*;

import javax.inject.*;
import javax.transaction.*;
import java.util.*;

/**
 * Created by justinsturm on 7/31/15.
 */
@RunWith(Arquillian.class)
public class PageStructureServiceTest
{
    @Deployment
    public static JavaArchive createDeployment()
    {
        Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(sql2oTestClassCollection.getClasses())
                .addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
                .addClasses(TestClockImpl.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static final UUID TEST_TRANSLATION_ID = UUID.randomUUID();
    public static final UUID TEST_PAGE_STRUCTURE_ID_1 = UUID.randomUUID();
    public static final UUID TEST_PAGE_STRUCTURE_ID_2 = UUID.randomUUID();
    public static final UUID TEST_PAGE_STRUCTURE_ID_3 = UUID.randomUUID();

    @Inject
    TranslationService translationService;
    @Inject
    PageStructureService pageStructureService;
    @Inject
    UserTransaction userTransaction;

    @BeforeClass
    public void initializeDatabase()
    {
        UnittestDatabaseBuilder.build();
    }

    @Before
    public void setup() throws SystemException, NotSupportedException
    {
        userTransaction.begin();

        Translation translation = PageStructureMockData.persistTranslation(translationService);
        PageStructureMockData.persistPageStructures(pageStructureService, translation);
    }

    @After
    public void cleanup() throws SystemException
    {
        userTransaction.rollback();
    }

    @Test
    public void testSelectById()
    {
        PageStructure pageStructure = pageStructureService.selectById(TEST_PAGE_STRUCTURE_ID_1);

        PageStructureMockData.validatePageStructureById(pageStructure);
    }

    @Test
    public void testSelectByTranslationId()
    {
        List<PageStructure> pageStructures = pageStructureService.selectByTranslationId(TEST_TRANSLATION_ID);

        PageStructureMockData.validatePageStructuresByTranslation(pageStructures);
    }

    @Test
    public void testSelectByTranslationAndFile()
    {
        PageStructure pageStructure = pageStructureService.selectByTranslationIdAndFilename(TEST_TRANSLATION_ID, "test_file_1.xml");

        PageStructureMockData.validatePageStructureByTranslationAndFile(pageStructure);
    }

    @Test
    public void testUpdate()
    {
        PageStructure pageStructure = pageStructureService.selectById(TEST_PAGE_STRUCTURE_ID_3);
        PageStructureMockData.validatePageStructureUpdate(pageStructure, null, null);
        pageStructure.setTranslation(pageStructureService.selectById(TEST_PAGE_STRUCTURE_ID_1).getTranslation());
        pageStructure.setFilename(pageStructureService.selectById(TEST_PAGE_STRUCTURE_ID_2).getFilename());
        pageStructureService.update(pageStructure);
        PageStructureMockData.validatePageStructureUpdate(pageStructure, TEST_TRANSLATION_ID, "test_file_1.xml");
    }

}