package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageService;
import org.cru.godtools.api.packages.domain.VersionService;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.translations.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PageDirectory;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_7__migrate_pages implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        LanguageService languageService = new LanguageService(sqlConnection);
        PackageService packageService = new PackageService(sqlConnection);
        TranslationService translationService = new TranslationService(sqlConnection);
        VersionService versionService = new VersionService(sqlConnection);
        PageService pageService = new PageService(sqlConnection);

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            Package gtPackage = packageService.selectByCode(packageCode);
            List<Translation> translationsForPackage = translationService.selectByPackageId(gtPackage.getId());

            for(Translation translation : translationsForPackage)
            {
                Language languageForTranslation = languageService.selectLanguageById(translation.getLanguageId());
                PageDirectory pageDirectory = new PageDirectory(packageCode, languageForTranslation.getPath());

                for(Page page : pageDirectory.buildPages(versionService.selectLatestVersionForTranslation(translation.getId())))
                {
                    pageService.insert(page);
                }
            }

        }

        sqlConnection.commit();
    }

}