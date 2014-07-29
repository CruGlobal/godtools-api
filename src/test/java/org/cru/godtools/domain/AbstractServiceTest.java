package org.cru.godtools.domain;

import org.sql2o.Connection;
import org.testng.annotations.AfterClass;

import java.sql.SQLException;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractServiceTest
{
	protected UnittestDatabaseBuilder builder;
	protected Connection sqlConnection;

	public void setup()
	{
		builder = new UnittestDatabaseBuilder();
		builder.build();

		sqlConnection = TestSqlConnectionProducer.getTestSqlConnection();
	}

	@AfterClass
	public void teardown()
	{
		try
		{
			sqlConnection.getJdbcConnection().close();
		}
		catch(SQLException sqlException)
		{
			/* move along */
		}
	}
}
