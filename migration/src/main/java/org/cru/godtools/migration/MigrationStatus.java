package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import org.sql2o.Connection;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by ryancarlson on 7/10/14.
 */
public class MigrationStatus
{

	public static void verifyMigration(Connection sqlConnection)
	{
		int migratedPackages = countMigratedPackages(sqlConnection);
		int expectedNumberOfMigratedPackages = getExpectedNumberOfMigratedPackages();

		System.out.println("");
		System.out.println("******************************************");
		System.out.println("*  Migration Status Report:");
		System.out.println("");
		System.out.println("*    - Migrated packages: " + migratedPackages + " of " + expectedNumberOfMigratedPackages);
		System.out.println("");
		System.out.println("******************************************");
		System.out.println("");
	}

	private static int countMigratedPackages(Connection sqlConnection)
	{
		return new Integer(sqlConnection.createQuery("SELECT count(*) from packages").executeScalar().toString());
	}

	private static int getExpectedNumberOfMigratedPackages()
	{
		File baseDirectory;

		try
		{
			baseDirectory = new File(MigrationStatus.class.getResource(PackageDirectory.DIRECTORY_BASE).toURI());
		}
		catch(URISyntaxException e)
		{
			throw Throwables.propagate(e);
		}

		int count = 0;

		for(File subDirectory : baseDirectory.listFiles())
		{
			if(subDirectory.isDirectory() && !"shared".equals(subDirectory.getName())) count++;
		}

		return count;
	}
}
