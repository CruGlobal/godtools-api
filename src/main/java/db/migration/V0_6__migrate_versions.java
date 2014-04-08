package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.GodToolsPackageShaGenerator;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.api.packages.domain.Package;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_6__migrate_versions implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        LanguageService languageService = new LanguageService(sqlConnection);
        PackageService packageService = new PackageService(sqlConnection);
        TranslationService translationService = new TranslationService(sqlConnection);
        VersionService versionService = new VersionService(sqlConnection);

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            PackageDirectory packageDirectory = new PackageDirectory(packageCode);

            Package gtPackage = packageService.selectByCode(packageCode);
            List<Translation> translations = translationService.selectByPackageId(gtPackage.getId());

            for(Translation translation : translations)
            {
                Language language = languageService.selectLanguageById(translation.getLanguageId());

                Version version = new Version(gtPackage, translation, 1, true);

                version.setPackageStructure(packageDirectory.getPackageDescriptorXml(language));
                version.setPackageStructureHash(new GodToolsPackageShaGenerator().calculateHash(version.getPackageStructure()));
                versionService.insert(version);
            }

        }
    }
}
