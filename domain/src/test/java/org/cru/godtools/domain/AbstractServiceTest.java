package org.cru.godtools.domain;

import org.sql2o.Connection;

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

		sqlConnection = SqlConnectionProducer.getTestSqlConnection();
	}
}
