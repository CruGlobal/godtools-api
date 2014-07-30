package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.languages.LanguageServiceTestMockData;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PackageServiceTest extends AbstractServiceTest
{
	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();

	@Inject
	PackageService packageService;

	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(PackageService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@BeforeMethod
	public void setup()
	{
		try
		{
			packageService.sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		PackageServiceTestMockData.persistPackage(packageService);
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			packageService.sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
	}

	@Test
	public void testSelectById()
	{
		Package gtPackage = packageService.selectById(TEST_PACKAGE_ID);

		PackageServiceTestMockData.validatePackage(gtPackage);
	}

	@Test
	public void testSelectByCode()
	{
		Package gtPackage = packageService.selectByCode("tp");

		PackageServiceTestMockData.validatePackage(gtPackage);
	}
}
