package org.cru.godtools.migration;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
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
        flyway.setInitVersion("0.4");
		flyway.setTarget(MigrationVersion.fromVersion("0.6"));

        flyway.migrate();
    }

    public static org.sql2o.Connection getSql2oConnection()
    {
        return new SqlConnectionProducer().getMigrationSqlConnection();
    }
}
