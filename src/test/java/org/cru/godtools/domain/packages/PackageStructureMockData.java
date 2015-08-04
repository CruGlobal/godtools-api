package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.model.Package;
import org.cru.godtools.domain.services.*;
import org.testng.*;

/**
 * Created by justinsturm on 7/31/15.
 */
public class PackageStructureMockData
{
    public static org.cru.godtools.domain.model.Package persistPackage(PackageService packageService)
    {
        Package gtPackage = new Package();
        gtPackage.setId(PackageStructureServiceTest.TEST_PACKAGE_ID);
        gtPackage.setName("Test Package");
        gtPackage.setCode("tp");

        packageService.insert(gtPackage);

        return gtPackage;
    }

    public static void persistPackageStructure(PackageStructureService packageStructureService, Package gtPackage)
    {
        PackageStructure packageStructure = new PackageStructure();
        packageStructure.setId(PackageStructureServiceTest.TEST_PACKAGE_STRUCTURE_ID);
        packageStructure.setPackage(gtPackage);

        packageStructureService.insert(packageStructure);
    }

    public static void validatePackageStructure(PackageStructure packageStructure)
    {
        Assert.assertNotNull(packageStructure);
        Assert.assertEquals(packageStructure.getId(), PackageStructureServiceTest.TEST_PACKAGE_STRUCTURE_ID);
        Assert.assertEquals(packageStructure.getPackage().getId(), PackageStructureServiceTest.TEST_PACKAGE_ID);
    }
}
