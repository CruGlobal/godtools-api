package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.utils.GodToolsVersion;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageServiceTest extends AbstractFullPackageServiceTest
{
	@BeforeClass
	@Override
	public void setup()
	{
		super.setup();
	}

	@Test
	public void testGetPackage()
	{
		GodToolsPackage englishKgpPackage = godToolsPackageService.getPackage(new LanguageCode("en"), "kgp",  new GodToolsVersion(new BigDecimal(1)), 1, true, PixelDensity.getEnum("High"));

		mockData.validateEnglishKgpPackage(englishKgpPackage);
	}

	@Test
	public void testGetPackagesForLanguage()
	{
		Set<GodToolsPackage> englishPackages = godToolsPackageService.getPackagesForLanguage(new LanguageCode("en"), 1, true, PixelDensity.getEnum("High"));

		Assert.assertEquals(englishPackages.size(), 1);
		mockData.validateEnglishKgpPackage(englishPackages.iterator().next());
	}

	@Test
	public void testGetPackageNoMinimumInterpreterSpecified()
	{
		GodToolsPackage englishKpgPackage = godToolsPackageService.getPackage(new LanguageCode("en"), "kgp", new GodToolsVersion(new BigDecimal(1)), null, true, PixelDensity.getEnum("High"));

		mockData.validateEnglishKgpPackage(englishKpgPackage);
	}
}
