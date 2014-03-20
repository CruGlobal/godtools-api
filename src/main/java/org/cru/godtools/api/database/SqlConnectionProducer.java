package org.cru.godtools.api.database;

import com.google.common.base.Throwables;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;


import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import java.sql.SQLException;

/**
 * Created by ryancarlson on 3/20/14.
 */
@RequestScoped
public class SqlConnectionProducer
{

    private Connection sqlConnection;

    @Produces
    public org.sql2o.Connection getTestSqlConnection()
    {
        Connection sqlConnection = new Connection(new Sql2o("jdbc:postgresql://localhost/godtools","godtoolsuser","godtoolsuser", QuirksMode.PostgreSQL));

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

}
