package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.ImageDirectory;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;

import java.sql.Connection;

import static org.cru.godtools.migration.KnownGodtoolsPackages.packages;

/**
 * Created by ryancarlson on 5/12/14.
 */
public class V0_5__migrate_images implements JdbcMigration
{

	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	LanguageService languageService = new LanguageService(sqlConnection);
	PackageService packageService = new PackageService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);
	PackageStructureService packageStructureService = new PackageStructureService(sqlConnection);
	PageStructureService pageStructureService = new PageStructureService(sqlConnection);
	TranslationElementService translationElementService = new TranslationElementService(sqlConnection);

	ImageService imageService = new ImageService(sqlConnection);

	@Override
	public void migrate(Connection connection) throws Exception
	{
		for(org.cru.godtools.api.packages.domain.Package gtPackage : packages)
		{
			PackageDirectory packageDirectory = new PackageDirectory(gtPackage.getCode(),
					packageService,
					languageService,
					translationService,
					translationElementService,
					packageStructureService,
					pageStructureService);

			for(Image image : packageDirectory.getSharedImageDirectory().buildImages(gtPackage.getCode()))
			{
				imageService.insert(image);
			}

			for(Image image : packageDirectory.getIconDirectory().buildImages(gtPackage.getCode()))
			{
				imageService.insert(image);
			}
		}

		for(Image image : ImageDirectory.getSharedImagesDirectory().buildImages("shared"))
		{
			imageService.insert(image);
		}
	}
}
