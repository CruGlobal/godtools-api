package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.translations.*;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 7/31/14.
 */
public class TranslationElementServiceTest extends Arquillian
{
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

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@BeforeMethod
	public void setup()
	{
		packageService.setAutoCommit(false);
		Language language = TranslationServiceTestMockData.persistLanguage(languageService);
		Package gtPackage = TranslationServiceTestMockData.persistPackage(packageService);
		Translation translation = TranslationServiceTestMockData.persistTranslation(translationService, language, gtPackage);
		PageStructure pageStructure = TranslationElementMockData.persistPageStructure(pageStructureService, translation);
		TranslationElementMockData.persistTranslationElements(translationElementService, pageStructure, translation);
	}

	@AfterMethod
	public void cleanup()
	{
		packageService.rollback();
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
