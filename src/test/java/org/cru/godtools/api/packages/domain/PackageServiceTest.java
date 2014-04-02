package org.cru.godtools.api.packages.domain;

import org.cru.godtools.tests.AbstractServiceTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PackageServiceTest extends AbstractServiceTest
{
	PackageServiceTestMockDataService mockData;

	PackageService packageService;

	public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();

	@BeforeClass
	public void setup()
	{
		super.setup();

		packageService = new PackageService(sqlConnection);

		mockData = new PackageServiceTestMockDataService();
		mockData.persistPackage(packageService);
	}

	@Test
	public void testSelectById()
	{
		Package gtPackage = packageService.selectById(TEST_PACKAGE_ID);

		mockData.validatePackage(gtPackage);
	}

	@Test
	public void testSelectByCode()
	{
		Package gtPackage = packageService.selectByCode("tp");

		mockData.validatePackage(gtPackage);
	}

}
