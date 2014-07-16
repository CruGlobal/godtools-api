package org.cru.godtools.migration;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
import org.cru.godtools.domain.database.SqlConnectionProducer;

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
		flyway.setTarget(MigrationVersion.fromVersion("0.6"));
		flyway.setLocations("classpath:org.cru.godtools.migration", "classpath:db.migration");
		flyway.clean();
		flyway.setInitOnMigrate(true);
        flyway.migrate();
    }

    public static org.sql2o.Connection getSql2oConnection()
    {
        return new SqlConnectionProducer().getMigrationSqlConnection();
    }
}