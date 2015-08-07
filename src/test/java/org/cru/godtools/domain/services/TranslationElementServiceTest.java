package org.cru.godtools.domain.services;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.model.Package;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.utils.collections.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.*;
import org.junit.runner.*;
import org.testng.Assert;

import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 7/31/14.
 */
@RunWith(Arquillian.class)
public class TranslationElementServiceTest
{
	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClass(TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	public static final UUID TEST_PAGE_STRUCTURE_ID = UUID.randomUUID();
	public static final UUID TEST_TRANSLATION_ELEMENT_ONE_ID = UUID.randomUUID();
	public static final UUID TEST_TRANSLATION_ELEMENT_TWO_ID = UUID.randomUUID();

	@Inject
	TranslationService translationService;
	@Inject
	PackageService packageService;
	@Inject
	LanguageService languageService;
	@Inject
	TranslationElementService translationElementService;
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

		Language language = TranslationMockData.persistLanguage(languageService);
		Package gtPackage = TranslationMockData.persistPackage(packageService);
		Translation translation = TranslationMockData.persistTranslation(translationService, language, gtPackage);
		PageStructure pageStructure = TranslationElementMockData.persistPageStructure(pageStructureService, translation);
		TranslationElementMockData.persistTranslationElements(translationElementService, pageStructure, translation);
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

	@Test
	public void testSelectByTranslationId()
	{
		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(TranslationServiceTest.TEST_TRANSLATION_ID);

		Assert.assertEquals(translationElementList.size(), 2);
		TranslationElementMockData.validateTranslationElements(translationElementList);
	}

	@Test
	public void testSelectByIdTranslationId()
	{
		TranslationElement translationElement = translationElementService.selectyByIdTranslationId(TEST_TRANSLATION_ELEMENT_ONE_ID, TranslationServiceTest.TEST_TRANSLATION_ID);

		Assert.assertNotNull(translationElement);
		TranslationElementMockData.validateTranslationElementsOne(translationElement);
	}

	@Test
	public void testUpdate()
	{
		TranslationElement translationElement = translationElementService.selectyByIdTranslationId(TEST_TRANSLATION_ELEMENT_ONE_ID, TranslationServiceTest.TEST_TRANSLATION_ID);
		translationElement.setBaseText("Hello people");
		translationElement.setTranslatedText("Bonjour touts les peuples");
		translationElement.setDisplayOrder(21);

		translationElementService.update(translationElement);

		TranslationElementMockData.validateUpdatedTranslationElement(translationElementService.selectyByIdTranslationId(TEST_TRANSLATION_ELEMENT_ONE_ID, TranslationServiceTest.TEST_TRANSLATION_ID));
	}
}
