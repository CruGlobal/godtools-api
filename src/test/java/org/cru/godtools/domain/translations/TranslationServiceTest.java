package org.cru.godtools.domain.translations;

import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.languages.LanguageService;

import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class TranslationServiceTest extends AbstractServiceTest
{
	public static final UUID TEST_TRANSLATION_ID =UUID.randomUUID();
	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();
	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();

	@Inject
	TranslationService translationService;

	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(TranslationService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@BeforeClass()
	public void setup()
	{
		super.setup();

		translationService = new TranslationService(sqlConnection);

		TranslationServiceTestMockData.persistLanguage(new LanguageService(sqlConnection));
		TranslationServiceTestMockData.persistPackage(new PackageService(sqlConnection));
		TranslationServiceTestMockData.persistTranslation(translationService);
	}

	@Test
	public void testSelectByLanguageId()
	{
		List<Translation> translations = translationService.selectByLanguageId(TEST_LANGUAGE_ID);

		Assert.assertEquals(translations.size(), 1);
		TranslationServiceTestMockData.validateTranslation(translations.get(0));
	}

	@Test
	public void testSelectByPackageId()
	{
		List<Translation> translations = translationService.selectByPackageId(TEST_PACKAGE_ID);

		Assert.assertEquals(translations.size(), 1);
		TranslationServiceTestMockData.validateTranslation(translations.get(0));
	}

	@Test
	public void testSelectByLanguageIdPackageId()
	{
		List<Translation> translation = translationService.selectByLanguageIdPackageId(TEST_LANGUAGE_ID, TEST_PACKAGE_ID);

		TranslationServiceTestMockData.validateTranslation(translation.get(0));
	}
}
