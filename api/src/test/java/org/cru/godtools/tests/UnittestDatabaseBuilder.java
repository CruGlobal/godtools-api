package org.cru.godtools.tests;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class UnittestDatabaseBuilder
{

	public void build()
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:postgresql://localhost/godtoolstest", "godtoolsuser", "godtoolsuser");
		flyway.setInitVersion("0");
		flyway.setTarget(MigrationVersion.fromVersion("0.2"));
		flyway.clean();
		flyway.init();
		flyway.migrate();
	}

}
