package org.cru.godtools.domain;


import org.cru.godtools.properties.GodToolsProperties;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Created by ryancarlson on 3/20/14.
 */
@RequestScoped
public class SqlConnectionProducer
{
    @Inject GodToolsProperties properties;

	private Connection sqlConnection;

    @Produces
    public Connection getSqlConnection()
    {
		if(sqlConnection == null) sqlConnection = new Connection(getSql2o());

		return sqlConnection;
    }

	public static Connection getMigrationSqlConnection()
	{
		return new Connection(new Sql2o("jdbc:postgresql://localhost/godtools", "godtoolsuser", "godtoolsuser", QuirksMode.PostgreSQL));
	}

    public static Connection getTestSqlConnection()
    {
        return new Connection(new Sql2o("jdbc:postgresql://localhost/godtoolstest", "godtoolsuser", "godtoolsuser", QuirksMode.PostgreSQL));
    }

    private Sql2o getSql2o()
    {
        return new Sql2o(properties.getProperty("databaseUrl"),
                properties.getProperty("databaseUsername"),
                properties.getProperty("databasePassword"),
                QuirksMode.PostgreSQL);
    }


}
