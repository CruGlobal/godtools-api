package org.cru.godtools.domain;

import org.cru.godtools.domain.properties.GodToolsProperties;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;

/**
 * Created by ryancarlson on 7/22/14.
 */
@RequestScoped
public class SqlConnectionProducer
{

	private Sql2o sqlConnection;

	@Inject GodToolsProperties properties;

	@Produces
	public Sql2o getSqlConnection()
	{
		if(sqlConnection == null)
		{
			sqlConnection = new Sql2o(properties.getProperty("databaseUrl"),
					properties.getProperty("databaseUsername"),
					properties.getProperty("databasePassword"),
					QuirksMode.PostgreSQL);
		}
		
		return sqlConnection;

	}
}
