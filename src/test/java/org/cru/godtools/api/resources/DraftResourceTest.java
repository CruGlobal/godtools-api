package org.cru.godtools.api.resources;

import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.translations.*;
import org.cru.godtools.api.translations.model.ContentsFile;
import org.cru.godtools.api.translations.model.ResourceElement;
import org.cru.godtools.domain.*;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.domain.services.AbstractFullPackageServiceTest;
import org.cru.godtools.utils.collections.GodToolsPackageServiceTestClassCollection;
import org.cru.godtools.utils.collections.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.*;
import org.testng.Assert;

import javax.inject.Inject;
import javax.transaction.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 7/31/14.
 */
@RunWith(Arquillian.class)
public class DraftResourceTest extends AbstractFullPackageServiceTest
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(DraftResource.class, FileZipper.class, GodToolsTranslationRetrieval.class, TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	DraftResource draftResource;

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

		saveTestPackage();
		setTestPackageDraftStatus();
		DraftResource.BYPASS_ASYNC_UPDATE = true;
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

	@Test
	public void testGetDraft() throws Exception
	{
		// auth token does not have access to drafts
		Response response = draftResource.getTranslation("en", "kgp", 1, null, "false", new BigDecimal("1.1"), "draft-access", null);

		validateContentsXml((ContentsFile)response.getEntity());
	}

	@Test
	public void testGetDrafts() throws Exception
	{
		// auth token does not have access to drafts
		Response response = draftResource.getTranslations("en", 1, null, "false", "draft-access", null);

		validateContentsXml((ContentsFile)response.getEntity());
	}

	@Test(expected = UnauthorizedException.class)
	public void testGetDraftUnauthorized() throws Exception
	{
		// auth token does not have access to drafts
		draftResource.getTranslation("en", "kgp", 1, null, "false", new BigDecimal("1.1"), "a", null);
	}

	private void validateContentsXml(ContentsFile xmlContentsFile)
	{
		Assert.assertEquals(xmlContentsFile.getResourceSet().size(), 1);

		ResourceElement firstResource = xmlContentsFile.getResourceSet().iterator().next();

		Assert.assertEquals(firstResource.getLanguage(), "en");
		Assert.assertEquals(firstResource.getPackageCode(), "kgp");
		Assert.assertEquals(firstResource.getStatus(), "draft");
		Assert.assertEquals(firstResource.getConfig(), TRANSLATION_ID + ".xml");
		Assert.assertEquals(firstResource.getIcon(), "646dbcad0e235684c4b89c0b82fc7aa8ba3a87b5.png");
		Assert.assertEquals(firstResource.getName(), "Connaitre Dieu Personellement");
		Assert.assertEquals(firstResource.getVersion(), "1.2");
	}
}