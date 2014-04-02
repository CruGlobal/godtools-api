package org.cru.godtools.api.database;


import org.cru.godtools.properties.GodToolsProperties;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;


import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by ryancarlson on 3/20/14.
 */
@RequestScoped
public class SqlConnectionProducer
{
    @Inject GodToolsProperties properties;

    @Produces
    public Connection getSqlConnection()
    {
        return new Connection(getSql2o());

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
