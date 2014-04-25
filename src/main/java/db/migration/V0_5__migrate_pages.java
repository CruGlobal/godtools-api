package db.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.images.FileSystemImageLookup;
import org.cru.godtools.api.images.ImageLookup;
import org.cru.godtools.api.images.ImageSet;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.images.domain.ReferencedImage;
import org.cru.godtools.api.images.domain.ReferencedImageService;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.InMemoryPageLookup;
import org.cru.godtools.api.packages.PageLookup;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageService;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.domain.VersionService;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PageDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_5__migrate_pages implements JdbcMigration
{
	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	LanguageService languageService = new LanguageService(sqlConnection);
	PackageService packageService = new PackageService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);
	VersionService versionService = new VersionService(sqlConnection);
	PageService pageService = new PageService(sqlConnection);
	ReferencedImageService referencedImageService = new ReferencedImageService(sqlConnection);
	ImageService imageService = new ImageService(sqlConnection,  referencedImageService);


    @Override
    public void migrate(Connection connection) throws Exception
    {
        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
            Package gtPackage = packageService.selectByCode(packageCode);
            List<Translation> translationsForPackage = translationService.selectByPackageId(gtPackage.getId());
			ImageLookup imageLookup = getImageLookupUtility(packageCode);

            for(Translation translation : translationsForPackage)
            {
                Language languageForTranslation = languageService.selectLanguageById(translation.getLanguageId());
				PageDirectory pageDirectory = new PageDirectory(packageCode, languageForTranslation.getPath());
				Version latestVersion = versionService.selectLatestVersionForTranslation(translation.getId());

				List<Page> pages = pageDirectory.buildPages();
				for(Page page : pages)
                {
					ImageSet imageSet = new ImageSet(page.getReferencedImages());
					imageSet.saveImages(imageService, imageLookup);

					page.replaceImageNamesWithImageHashes(imageLookup);
					page.setVersionId(latestVersion.getId());

                    pageService.insert(page);

					imageSet.saveReferencedImages(referencedImageService, page, latestVersion);
                }
				latestVersion.replacePageNamesWithPageHashes(getPageLookupUtility(pages));
				versionService.update(latestVersion);
			}
        }
    }

	private ImageLookup getImageLookupUtility(String packageCode)
	{
		return new FileSystemImageLookup("/data/SnuffyPackages/" + packageCode);
	}

	private PageLookup getPageLookupUtility(List<Page> pages)
	{
		return new InMemoryPageLookup(pages);
	}

}