package org.cru.godtools.domain.packages;

import org.testng.Assert;

import java.lang.*;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PackageServiceTestMockDataService
{
	public void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(PackageServiceTest.TEST_PACKAGE_ID);
		gtPackage.setName("Test Package");
		gtPackage.setCode("tp");

		packageService.insert(gtPackage);

	}

	public void validatePackage(Package gtPackage)
	{
		Assert.assertNotNull(gtPackage);
		Assert.assertEquals(gtPackage.getId(), PackageServiceTest.TEST_PACKAGE_ID);
		Assert.assertEquals(gtPackage.getName(), "Test Package");
		Assert.assertEquals(gtPackage.getCode(), "tp");
		Assert.assertNull(gtPackage.getDefaultLanguageId());
	}
}
