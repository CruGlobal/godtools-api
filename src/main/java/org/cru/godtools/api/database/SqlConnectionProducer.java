package org.cru.godtools.api.database;

import com.google.common.base.Throwables;
import org.cru.godtools.properties.GodToolsProperties;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;


import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.sql.SQLException;

/**
 * Created by ryancarlson on 3/20/14.
 */
@RequestScoped
public class SqlConnectionProducer
{

    private Connection sqlConnection;

    @Inject GodToolsProperties properties;

    @Produces
    public org.sql2o.Connection getSqlConnection()
    {
        Connection sqlConnection = new Connection(getSql2o());

        try
        {
            sqlConnection.getJdbcConnection().setAutoCommit(false);
        }
        catch(SQLException e)
        { /*come on... really*/
            Throwables.propagate(e);
        }

        return sqlConnection;
    }

    private Sql2o getSql2o()
    {
        return new Sql2o(properties.getProperty("databaseUrl"),
                properties.getProperty("databaseUsername"),
                properties.getProperty("databasePassword"),
                QuirksMode.PostgreSQL);
    }

    @PreDestroy
    public void returnSqlConnection(Connection sqlConnection)
    {
        sqlConnection.commit();
    }

}
