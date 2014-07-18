package org.cru.godtools.domain;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class UnittestDatabaseBuilder
{
	public static void main(String[] args)
	{
		new UnittestDatabaseBuilder().build();
	}

	public void build()
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:postgresql://localhost/godtoolstest", "godtoolsuser", "godtoolsuser");
		flyway.setInitVersion("0");
		flyway.setTarget(MigrationVersion.fromVersion("0.3"));
		flyway.clean();
		flyway.init();
		flyway.migrate();
	}
}
