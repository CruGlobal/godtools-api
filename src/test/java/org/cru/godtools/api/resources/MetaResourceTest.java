package org.cru.godtools.api.resources;

import org.cru.godtools.api.meta.*;
import org.cru.godtools.domain.*;
import org.cru.godtools.domain.services.mockdata.AbstractFullPackageMockData;
import org.cru.godtools.utils.*;
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
import java.math.*;

import java.util.Set;

/**
 * Created by ryancarlson on 7/31/14.
 */
@RunWith(Arquillian.class)
public class MetaResourceTest extends AbstractFullPackageMockData
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(MetaResource.class, TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	MetaResource metaResource;

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
	}

	@After
	public void cleanup()
	{
		metaResource.rollback();
	}

	/**
	 * Test getting all meta info with a generic access token.
	 *
	 * Expects 1 language (en) and 1 package (kgp)
	 */
	@Test
	public void testGetAllMetaInfo() throws Exception
	{
		Response response = metaResource.getAllMetaInfo(1, null, "a", null);

		Assert.assertEquals(200, response.getStatus());

		validateLiveMetaInfo((MetaResults) response.getEntity());
	}

	/**
	 * Test getting meta info for a language (en) with a generic access token.
	 *
	 * Expects 1 language (en) and 1 package (kgp)
	 */
	@Test
	public void testGetLanguageMetaInfo() throws Exception
	{
		Response response = metaResource.getLanguageMetaInfo("en", 1, null, "a", null);

		Assert.assertEquals(200, response.getStatus());

		validateLiveMetaInfo((MetaResults) response.getEntity());
	}

	/**
	 * Test getting meta info for a language (en) and package (kgp) with a generic access token.
	 *
	 * Expects 1 language (en) and 1 package (kgp)
	 */
	@Test
	public void testGetLanguageAndPackageMetaInfo() throws Exception
	{
		Response response = metaResource.getLanguageAndPackageMetaInfo("en", "kgp", 1, null, "a", null);

		Assert.assertEquals(200, response.getStatus());

		validateLiveMetaInfo((MetaResults) response.getEntity());
	}

	/**
	 * Test getting meta info for a language (en) and package (kgp) with a draft access token.
	 *
	 * Expects 1 language (en), and no packages (no drafts in database)
	 */
	@Test
	public void testGetAllDraftMetaInfo() throws Exception
	{
		Response response = metaResource.getLanguageAndPackageMetaInfo("en", "kgp", 1, null, "draft-access", null);

		Assert.assertEquals(200, response.getStatus());

		// english language should be present
		MetaLanguage metaLanguage = ((MetaResults)response.getEntity()).getLanguages().iterator().next();
		Assert.assertEquals(metaLanguage.getCode(), "en");

		Assert.assertTrue(metaLanguage.getPackages().isEmpty());
	}

	/**
	 * Test getting meta info for a language (en) and package (kgp) with a draft access token.
	 *
	 * Expects 1 language (en) and 1 package (kgp)
	 */
	@Test
	public void testGetAllDraftMetaInfoWithResults() throws Exception
	{
		// set the current translation to status = draft and version = 1.2
		setTestPackageDraftStatus();

		Response response = metaResource.getLanguageAndPackageMetaInfo("en", "kgp", 1, null, "draft-access", null);

		Assert.assertEquals(200, response.getStatus());

		validateDraftMetaInfo((MetaResults) response.getEntity());
	}

	/**
	 * Test getting meta info w/o interpreter.  Should result in a 400 Bad Request exception
	 */
	@Test
	public void testGetAllMetaInfoNoInterpreter() throws Exception
	{
		Response response = metaResource.getLanguageAndPackageMetaInfo("en", "kgp", null, null, "draft-access", null);

		Assert.assertEquals(400, response.getStatus());
	}

	private MetaPackage validateCommonMetaInfo(MetaResults metaResults)
	{
		Set<MetaLanguage> languageSet = metaResults.getLanguages();

		Assert.assertEquals(languageSet.size(), 1);

		MetaLanguage metaLanguage = languageSet.iterator().next();

		Assert.assertEquals(metaLanguage.getCode(), "en");

		Assert.assertEquals(metaLanguage.getPackages().size(), 1);

		MetaPackage metaPackage = metaLanguage.getPackages().iterator().next();

		Assert.assertEquals(metaPackage.getCode(), "kgp");

		return metaPackage;
	}

	private void validateLiveMetaInfo(MetaResults metaResults)
	{
		MetaPackage metaPackage = validateCommonMetaInfo(metaResults);
		Assert.assertEquals(metaPackage.getStatus(), "live");
		Assert.assertEquals(metaPackage.getVersion(), new GodToolsVersion(new BigDecimal("1.1")).toString());
	}

	private void validateDraftMetaInfo(MetaResults metaResults)
	{
		MetaPackage metaPackage = validateCommonMetaInfo(metaResults);
		Assert.assertEquals(metaPackage.getStatus(), "draft");
		Assert.assertEquals(metaPackage.getVersion(), new GodToolsVersion(new BigDecimal("1.2")).toString());
	}
}
