package org.cru.godtools.api.packages.domain;

import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.testng.Assert;

import java.util.List;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class VersionServiceTestMockDataService
{
	public void persistVersionThree(VersionService versionService)
	{
		Version version = new Version();
		version.setId(VersionServiceTest.TEST_VERSION_THREE_ID);
		version.setVersionNumber(3);
		version.setMinimumInterpreterVersion(3);
		version.setPackageId(VersionServiceTest.TEST_PACKAGE_ID);
		version.setTranslationId(VersionServiceTest.TEST_TRANSLATION_ID);
		version.setPackageStructure(null);
		version.setPackageStructureHash("afsdfhsd");
		version.setReleased(false);

		versionService.insert(version);
	}

	public void validateVersionThree(Version versionThree)
	{
		Assert.assertNotNull(versionThree);
		Assert.assertEquals(versionThree.getId(), VersionServiceTest.TEST_VERSION_THREE_ID);
		Assert.assertEquals(versionThree.getMinimumInterpreterVersion(), (Integer)3);
		Assert.assertEquals(versionThree.getVersionNumber(), (Integer)3);
		Assert.assertEquals(versionThree.getPackageStructureHash(), "afsdfhsd");
		Assert.assertEquals(versionThree.getPackageId(), VersionServiceTest.TEST_PACKAGE_ID);
		Assert.assertEquals(versionThree.getTranslationId(), VersionServiceTest.TEST_TRANSLATION_ID);
		Assert.assertFalse(versionThree.isReleased());
	}

	public void persistVersionTwo(VersionService versionService)
	{
		Version version = new Version();
		version.setId(VersionServiceTest.TEST_VERSION_TWO_ID);
		version.setVersionNumber(2);
		version.setMinimumInterpreterVersion(2);
		version.setPackageId(VersionServiceTest.TEST_PACKAGE_ID);
		version.setTranslationId(VersionServiceTest.TEST_TRANSLATION_ID);
		version.setPackageStructure(null);
		version.setPackageStructureHash("fhdsadsv");
		version.setReleased(true);

		versionService.insert(version);
	}

	public void validateVersionTwo(Version versionTwo)
	{
		Assert.assertNotNull(versionTwo);
		Assert.assertEquals(versionTwo.getId(), VersionServiceTest.TEST_VERSION_TWO_ID);
		Assert.assertEquals(versionTwo.getMinimumInterpreterVersion(), (Integer)2);
		Assert.assertEquals(versionTwo.getVersionNumber(), (Integer)2);
		Assert.assertEquals(versionTwo.getPackageStructureHash(), "fhdsadsv");
		Assert.assertEquals(versionTwo.getPackageId(), VersionServiceTest.TEST_PACKAGE_ID);
		Assert.assertEquals(versionTwo.getTranslationId(), VersionServiceTest.TEST_TRANSLATION_ID);
		Assert.assertTrue(versionTwo.isReleased());
	}

	public void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(VersionServiceTest.TEST_PACKAGE_ID);

		packageService.insert(gtPackage);
	}

	public void persistTranslation(TranslationService translationService)
	{
		Translation translation = new Translation();
		translation.setId(VersionServiceTest.TEST_TRANSLATION_ID);

		translationService.insert(translation);
	}

	public void validateAllVersions(List<Version> versions)
	{
		for(Version version : versions)
		{
			if(version.getId().equals(VersionServiceTest.TEST_VERSION_THREE_ID)) validateVersionThree(version);
			else if(version.getId().equals(VersionServiceTest.TEST_VERSION_TWO_ID)) validateVersionTwo(version);
			else Assert.fail("Unknown version found");
		}
	}

	public void modifyVersion(VersionService versionService)
	{
		Version version = new Version();
		version.setId(VersionServiceTest.TEST_VERSION_THREE_ID);
		version.setVersionNumber(3);
		version.setMinimumInterpreterVersion(3);
		version.setPackageId(VersionServiceTest.TEST_PACKAGE_ID);
		version.setTranslationId(VersionServiceTest.TEST_TRANSLATION_ID);
		version.setPackageStructure(null);
		version.setPackageStructureHash("adgsdfss");
		version.setReleased(true);

		versionService.update(version);
	}

	public void validateModifiedVersion(Version modifiedVersion)
	{
		Assert.assertNotNull(modifiedVersion);
		Assert.assertEquals(modifiedVersion.getId(), VersionServiceTest.TEST_VERSION_THREE_ID);
		Assert.assertEquals(modifiedVersion.getMinimumInterpreterVersion(), (Integer)3);
		Assert.assertEquals(modifiedVersion.getVersionNumber(), (Integer)3);
		Assert.assertEquals(modifiedVersion.getPackageStructureHash(), "adgsdfss");
		Assert.assertEquals(modifiedVersion.getPackageId(), VersionServiceTest.TEST_PACKAGE_ID);
		Assert.assertEquals(modifiedVersion.getTranslationId(), VersionServiceTest.TEST_TRANSLATION_ID);
		Assert.assertTrue(modifiedVersion.isReleased());
	}

}
