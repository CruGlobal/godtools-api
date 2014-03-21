package db.migration;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.database.SqlConnectionProducer;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.PackageDirectory;

import java.io.File;
import java.sql.Connection;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class V0_4__migrate_packages implements JdbcMigration
{

    public static void main(String[] args)
    {
        V0_4__migrate_packages proc = new V0_4__migrate_packages();
        
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:postgresql://localhost/godtools", "godtoolsuser", "godtoolsuser");
        flyway.setInitVersion("0");
        flyway.clean();
        flyway.init();
        flyway.migrate();

    }

    private org.sql2o.Connection getSql2oConnection()
    {
        return new SqlConnectionProducer().getTestSqlConnection();
    }

    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = getSql2oConnection();

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            PackageDirectory packageDirectory = new PackageDirectory();
            File directory = packageDirectory.getDirectory(packageCode);

            for(File nextFile : directory.listFiles())
            {
                if(nextFile.isFile() && nextFile.getName().endsWith(".xml"))
                {
                    Package gtPackage = packageDirectory.buildPackage(packageCode, nextFile);

                    PackageService packageService = new PackageService(sqlConnection);
                    packageService.insert(gtPackage);

                    break;
                }
            }
        }

        sqlConnection.commit();
    }




}
