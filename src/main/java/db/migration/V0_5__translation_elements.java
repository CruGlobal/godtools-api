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
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.api.packages.domain.TranslationElementService;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.domain.VersionService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.migration.PageDirectory;
import org.cru.godtools.migration.TranslatableElements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_5__translation_elements implements JdbcMigration
{
	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	LanguageService languageService = new LanguageService(sqlConnection);
	PackageService packageService = new PackageService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);
	TranslationElementService translationElementService = new TranslationElementService(sqlConnection);


    @Override
    public void migrate(Connection connection) throws Exception
    {
        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
			PackageDirectory packageDirectory = new PackageDirectory(packageCode);
            Package gtPackage = packageService.selectByCode(packageCode);
            List<Translation> translationsForPackage = translationService.selectByPackageId(gtPackage.getId());

            for(Translation translation : translationsForPackage)
            {
				Language englishLanguage = languageService.selectByLanguageCode(new LanguageCode("en"));
                Language languageForTranslation = languageService.selectLanguageById(translation.getLanguageId());

				if(languageForTranslation.getPath().equalsIgnoreCase(englishLanguage.getPath())) continue;

				savePackageLevelElements(packageCode, packageDirectory, englishLanguage, languageForTranslation);
				savePageLevelElements(packageCode, englishLanguage, languageForTranslation);
			}
        }
    }

	private void savePackageLevelElements(String packageCode, PackageDirectory packageDirectory, Language englishLanguage, Language languageForTranslation) throws IOException, SAXException, ParserConfigurationException
	{
		Document xmlPackageStructureForEnglish = new PackageDirectory(packageCode).getPackageDescriptorXml(englishLanguage);

		Document xmlPackageStructureForThisTranslation = packageDirectory.getPackageDescriptorXml(languageForTranslation);

		TranslatableElements translatableElements = new TranslatableElements(packageCode, languageForTranslation.getPath(), xmlPackageStructureForThisTranslation, xmlPackageStructureForEnglish);

		translatableElements.save(translationService,
				languageService,
				packageService,
				translationElementService);
	}

	private void savePageLevelElements(String packageCode, Language englishLanguage, Language languageForTranslation)
	{
		PageDirectory englishPageDirectory = new PageDirectory(packageCode, englishLanguage.getPath());
		PageDirectory translationPageDirectory = new PageDirectory(packageCode, languageForTranslation.getPath());

		List<Page> englishPages = englishPageDirectory.buildPages();
		List<Page> translationPages = translationPageDirectory.buildPages();

		for(int i = 0; i < englishPages.size() && i < translationPages.size(); i++)
		{
			TranslatableElements translatableElements = new TranslatableElements(packageCode,
					languageForTranslation.getPath(),
					translationPages.get(i).getXmlContent(),
					englishPages.get(i).getXmlContent());

			translatableElements.save(translationService,
					languageService,
					packageService,
					translationElementService);
		}

	}
}