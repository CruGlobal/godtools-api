package org.cru.godtools.api.resources;

import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.utils.collections.*;
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

/**
 * Created by ryancarlson on 7/31/14.
 */
@RunWith(Arquillian.class)
public class AuthorizationResourceTest
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(AuthorizationResource.class, TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	AuthorizationResource authorizationResource;

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
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

	/**
	 * Tests getting a generic auth token, with draft access set to false.
	 */
	@Test
	public void testGetGenericAuthorizationToken() throws Exception
	{
		Response response = authorizationResource.getAuthorizationToken("abcdefg", null);

		Assert.assertEquals(response.getStatus(), 204);
		Assert.assertNotNull(response.getHeaderString("Authorization"));

		authorizationResource.requestAuthStatus(null, response.getHeaderString("Authorization"));
	}

	/**
	 *  Tests getting an access token with access to drafts.  Must pass a valid access code.
	 */
	@Test
	public void testGetDraftAuthorizationToken() throws Exception
	{
		Response response = authorizationResource.getAuthorizationToken("123456", "abcdefg", null);

		Assert.assertEquals(response.getStatus(), 204);
		Assert.assertNotNull(response.getHeaderString("Authorization"));

		authorizationResource.requestAuthStatus(null, response.getHeaderString("Authorization"));
	}

	/**
	 * Tests passing an invalid access code to draft access endpoint.  Should result in a 401
	 * Unauthorized exception
	 */
	@Test(expected = UnauthorizedException.class)
	public void testGetDraftAuthorizationTokenInvalidAccessCode() throws Exception
	{
		Response response = authorizationResource.getAuthorizationToken("777777", "abcdefg", null);
	}
}
