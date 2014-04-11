package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.EstonianLanguageCode;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.migration.TranslationsToMigrate;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_5__migrate_languages_translations implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        LanguageService languageService = new LanguageService(sqlConnection);
        PackageService packageService = new PackageService(sqlConnection);
        TranslationService translationService = new TranslationService(sqlConnection);

		TranslationsToMigrate translationsToMigrate = new TranslationsToMigrate();

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            PackageDirectory packageDirectory = new PackageDirectory(packageCode);

            List<Language> languages = packageDirectory.buildLanguages();

            for(Language language : languages)
            {
				new EstonianLanguageCode().removeHeartbeatSubculture(language);

				if(! translationsToMigrate.isDesiredTranslation(LanguageCode.fromLanguage(language))) continue;
                if(!languageService.languageExists(language))
                {
                    languageService.insert(language);
                }

                Language retrievedLanguage = languageService.selectByLanguageCode(LanguageCode.fromLanguage(language));
                translationService.insert(new Translation(packageService.selectByCode(packageCode),retrievedLanguage));
            }
        }
    }
}
