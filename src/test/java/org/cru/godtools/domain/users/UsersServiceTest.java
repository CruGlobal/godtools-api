package org.cru.godtools.domain.users;

import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
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
 * Created by matthewfrederick on 8/6/14.
 */
public class UsersServiceTest extends Arquillian
{
	public static final UUID TEST_ID = UUID.randomUUID();
	public static final String TEST_USER_ID = "UnitTestUserId";
	public static final String TEST_USER_NAME = "UnitTestUserName";

	@Inject
	UserService userService;

	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(UserService.class, TestClockImpl.class)
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
			userService.sqlConnection.getJdbcConnection().setAutoCommit(false);
		} catch (SQLException e)
		{

		}
		UsersServiceTestMockData.persistUser(userService);
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			userService.sqlConnection.getJdbcConnection().rollback();
		} catch (SQLException e)
		{

		}
	}

	@Test
	public void testSelectById()
	{
		UserRecord userRecord = userService.getUserRecordByUUID(TEST_ID);

		UsersServiceTestMockData.validateUserRecord(userRecord);
	}

	@Test
	public void testSelectByUserId()
	{
		UserRecord userRecord = userService.getUserRecordByUserId(TEST_USER_ID);

		UsersServiceTestMockData.validateUserRecord(userRecord);
	}
}
