package org.cru.godtools.domain.translations;

import org.cru.godtools.domain.*;

import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
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
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class TranslationServiceTest extends Arquillian
{
	public static final UUID TEST_TRANSLATION_ID = UUID.randomUUID();
	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();
	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();

	@Inject
	TranslationService translationService;
	@Inject
	PackageService packageService;
	@Inject
	LanguageService languageService;

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

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@BeforeMethod
	public void setup()
	{
		translationService.setAutoCommit(false);
		packageService.setAutoCommit(false);
		languageService.setAutoCommit(false);
		Language language = TranslationMockData.persistLanguage(languageService);
		Package gtPackage = TranslationMockData.persistPackage(packageService);
		TranslationMockData.persistTranslation(translationService, language, gtPackage);
	}

	@AfterMethod
	public void cleanup()
	{
		translationService.rollback();
		packageService.rollback();
		languageService.rollback();
	}

	@Test
	public void testSelectByLanguageId()
	{
		List<Translation> translations = translationService.selectByLanguageId(TEST_LANGUAGE_ID);

		Assert.assertEquals(translations.size(), 1);
		TranslationMockData.validateTranslation(translations.get(0));
	}

	@Test
	public void testSelectByPackageId()
	{
		List<Translation> translations = translationService.selectByPackageId(TEST_PACKAGE_ID);

		Assert.assertEquals(translations.size(), 1);
		TranslationMockData.validateTranslation(translations.get(0));
	}

	@Test
	public void testSelectByLanguageIdPackageId()
	{
		List<Translation> translation = translationService.selectByLanguageIdPackageId(TEST_LANGUAGE_ID, TEST_PACKAGE_ID);

		TranslationMockData.validateTranslation(translation.get(0));
	}
}
