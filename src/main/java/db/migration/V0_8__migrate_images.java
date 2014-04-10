package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.ImageReader;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.migration.PageDirectory;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_8__migrate_images implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        LanguageService languageService = new LanguageService(sqlConnection);
        PackageService packageService = new PackageService(sqlConnection);
        TranslationService translationService = new TranslationService(sqlConnection);
        ImageService imageService = new ImageService(sqlConnection);

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            Package gtPackage = packageService.selectByCode(packageCode);
            List<Translation> translationsForPackage = translationService.selectByPackageId(gtPackage.getId());

            for(Translation translation : translationsForPackage)
            {
                Language languageForTranslation = languageService.selectLanguageById(translation.getLanguageId());
                PageDirectory pageDirectory = new PageDirectory(packageCode, languageForTranslation.getPath());

                for(Image image : pageDirectory.buildImages())
                {
                    imageService.insert(image);
                }
            }

            PackageDirectory packageDirectory = new PackageDirectory(packageCode);

            for(Image icon : packageDirectory.buildIcons())
            {
                imageService.insert(icon);
            }
        }

        URL url = this.getClass().getResource("/data/SnuffyPackages/shared");
        File sharedDirectory = new File(url.toURI());

        for(File sharedImage : sharedDirectory.listFiles())
        {
            Image image = new Image();
            image.setId(UUID.randomUUID());
            image.setFilename(sharedImage.getName());
            image.setImageContent(ImageReader.read(sharedImage));
            image.setImageHash(ShaGenerator.calculateHash(image.getImageContent()));
            image.setResolution("High");
            imageService.insert(image);
        }
    }
}
