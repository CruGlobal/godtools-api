package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.tests.GodToolsPackageServiceTestClassCollection;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * First pass at writing Arquillian tests
 *
 * Created by ryancarlson on 7/30/14.
 */
public class PackageResourceTest extends AbstractFullPackageServiceTest
{

	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(PackageResource.class, AuthorizationService.class, FileZipper.class, GodToolsPackageRetrievalProcess.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	PackageResource packageResource;

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
	public void testGetPackage() throws Exception
	{
		Response response = packageResource.getPackage("en",
				"kgp",
				1,
				null,
				"false",
				new BigDecimal("1.1"),
				"High",
				"a",
				null);

		Assert.assertEquals(response.getStatus(), 200);
	}
}
