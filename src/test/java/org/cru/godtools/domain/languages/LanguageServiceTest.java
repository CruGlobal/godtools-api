package org.cru.godtools.domain.languages;

import org.cru.godtools.domain.*;
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
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class LanguageServiceTest extends Arquillian
{
	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();
	public static final UUID TEST_LANGUAGE2_ID = UUID.randomUUID();

	@Inject
	@JPAStandard
	LanguageService languageService;

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
		languageService.setAutoCommit(false);
		LanguageServiceTestMockData.persistLanguages(languageService);
	}

	@AfterMethod
	public void cleanup()
	{
		languageService.rollback();
	}

    @Test
    public void testSelectAllLanguages()
    {
		List<Language> languages = languageService.selectAllLanguages();

		Assert.assertEquals(languages.size(), 2);
		LanguageServiceTestMockData.validateLanguages(languages);
    }

    @Test
    public void testSelectLanguageById()
    {
		Language language = languageService.selectLanguageById(TEST_LANGUAGE_ID);

		Assert.assertNotNull(language);
		LanguageServiceTestMockData.validateLanguage(language);

		Language language2 = languageService.selectLanguageById(TEST_LANGUAGE2_ID);

		Assert.assertNotNull(language2);
		LanguageServiceTestMockData.validateLanguage2(language2);
    }

    @Test
    public void testSelectLanguageByCode()
    {
		LanguageCode languageCode = new LanguageCode("fr-nt-hipster");
		Language language = languageService.selectByLanguageCode(languageCode);

		Assert.assertNotNull(language);
		LanguageServiceTestMockData.validateLanguage(language);

		LanguageCode languageCode2 = new LanguageCode("en");
		Language language2 = languageService.selectByLanguageCode(languageCode2);

		Assert.assertNotNull(language2);
		LanguageServiceTestMockData.validateLanguage2(language2);
	}

	@Test()
	public void testSelectLanguageByCodeNotFound()
	{
		LanguageCode languageCode = new LanguageCode("fr");
		Assert.assertNull(languageService.selectByLanguageCode(languageCode));
	}


	@Test
	public void testLanguageExists()
	{
		Language language = languageService.selectLanguageById(TEST_LANGUAGE_ID);

		Assert.assertTrue(languageService.languageExists(language));

		Language language2 = languageService.selectLanguageById(TEST_LANGUAGE2_ID);

		Assert.assertTrue(languageService.languageExists(language2));

		Language nonExistantLanguage = LanguageServiceTestMockData.getNonExistantLanguage();

		Assert.assertFalse(languageService.languageExists(nonExistantLanguage));
	}
}
