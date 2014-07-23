package org.cru.godtools.domain;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class DatabaseMigration
{
	static GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public static void main(String[] args)
	{
		new DatabaseMigration().build();
	}

	public void build()
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource(properties.getProperty("databaseUrl"), properties.getProperty("databaseUsername") ,properties.getProperty("databasePassword"));
		flyway.setInitVersion("0");
		flyway.setTarget(MigrationVersion.fromVersion("0.3"));
		flyway.clean();
		flyway.init();
		flyway.migrate();
	}
}
