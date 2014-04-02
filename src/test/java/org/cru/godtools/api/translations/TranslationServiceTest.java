package org.cru.godtools.api.translations;

import org.cru.godtools.api.database.SqlConnectionProducer;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.tests.UnittestDatabaseBuilder;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class TranslationServiceTest
{
	UnittestDatabaseBuilder builder;
	TranslationServiceTestMockDataService mockData;

	Connection sqlConnection;
	TranslationService translationService;

	public static final UUID TEST_TRANSLATION_ID =UUID.randomUUID();
	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();
	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();

	@BeforeClass()
	public void setup()
	{
		builder = new UnittestDatabaseBuilder();
		builder.build();

		sqlConnection = SqlConnectionProducer.getTestSqlConnection();
		translationService = new TranslationService(sqlConnection);

		mockData = new TranslationServiceTestMockDataService();
		mockData.persistLanguage(new LanguageService(sqlConnection));
		mockData.persistPackage(new PackageService(sqlConnection));
		mockData.persistTranslation(translationService);
	}

	@Test
	public void testSelectByLanguageId()
	{
		List<Translation> translations = translationService.selectByLanguageId(TEST_LANGUAGE_ID);

		Assert.assertEquals(translations.size(), 1);
		mockData.validateTranslation(translations.get(0));

	}

	@Test
	public void testSelectByPackageId()
	{
		List<Translation> translations = translationService.selectByPackageId(TEST_PACKAGE_ID);

		Assert.assertEquals(translations.size(), 1);
		mockData.validateTranslation(translations.get(0));
	}

	@Test
	public void testSelectByLanguageIdPackageId()
	{
		Translation translation = translationService.selectByLanguageIdPackageId(TEST_LANGUAGE_ID, TEST_PACKAGE_ID);

		mockData.validateTranslation(translation);
	}
}
