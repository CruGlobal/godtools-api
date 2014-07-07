package org.cru.godtools.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.TranslationService;

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
		for(Package gtPackage : packages)
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
