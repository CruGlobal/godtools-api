package org.cru.godtools.domain.services.sql2o;

import org.cru.godtools.domain.authentication.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.*;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class AuthorizationServiceTest extends Arquillian
{
	public static final UUID TEST_AUTHORIZATION_ID = UUID.randomUUID();

	@Inject
	private AuthorizationService authorizationService;

	@Inject
	Connection sqlConnection;

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
			sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
            /*Do Nothing*/
		}
		AuthorizationMockData.persistAuthorization(authorizationService);
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
            /*Do Nothing*/
		}
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

	@Test(expectedExceptions = UnauthorizedException.class)
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

	@Test(expectedExceptions = UnauthorizedException.class)
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
