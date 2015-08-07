package org.cru.godtools.domain.database;

import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.flywaydb.core.*;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class DatabaseMigration
{
	static GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public static void main(String[] args)
	{
		// i chose an OR here to allow for the process to be invoked from the IDE w/o having to set CLI args.  Jenkins will always pass true or false
		if(args.length == 0 || doDeploy(args[0]))
		{
			String environmentPrefix = args.length > 1 ? (getEnvironmentPrefix(args[1])) : "";

			new DatabaseMigration().build(environmentPrefix);
		}
	}

	public void build(String environmentPrefix)
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource(properties.getProperty(environmentPrefix + "databaseUrl"),
				properties.getProperty(environmentPrefix + "databaseUsername"),
				properties.getProperty(environmentPrefix + "databasePassword"));
		flyway.setInitVersion("0");
		flyway.migrate();
	}

	static boolean doDeploy(String commandLineArg)
	{
		return Boolean.parseBoolean(commandLineArg);
	}

	static String getEnvironmentPrefix(String commandLineArg)
	{
		return  commandLineArg + "_";
	}
}
