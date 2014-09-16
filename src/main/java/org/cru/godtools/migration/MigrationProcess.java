package org.cru.godtools.migration;

import com.googlecode.flyway.core.Flyway;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.sql2o.Connection;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

/**
 * Can only be run after domain's migration process is run!
 *
 * Created by ryancarlson on 3/21/14.
 */
public class MigrationProcess
{
	static GodToolsProperties properties = new GodToolsPropertiesFactory().get();

    public static void main(String[] args)
    {
        Flyway flyway = new Flyway();
        flyway.setDataSource(properties.getProperty("databaseUrl"), properties.getProperty("databaseUsername"), properties.getProperty("databasePassword"));
		flyway.setInitVersion("0");
		flyway.setLocations("classpath:org.cru.godtools.migration", "classpath:db.migration");
//		flyway.clean();
		flyway.setInitOnMigrate(true);
//	    flyway.migrate();
    }

    public static org.sql2o.Connection getSql2oConnection()
    {
        return new Connection(new Sql2o(properties.getProperty("databaseUrl"), properties.getProperty("databaseUsername") ,properties.getProperty("databasePassword"), QuirksMode.PostgreSQL));
    }
}
