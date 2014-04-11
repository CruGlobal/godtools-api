package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.EstonianLanguageCode;
import org.cru.godtools.migration.ImageDirectory;
import org.cru.godtools.migration.ImageReader;
import org.cru.godtools.migration.ImagesImageDirectory;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.migration.PageDirectory;
import org.cru.godtools.migration.ThumbsImageDirectory;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                final Language languageForTranslation = languageService.selectLanguageById(translation.getLanguageId());

				new EstonianLanguageCode().addHeartbeatSubculture(languageForTranslation);

				ImagesImageDirectory imagesDirectory = new ImagesImageDirectory("/data/SnuffyPackages/" + packageCode + "/" + languageForTranslation.getPath());

				try
				{
					for (Image image : imagesDirectory.getImagesSet())
					{
						imageService.insert(image);
					}
				}
				catch(NullPointerException npe)
				{
					System.out.println("Skipping directory: " + imagesDirectory.getPath());
				}

				ThumbsImageDirectory thumbsDirectory = new ThumbsImageDirectory("/data/SnuffyPackages/" + packageCode + "/" + languageForTranslation.getPath());

				try
				{
					for(Image image : thumbsDirectory.getImagesSet())
					{
						imageService.insert(image);
					}
				}
				catch(NullPointerException npe)
				{
					System.out.println("Skipping directory: " + thumbsDirectory.getPath());
				}
			}

			ImageDirectory iconImageDirectory = new ImageDirectory("/data/SnuffyPackages/" + packageCode + "/icons");

			try
			{
				for (Image icon : iconImageDirectory.getImagesSet())
				{
					imageService.insert(icon);
				}
			}
			catch(NullPointerException npe)
			{
				System.out.println("Skipping directory: " + iconImageDirectory.getPath());
			}


		}

		ImageDirectory sharedImagesDirectory = new ImageDirectory("/data/SnuffyPackages/shared");

        for(Image image : sharedImagesDirectory.getImagesSet())
        {
            imageService.insert(image);
        }
    }
}
