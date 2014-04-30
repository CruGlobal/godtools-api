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
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.PackageStructureService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.packages.domain.PageStructureService;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.api.packages.domain.TranslationElementService;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.domain.VersionService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.CurrentTranslation;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.migration.PageDirectory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class V0_4__migrate_packages implements JdbcMigration
{

	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	LanguageService languageService = new LanguageService(sqlConnection);
	PackageService packageService = new PackageService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);
	PackageStructureService packageStructureService = new PackageStructureService(sqlConnection);
	PageStructureService pageStructureService = new PageStructureService(sqlConnection);
	TranslationElementService translationElementService = new TranslationElementService(sqlConnection);

	@Override
    public void migrate(Connection connection) throws Exception
    {

        for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
			savePackage(packageCode);
			saveLanguages(packageCode);
			saveTranslations(packageCode);
			savePackageStructures(packageCode);
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

	private void savePackageStructures(String packageCode) throws Exception
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		PackageDirectory packageDirectory = new PackageDirectory(packageCode);

		PackageStructure packageStructure = new PackageStructure();

		packageStructure.setId(UUID.randomUUID());
		packageStructure.setPackageId(gtPackage.getId());
		packageStructure.setXmlContent(packageDirectory.getPackageDescriptorXml(languageService.selectByLanguageCode(new LanguageCode("en"))));
		packageStructure.setVersion_number(1);

		saveBaseTranslatableElements(packageCode, packageStructure.getXmlContent());

		packageStructureService.insert(packageStructure);

		savePageStructures(packageStructure, packageCode);
	}

	private void saveBaseTranslatableElements(String packageCode, Document xmlContent)
	{
		Translation englishTranslation = translationService.selectByLanguageIdPackageId(languageService.selectByLanguageCode(new LanguageCode("en")).getId(),
				packageService.selectByCode(packageCode).getId());

		List<Element> translatableElements = XmlDocumentSearchUtilities.findElementsWithAttribute(xmlContent, "translate");

		for(Element xmlTranslationElement : translatableElements)
		{
			if(Boolean.parseBoolean(xmlTranslationElement.getAttribute("translate")))
			{
				UUID translationElementId = UUID.randomUUID();
				xmlTranslationElement.setAttribute("gtapi-trx-id", translationElementId.toString());

				TranslationElement translationElement = new TranslationElement();
				translationElement.setId(translationElementId);
				translationElement.setTranslationId(englishTranslation.getId());
				translationElement.setBaseText(xmlTranslationElement.getTextContent());
				translationElement.setTranslatedText(xmlTranslationElement.getTextContent());
				translationElement.setElementType(xmlTranslationElement.getNodeName());

				translationElementService.insert(translationElement);
			}
		}
	}

	private void savePageStructures(PackageStructure packageStructure, String packageCode) throws Exception
	{
		PageDirectory pageDirectory = new PageDirectory(packageCode, "en");

		for(Page page : pageDirectory.buildPages())
		{
			PageStructure pageStructure = new PageStructure();

			pageStructure.setId(UUID.randomUUID());
			pageStructure.setXmlContent(page.getXmlContent());
			pageStructure.setPackageStructureId(packageStructure.getId());

			saveBaseTranslatableElements(packageCode, page.getXmlContent());

			pageStructureService.insert(pageStructure);
		}
	}
}
