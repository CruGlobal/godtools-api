package org.cru.godtools.domain;


import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

/**
 * Creates sql2o connection objects that access the unittest database.  Each request creates a new
 * connection, so callers should be careful to close connections when finished.
 *
 * Created by ryancarlson on 3/20/14.
 */
public class TestSqlConnectionProducer
{
    static GodToolsProperties properties = new GodToolsPropertiesFactory().get();

    public static Connection getTestSqlConnection()
    {
        return new Connection(new Sql2o(properties.getProperty("unittestDatabaseUrl"),
				properties.getProperty("unittestDatabaseUsername"),
				properties.getProperty("unittestDatabasePassword"),
				QuirksMode.PostgreSQL));
    }
}
