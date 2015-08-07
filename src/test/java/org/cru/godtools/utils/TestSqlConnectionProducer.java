package org.cru.godtools.utils;


import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Creates sql2o connection objects that access the unittest database.  Each request creates a new
 * connection, so callers should be careful to close connections when finished.
 *
 * Created by ryancarlson on 3/20/14.
 */
public class TestSqlConnectionProducer
{
	@Inject
    GodToolsProperties properties;

	private static Connection connection;

	@Produces
    public Connection getTestSqlConnection()
    {
		GodToolsProperties properties = resolveProperties();

		if(connection == null)
		{
			connection = new Connection(new Sql2o(properties.getProperty("unittestDatabaseUrl"),
					properties.getProperty("unittestDatabaseUsername"),
					properties.getProperty("unittestDatabasePassword"),
					QuirksMode.PostgreSQL));
		}

		return connection;
    }

	public static Connection getConnection()
	{
		return connection;
	}

	private GodToolsProperties resolveProperties()
	{
		GodToolsProperties properties;

		if(this.properties != null)
		{
			properties = this.properties;
		}
		else
		{
			properties = new GodToolsPropertiesFactory().get();
		}
		return properties;
	}
}
