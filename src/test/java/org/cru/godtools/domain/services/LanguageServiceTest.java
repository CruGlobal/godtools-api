package org.cru.godtools.domain.services;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.utils.*;
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
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
@RunWith(Arquillian.class)
public class LanguageServiceTest
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

	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();
	public static final UUID TEST_LANGUAGE2_ID = UUID.randomUUID();

	@Inject
	LanguageService languageService;

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

		LanguageMockData.persistLanguages(languageService);
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

    @Test
    public void testSelectAllLanguages()
    {
		List<Language> languages = languageService.selectAllLanguages();

		Assert.assertEquals(languages.size(), 2);
		LanguageMockData.validateLanguages(languages);
    }

    @Test
    public void testSelectLanguageById()
    {
		Language language = languageService.selectLanguageById(TEST_LANGUAGE_ID);

		Assert.assertNotNull(language);
		LanguageMockData.validateLanguage(language);

		Language language2 = languageService.selectLanguageById(TEST_LANGUAGE2_ID);

		Assert.assertNotNull(language2);
		LanguageMockData.validateLanguage2(language2);
    }

    @Test
    public void testSelectLanguageByCode()
    {
		LanguageCode languageCode = new LanguageCode("fr-nt-hipster");
		Language language = languageService.selectByLanguageCode(languageCode);

		Assert.assertNotNull(language);
		LanguageMockData.validateLanguage(language);

		LanguageCode languageCode2 = new LanguageCode("en");
		Language language2 = languageService.selectByLanguageCode(languageCode2);

		Assert.assertNotNull(language2);
		LanguageMockData.validateLanguage2(language2);
	}

	@Test
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

		Language nonExistantLanguage = LanguageMockData.getNonExistantLanguage();

		Assert.assertFalse(languageService.languageExists(nonExistantLanguage));
	}
}
