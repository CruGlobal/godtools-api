package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PackageServiceTest extends Arquillian
{
	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();
	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();

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
		packageService.setAutoCommit(false);
		languageService.setAutoCommit(false);

		Language language = PackageMockData.persistLanguage(languageService);
		PackageMockData.persistPackage(packageService, language);
	}

	@AfterMethod
	public void cleanup()
	{
		packageService.rollback();
		languageService.rollback();
	}

	@Test
	public void testSelectById()
	{
		Package gtPackage = packageService.selectById(TEST_PACKAGE_ID);

		PackageMockData.validatePackage(gtPackage);
	}

	@Test
	public void testSelectByCode()
	{
		Package gtPackage = packageService.selectByCode("tp");

		PackageMockData.validatePackage(gtPackage);
	}

	//@Test
	public void testOrphanedByLanguage()
	{
		languageService.rollback();

		Package gtPackage = packageService.selectById(TEST_PACKAGE_ID);

		PackageMockData.validateOrphanLanguagePackage(gtPackage);
	}
}
