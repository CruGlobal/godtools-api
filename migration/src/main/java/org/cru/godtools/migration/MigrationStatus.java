package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.sql2o.Connection;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Created by ryancarlson on 7/10/14.
 */
public class MigrationStatus
{

	public static void verifyMigration(Connection sqlConnection)
	{
		int migratedPackages = countMigratedObject("packages", sqlConnection);
		int migratedLanguages = countMigratedObject("languages", sqlConnection);
		int migratedTranslations = countMigratedObject("translations", sqlConnection);
		int migratedPackageStructures = countMigratedObject("package_structure", sqlConnection);
		int migratedPageStructures = countMigratedObject("page_structure", sqlConnection);

		int expectedNumberOfMigratedPackages = getExpectedNumberOfMigratedPackages();
		int expectedNumberOfMigratedLanguages = getExpectedNumberOfMigratedLanguages();
		int expectedNumberOfMigratedTranslations = getExpectedNumberOfMigratedTranslations();
		int expectedNumberOfMigratedPackageStructures = expectedNumberOfMigratedPackages; // the same thing really... no need to look it up again. guaranteed 1 to 1.
		int expectedNumberOfMigratedPageStructures = getExpectedNumberOfMigratedPageStructures();


		System.out.println("");
		System.out.println("******************************************");
		System.out.println("*  Migration Status Report:");
		System.out.println("");
		System.out.println("*    - Migrated packages:           " + migratedPackages + " of " + expectedNumberOfMigratedPackages);
		System.out.println("*    - Migrated languages:          " + migratedLanguages + " of " + expectedNumberOfMigratedLanguages);
		System.out.println("*    - Migrated translations:       " + migratedTranslations + " of " + expectedNumberOfMigratedTranslations);
		System.out.println("*    - Migrated package structures: " + migratedPackageStructures + " of " + expectedNumberOfMigratedPackageStructures);
		System.out.println("*    - Migrated page structures:    " + migratedPageStructures + " of " + expectedNumberOfMigratedPageStructures);
		System.out.println("");
		System.out.println("******************************************");
		System.out.println("");
	}

	private static int countMigratedObject(String objectName, Connection sqlConnection)
	{
		return new Integer(sqlConnection.createQuery("SELECT count(*) from " + objectName).executeScalar().toString());
	}

	private static int getExpectedNumberOfMigratedPackages()
	{
		File baseDirectory = getBaseDirectory();

		int count = 0;

		for(File packageDirectory : baseDirectory.listFiles())
		{
			if(packageDirectory.isDirectory() && !"shared".equals(packageDirectory.getName())) count++;
		}

		return count;
	}

	private static int getExpectedNumberOfMigratedLanguages()
	{
		File baseDirectory = getBaseDirectory();
		Set<String> encounteredLanguages = Sets.newHashSet();
		int count = 0;

		for(File packageDirectory : baseDirectory.listFiles())
		{
			if(packageDirectory.isDirectory() && !"shared".equals(packageDirectory.getName()))
			{
				for(File translationDirectory : packageDirectory.listFiles())
				{
					if(translationDirectory.isDirectory() && !"icons".equals(translationDirectory.getName()) && !"shared".equals(translationDirectory.getName()))
					{
						if(!encounteredLanguages.contains(translationDirectory.getName()))
						{
							encounteredLanguages.add(translationDirectory.getName());
							count++;
						}
					}
				}
			}
		}
		return count;
	}

	private static int getExpectedNumberOfMigratedTranslations()
	{
		File baseDirectory = getBaseDirectory();

		int count = 0;

		for(File packageDirectory : baseDirectory.listFiles())
		{
			if(packageDirectory.isDirectory() && !"shared".equals(packageDirectory.getName()))
			{
				for(File translationDirectory : packageDirectory.listFiles())
				{
					if(translationDirectory.isDirectory() && !"shared".equals(translationDirectory.getName()) && !"icons".equals(translationDirectory.getName()))
					{
							count++;
					}
				}
			}
		}
		return count;
	}


	private static int getExpectedNumberOfMigratedPageStructures()
	{
		File baseDirectory = getBaseDirectory();

		int count = 0;

		for(File packageDirectory : baseDirectory.listFiles())
		{
			if(packageDirectory.isDirectory() && !"shared".equals(packageDirectory.getName()))
			{
				for(File translationDirectory : packageDirectory.listFiles())
				{
					if(translationDirectory.isDirectory() && !"shared".equals(translationDirectory.getName()) && !"icons".equals(translationDirectory.getName()))
					{
						for(File page : translationDirectory.listFiles())
						{
							if(page.isFile()) count++;
						}
					}
				}
			}
		}

		return count;
	}

	private static File getBaseDirectory()
	{
		try
		{
			return new File(MigrationStatus.class.getResource(PackageDirectory.DIRECTORY_BASE).toURI());
		}
		catch(URISyntaxException e)
		{
			throw Throwables.propagate(e);
		}
	}
}
