package org.cru.godtools.api.packages.domain;


import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.api.utilities.ResourceNotFoundException;
import org.cru.godtools.tests.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class VersionServiceTest extends AbstractServiceTest
{
	VersionServiceTestMockDataService mockData;

	VersionService versionService;

	public static final UUID TEST_TRANSLATION_ID = UUID.randomUUID();
	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();

	public static final UUID TEST_VERSION_THREE_ID = UUID.randomUUID();
	public static final UUID TEST_VERSION_TWO_ID = UUID.randomUUID();

	@Override
	@BeforeClass
	public void setup()
	{
		super.setup();

		versionService = new VersionService(sqlConnection);

		mockData = new VersionServiceTestMockDataService();
		mockData.persistTranslation(new TranslationService(sqlConnection));
		mockData.persistPackage(new PackageService(sqlConnection));
		mockData.persistVersionThree(versionService);
		mockData.persistVersionTwo(versionService);

	}

	@Test
	public void testSelectByTranslationId()
	{
		List<Version> versions = versionService.selectByTranslationId(TEST_TRANSLATION_ID);
		Assert.assertEquals(versions.size(), 2);

		mockData.validateAllVersions(versions);
	}

	@Test
	public void testSelectLatestVersionForTranslation()
	{
		Version version = versionService.selectLatestVersionForTranslation(TEST_TRANSLATION_ID);

		mockData.validateVersionThree(version);
	}

	@Test
	public void testSelectLatestVersionForTranslationWithMinimumInterpreterVersion()
	{
		Version version = versionService.selectLatestVersionForTranslation(TEST_TRANSLATION_ID, 3);

		mockData.validateVersionThree(version);

		Version sameVersion = versionService.selectLatestVersionForTranslation(TEST_TRANSLATION_ID, 1);

		mockData.validateVersionThree(sameVersion);
	}

	@Test
	public void testSelectSpecificVersionForTranslation()
	{
		Version versionTwo = versionService.selectSpecificVersionForTranslation(TEST_TRANSLATION_ID, 2);

		mockData.validateVersionTwo(versionTwo);

		Version versionThree = versionService.selectSpecificVersionForTranslation(TEST_TRANSLATION_ID, 3);

		mockData.validateVersionThree(versionThree);
	}

	@Test
	public void testSelectSpecificVersionForTranslationsWithMinimumInterpreterVersion()
	{
		Version versionThree = versionService.selectSpecificVersionForTranslation(TEST_TRANSLATION_ID, 3, 3);

		mockData.validateVersionThree(versionThree);

		Version sameVersionThree = versionService.selectSpecificVersionForTranslation(TEST_TRANSLATION_ID, 3, 1);

		mockData.validateVersionThree(sameVersionThree);
	}

	@Test(expectedExceptions = ResourceNotFoundException.class)
	public void testSelectSpecificVersionForTranslationWithMinimumInterpreterVersionTooHigh()
	{
		versionService.selectSpecificVersionForTranslation(TEST_TRANSLATION_ID, 3, 4);
	}

	@Test
	public void testUpdate()
	{
		mockData.modifyVersion(versionService);

		Version modifiedVersion = versionService.selectLatestVersionForTranslation(TEST_TRANSLATION_ID);

		mockData.validateModifiedVersion(modifiedVersion);
	}
}
