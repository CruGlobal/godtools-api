package org.cru.godtools.migration;

import com.googlecode.flyway.core.Flyway;
import org.cru.godtools.api.database.SqlConnectionProducer;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class MigrationProcess
{
    public static void main(String[] args)
    {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:postgresql://localhost/godtools", "godtoolsuser", "godtoolsuser");
        flyway.setInitVersion("0");
        flyway.clean();
        flyway.init();
        flyway.migrate();
    }

    public static org.sql2o.Connection getSql2oConnection()
    {
        return new SqlConnectionProducer().getTestSqlConnection();
    }
}
