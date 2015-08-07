package org.cru.godtools.domain.services;

import org.cru.godtools.domain.authentication.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.utils.collections.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.*;
import org.junit.runner.*;
import org.testng.Assert;

import javax.inject.Inject;
import javax.transaction.*;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/3/14.
 */
@RunWith(Arquillian.class)
public class AuthorizationServiceTest
{
	public static final UUID TEST_AUTHORIZATION_ID = UUID.randomUUID();

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

	@Inject
	private AuthorizationService authorizationService;

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
		AuthorizationMockData.persistAuthorization(authorizationService);
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

	//TODO Write test cases for JPA mapping

	@Test
	public void testCheckAuthorizationFromParam()
	{
		authorizationService.getAuthorizationRecord("a", null);
	}

	@Test
	public void testCheckAuthorizationFromHeader()
	{
		authorizationService.getAuthorizationRecord(null, "a");
	}

	@Test(expected = UnauthorizedException.class)
	public void testCheckAuthorizationFromParamFailed()
	{
		try
		{
			AuthorizationRecord.checkAuthorization(authorizationService.getAuthorizationRecord("b", null), new TestClockImpl().currentDateTime());
		}
		catch(UnauthorizedException exception)
		{
			Assert.assertEquals(exception.getResponse().getStatus(), 401);
			throw exception;
		}
	}

	@Test(expected = UnauthorizedException.class)
	public void testCheckAuthorizationFromHeaderFailed()
	{
		try
		{
			AuthorizationRecord.checkAuthorization(authorizationService.getAuthorizationRecord(null, "b"), new TestClockImpl().currentDateTime());
		}
		catch(UnauthorizedException exception)
		{
			Assert.assertEquals(exception.getResponse().getStatus(), 401);
			throw exception;
		}
	}
}
