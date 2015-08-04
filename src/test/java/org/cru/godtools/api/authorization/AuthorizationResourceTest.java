package org.cru.godtools.api.authorization;

import org.cru.godtools.api.*;
import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
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

/**
 * Created by ryancarlson on 7/31/14.
 */
public class AuthorizationResourceTest extends Arquillian
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

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@BeforeMethod
	public void setup()
	{
		authorizationResource.setAutoCommit(false);
	}

	@AfterMethod
	public void cleanup()
	{
		authorizationResource.rollback();
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
	@Test(expectedExceptions = UnauthorizedException.class)
	public void testGetDraftAuthorizationTokenInvalidAccessCode() throws Exception
	{
		Response response = authorizationResource.getAuthorizationToken("777777", "abcdefg", null);
	}
}
