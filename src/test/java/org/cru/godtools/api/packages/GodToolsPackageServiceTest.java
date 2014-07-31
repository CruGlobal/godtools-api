package org.cru.godtools.api.packages;

import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.packages.PixelDensity;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.tests.GodToolsPackageServiceTestClassCollection;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageServiceTest extends AbstractFullPackageServiceTest
{

	@Inject
	private GodToolsPackageService godToolsPackageService;

	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
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
		try
		{
			TestSqlConnectionProducer.getConnection().getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		saveTestPackage();
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			TestSqlConnectionProducer.getConnection().getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
	}

	@Test
	public void testGetPackage()
	{
		GodToolsPackage englishKgpPackage = godToolsPackageService.getPackage(new LanguageCode("en"), "kgp",  new GodToolsVersion(new BigDecimal("1.1")), 1, PixelDensity.getEnum("High"));

		GodToolsPackageServiceTestMockData.validateEnglishKgpPackage(englishKgpPackage);
	}

	@Test
	public void testGetPackagesForLanguage()
	{
		Set<GodToolsPackage> englishPackages = godToolsPackageService.getPackagesForLanguage(new LanguageCode("en"), 1, PixelDensity.getEnum("High"));

		Assert.assertEquals(englishPackages.size(), 1);
		GodToolsPackageServiceTestMockData.validateEnglishKgpPackage(englishPackages.iterator().next());
	}

	@Test
	public void testGetPackageNoMinimumInterpreterSpecified()
	{
		GodToolsPackage englishKpgPackage = godToolsPackageService.getPackage(new LanguageCode("en"), "kgp", new GodToolsVersion(new BigDecimal("1.1")), null, PixelDensity.getEnum("High"));

		GodToolsPackageServiceTestMockData.validateEnglishKgpPackage(englishKpgPackage);
	}
}
