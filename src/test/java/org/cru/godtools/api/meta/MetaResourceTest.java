package org.cru.godtools.api.meta;

import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.SQLException;

import java.util.Set;

/**
 * Created by ryancarlson on 7/31/14.
 */
public class MetaResourceTest extends AbstractFullPackageServiceTest
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(MetaResource.class, MetaService.class, AuthorizationService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	MetaResource metaResource;

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
	public void testGetAllMetaInfo() throws Exception
	{
		Response response = metaResource.getAllMetaInfo(1, null, "a", null);

		Assert.assertEquals(200, response.getStatus());

		validateMetaInfo((MetaResults) response.getEntity());
	}

	@Test
	public void testGetLanguageMetaInfo() throws Exception
	{
		Response response = metaResource.getLanguageMetaInfo("en", 1, null, "a", null);

		Assert.assertEquals(200, response.getStatus());

		validateMetaInfo((MetaResults) response.getEntity());
	}

	@Test
	public void testGetLanguageAndPackageMetaInfo() throws Exception
	{
		Response response = metaResource.getLanguageAndPackageMetaInfo("en", "kgp", 1, null, "a", null);

		Assert.assertEquals(200, response.getStatus());

		validateMetaInfo((MetaResults) response.getEntity());
	}

private void validateMetaInfo(MetaResults metaResults)
	{
		Set<MetaLanguage> languageSet = metaResults.getLanguages();

		Assert.assertEquals(languageSet.size(), 1);

		MetaLanguage metaLanguage = languageSet.iterator().next();

		Assert.assertEquals(metaLanguage.getCode(), "en");

		Assert.assertEquals(metaLanguage.getPackages().size(), 1);

		MetaPackage metaPackage = metaLanguage.getPackages().iterator().next();

		Assert.assertEquals(metaPackage.getCode(), "kgp");
		Assert.assertEquals(metaPackage.getName(), "Knowing God Personally");
		Assert.assertEquals(metaPackage.getStatus(), "live");
		Assert.assertEquals(metaPackage.getVersion(), new BigDecimal("1.1"));
	}
}
