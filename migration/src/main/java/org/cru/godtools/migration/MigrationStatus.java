package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.sql2o.Connection;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Created by ryancarlson on 7/10/14.
 */
public class MigrationStatus
{

	public static void verifyPackageMigration(Connection sqlConnection)
	{
		int migratedPackages = countMigratedObject("packages", sqlConnection);
		int migratedLanguages = countMigratedObject("languages", sqlConnection);
		int migratedTranslations = countMigratedObject("translations", sqlConnection);
		int migratedPackageStructures = countMigratedObject("package_structure", sqlConnection);
		int migratedPageStructures = countMigratedObject("page_structure", sqlConnection);
		int migratedtranslationElements = countMigratedObject("translation_elements", sqlConnection);

		int expectedNumberOfMigratedPackages = getExpectedNumberOfMigratedPackages();
		int expectedNumberOfMigratedLanguages = getExpectedNumberOfMigratedLanguages();
		int expectedNumberOfMigratedTranslations = getExpectedNumberOfMigratedTranslations();
		int expectedNumberOfMigratedPackageStructures = expectedNumberOfMigratedPackages; // the same thing really... no need to look it up again. guaranteed 1 to 1.
		int expectedNumberOfMigratedPageStructures = getExpectedNumberOfMigratedPageStructures();
		int expectedNumberOfMigratedTranslationElements = getExpectedNumberOfMigratedTranslationElements();

		System.out.println("");
		System.out.println("******************************************");
		System.out.println("*  Migration Status Report:");
		System.out.println("");
		System.out.println("*    - Migrated packages:             " + migratedPackages + " of " + expectedNumberOfMigratedPackages);
		System.out.println("*    - Migrated languages:            " + migratedLanguages + " of " + expectedNumberOfMigratedLanguages);
		System.out.println("*    - Migrated translations:         " + migratedTranslations + " of " + expectedNumberOfMigratedTranslations);
		System.out.println("*    - Migrated package structures:   " + migratedPackageStructures + " of " + expectedNumberOfMigratedPackageStructures);
		System.out.println("*    - Migrated page structures:      " + migratedPageStructures + " of " + expectedNumberOfMigratedPageStructures);
		System.out.println("*    - Migrated translation elements: " + migratedtranslationElements + " of " + expectedNumberOfMigratedTranslationElements);
		System.out.println("");
		System.out.println("******************************************");
		System.out.println("");
	}

	public static void verifyImageMigration(Connection sqlConnection)
	{
		int migratedImages = countMigratedObject("images", sqlConnection);

		int expectedNumberOfMigratedImages = getExpectedNumberOfMigratedImages();

		System.out.println("");
		System.out.println("******************************************");
		System.out.println("*  Migration Status Report:");
		System.out.println("");
		System.out.println("*    - Migrated images:               " + migratedImages + " of " + expectedNumberOfMigratedImages);
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
							if(page.isFile())
							{

								count++;
							}
						}
					}
				}
			}
		}

		return count;
	}

	private static int getExpectedNumberOfMigratedTranslationElements()
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
							if(page.isFile())
							{
								count += XmlDocumentSearchUtilities.findElementsWithAttribute(getPageXml(page), "translate").size();
							}
						}
					}
					// in this case translationDirectory points to a package xml file (en.xml)
					else if(translationDirectory.isFile())
					{
						count += XmlDocumentSearchUtilities.findElementsWithAttribute(getPageXml(translationDirectory), "translate").size();
					}
				}
			}
		}

		return count;
	}

	private static int getExpectedNumberOfMigratedImages()
	{
		File baseDirectory = getBaseDirectory();

		int count = 0;

		for(File packageDirectory : baseDirectory.listFiles())
		{
			if (packageDirectory.isDirectory() && "shared".equals(packageDirectory.getName()))
			{
				for (File sharedImage : packageDirectory.listFiles())
				{
					if (sharedImage.getName().endsWith(".png")) count++;
				}
			}
			else if (packageDirectory.isDirectory())
			{
				for (File translationDirectory : packageDirectory.listFiles())
				{
					if (translationDirectory.isDirectory() && ("shared".equals(translationDirectory.getName()) || "icons".equals(translationDirectory.getName())))
					{
						for(File image : translationDirectory.listFiles())
						{
							if(image.getName().endsWith(".png")) count++;
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

	private static Document getPageXml(File pageFile)
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.parse(pageFile);
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}


}
