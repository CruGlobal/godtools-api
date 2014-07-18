package org.cru.godtools.migration;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
import org.cru.godtools.domain.database.SqlConnectionProducer;

/**
 * Can only be run after domain's migration process is run!
 *
 * Created by ryancarlson on 3/21/14.
 */
public class MigrationProcess
{
    public static void main(String[] args)
    {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:postgresql://localhost/godtools", "godtoolsuser", "godtoolsuser");
		flyway.setTarget(MigrationVersion.fromVersion("0.6"));
		flyway.setLocations("classpath:org.cru.godtools.migration", "classpath:db.migration");
	    flyway.migrate();
    }

    public static org.sql2o.Connection getSql2oConnection()
    {
        return new SqlConnectionProducer().getMigrationSqlConnection();
    }
}
