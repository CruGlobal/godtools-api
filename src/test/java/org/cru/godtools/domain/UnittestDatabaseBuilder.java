package org.cru.godtools.domain;

import com.googlecode.flyway.core.Flyway;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class UnittestDatabaseBuilder
{
	static GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public static void main(String[] args)
	{
		build();
	}

	public static void build()
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource(properties.getProperty("unittestDatabaseUrl"),
				properties.getProperty("unittestDatabaseUsername"),
				properties.getProperty("unittestDatabasePassword"));
		flyway.setInitVersion("0");
		flyway.clean();
		flyway.init();
		flyway.migrate();
	}
}
