package org.cru.godtools.domain.migration;

import com.googlecode.flyway.core.Flyway;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;


public class DatabaseSetup
{
	public static void main(String[] args)
	{
		Flyway flyway = new Flyway();

		GodToolsProperties properties = new GodToolsPropertiesFactory().get();

		flyway.setDataSource(properties.getProperty("databaseUrl"),
				properties.getProperty("databaseUsername"),
				properties.getProperty("databasePassword"));

		flyway.setLocations("db.migration", "db.seed", "org.cru.godtools.domain.migration.versions");

		flyway.clean();
		flyway.setInitOnMigrate(true);
		flyway.migrate();
	}
}
