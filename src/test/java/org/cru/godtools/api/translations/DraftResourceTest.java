package org.cru.godtools.api.translations;

import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.translations.model.Content;
import org.cru.godtools.api.translations.model.Resource;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
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

/**
 * Created by ryancarlson on 7/31/14.
 */
public class DraftResourceTest extends AbstractFullPackageServiceTest
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(DraftResource.class, AuthorizationService.class, FileZipper.class, GodToolsTranslationRetrieval.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	DraftResource draftResource;

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
		catch (SQLException e)
		{
			/*yawn*/
		}
		saveTestPackage();
		setTestPackageDraftStatus();
		DraftResource.BYPASS_ASYNC_UPDATE = true;
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			TestSqlConnectionProducer.getConnection().getJdbcConnection().rollback();
		}
		catch (SQLException e)
		{
			/*yawn*/
		}
	}

	@Test
	public void testGetDraft() throws Exception
	{
		// auth token does not have access to drafts
		Response response = draftResource.getTranslation("en", "kgp", 1, null, "false", new BigDecimal("1.1"), "draft-access", null);

		validateContentsXml((Content)response.getEntity());
	}

	@Test
	public void testGetDrafts() throws Exception
	{
		// auth token does not have access to drafts
		Response response = draftResource.getTranslations("en", 1, null, "false", "draft-access", null);

		validateContentsXml((Content)response.getEntity());
	}

	@Test(expectedExceptions = UnauthorizedException.class)
	public void testGetDraftUnauthorized() throws Exception
	{
		// auth token does not have access to drafts
		draftResource.getTranslation("en", "kgp", 1, null, "false", new BigDecimal("1.1"), "a", null);
	}

	private void validateContentsXml(Content xmlContentsFile)
	{
		Assert.assertEquals(xmlContentsFile.getResourceSet().size(), 1);

		Resource firstResource = xmlContentsFile.getResourceSet().iterator().next();

		Assert.assertEquals(firstResource.getLanguage(), "en");
		Assert.assertEquals(firstResource.getPackageCode(), "kgp");
		Assert.assertEquals(firstResource.getStatus(), "draft");
		Assert.assertEquals(firstResource.getConfig(), TRANSLATION_ID + ".xml");
		Assert.assertEquals(firstResource.getIcon(), "646dbcad0e235684c4b89c0b82fc7aa8ba3a87b5.png");
		Assert.assertEquals(firstResource.getName(), "Connaitre Dieu Personellement");
		Assert.assertEquals(firstResource.getVersion(), "1.2");
	}
}