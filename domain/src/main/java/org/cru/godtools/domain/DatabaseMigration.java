package org.cru.godtools.domain;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class DatabaseMigration
{

	public static void main(String[] args)
	{
		new DatabaseMigration().build();
	}

	public void build()
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:postgresql://localhost/godtools", "godtoolsuser", "godtoolsuser");
		flyway.setInitVersion("0");
		flyway.setTarget(MigrationVersion.fromVersion("0.3"));
		flyway.clean();
		flyway.init();
		flyway.migrate();
	}
}
