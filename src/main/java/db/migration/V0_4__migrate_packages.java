package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;

import java.sql.Connection;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class V0_4__migrate_packages implements JdbcMigration
{


    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            PackageDirectory packageDirectory = new PackageDirectory(packageCode);
            Package gtPackage = packageDirectory.buildPackage();

            PackageService packageService = new PackageService(sqlConnection);
            packageService.insert(gtPackage);
        }
    }
}
