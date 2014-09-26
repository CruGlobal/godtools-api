package org.cru.godtools.domain.database;


import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
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
    @Inject
	GodToolsProperties properties;

	private Connection sqlConnection;

    @Produces
    public Connection getSqlConnection()
    {
		if(sqlConnection == null) sqlConnection = new Connection(getSql2o());

		return sqlConnection;
    }

    private Sql2o getSql2o()
    {
		// quartz can't do CDI, but I need an sql connection
		if(properties == null) properties = new GodToolsPropertiesFactory().get();

        return new Sql2o(properties.getProperty("databaseUrl"),
                properties.getProperty("databaseUsername"),
                properties.getProperty("databasePassword"),
                QuirksMode.PostgreSQL);
    }


}
