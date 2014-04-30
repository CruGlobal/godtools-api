package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.images.FileSystemImageLookup;
import org.cru.godtools.api.images.ImageLookup;
import org.cru.godtools.api.images.ImageSet;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.images.domain.ReferencedImageService;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.domain.VersionService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class V0_4__migrate_packages implements JdbcMigration
{

	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	LanguageService languageService = new LanguageService(sqlConnection);
	PackageService packageService = new PackageService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);
	VersionService versionService = new VersionService(sqlConnection);
 	ReferencedImageService referencedImageService = new ReferencedImageService(sqlConnection);
	ImageService imageService = new ImageService(sqlConnection, referencedImageService);

	@Override
    public void migrate(Connection connection) throws Exception
    {

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
			savePackage(packageCode);
			saveLanguages(packageCode);
			saveTranslations(packageCode);
//			saveVersions(packageCode);
		}
    }

	private void savePackage(String packageCode) throws Exception
	{
		PackageDirectory packageDirectory = new PackageDirectory(packageCode);
		Package gtPackage = packageDirectory.buildPackage();

		packageService.insert(gtPackage);
	}


	private void saveLanguages(String packageCode) throws Exception
	{
		PackageDirectory packageDirectory = new PackageDirectory(packageCode);

		List<Language> languages = packageDirectory.buildLanguages();

		for(Language language : languages)
		{
			if(!languageService.languageExists(language))
			{
				languageService.insert(language);
			}
		}
	}

	private void saveTranslations(String packageCode) throws Exception
	{
		PackageDirectory packageDirectory = new PackageDirectory(packageCode);

		List<Language> languages = packageDirectory.buildLanguages();

		for(Language language : languages)
		{
			Language retrievedLanguage = languageService.selectByLanguageCode(LanguageCode.fromLanguage(language));
			translationService.insert(new Translation(packageService.selectByCode(packageCode),retrievedLanguage));
		}
	}

	private void saveVersions(String packageCode) throws Exception
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		List<Translation> translations = translationService.selectByPackageId(gtPackage.getId());
		PackageDirectory packageDirectory = new PackageDirectory(packageCode);

		for(Translation translation : translations)
		{
			Language language = languageService.selectLanguageById(translation.getLanguageId());

			Version version = new Version(translation, 1, true);

			version.setPackageStructure(packageDirectory.getPackageDescriptorXml(language));

			ImageSet imageSet = new ImageSet(version.getReferencedImages());

			imageSet.saveImages(imageService, new FileSystemImageLookup("/data/SnuffyPackages/" + packageCode));

			version.replaceThumbnailNamesWithImageHashes(new FileSystemImageLookup("/data/SnuffyPackages/" + packageCode));

			versionService.insert(version);

			imageSet.saveReferencedImages(referencedImageService, version);
		}
	}
}
