package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.testng.*;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.*;
import org.jboss.shrinkwrap.api.spec.*;
import org.testng.*;
import org.testng.annotations.*;

import javax.inject.*;
import java.util.*;

/**
 * Created by justinsturm on 7/31/15.
 */
public class PackageStructureServiceTest extends Arquillian
{
    public static final UUID TEST_PACKAGE_ID = UUID.randomUUID();
    public static final UUID TEST_PACKAGE_STRUCTURE_ID = UUID.randomUUID();

    @Inject
    PackageStructureService packageStructureService;
    @Inject
    PackageService packageService;

    @Deployment
    public static JavaArchive createDeployment()
    {
        Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(sql2oTestClassCollection.getClasses())
                .addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
                .addClasses(TestClockImpl.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @BeforeClass
    public void initializeDatabase()
    {
        UnittestDatabaseBuilder.build();
    }

    @BeforeMethod
    public void setup()
    {
        packageService.setAutoCommit(false);
        packageStructureService.setAutoCommit(false);

        org.cru.godtools.domain.model.Package gtPackage = PackageStructureMockData.persistPackage(packageService);
        PackageStructureMockData.persistPackageStructure(packageStructureService, gtPackage);
    }

    @AfterMethod
    public void cleanup()
    {
        packageService.rollback();
        packageStructureService.rollback();
    }

    //@Test
    public void testSelectById()
    {
        PackageStructure packageStructure = packageStructureService.selectByPackageId(TEST_PACKAGE_ID);

        PackageStructureMockData.validatePackageStructure(packageStructure);
    }

    //@Test
    public void testSelectAll()
    {
        List<PackageStructure> packageStructures = packageStructureService.selectAll();

        Assert.assertEquals(packageStructures.size(),1);
    }
}
