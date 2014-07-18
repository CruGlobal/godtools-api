package org.cru.godtools.api.packages;

import org.cru.godtools.domain.packages.PixelDensity;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageServiceTest extends AbstractFullPackageServiceTest
{

	private GodToolsPackageService godToolsPackageService;

	@BeforeClass
	@Override
	public void setup()
	{
		super.setup();
		godToolsPackageService = createPackageService();
	}

	@Test
	public void testGetPackage()
	{
		GodToolsPackage englishKgpPackage = godToolsPackageService.getPackage(new LanguageCode("en"), "kgp",  new GodToolsVersion(new BigDecimal("1.1")), 1, true, PixelDensity.getEnum("High"));

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
		GodToolsPackage englishKpgPackage = godToolsPackageService.getPackage(new LanguageCode("en"), "kgp", new GodToolsVersion(new BigDecimal("1.1")), null, true, PixelDensity.getEnum("High"));

		mockData.validateEnglishKgpPackage(englishKpgPackage);
	}
}
