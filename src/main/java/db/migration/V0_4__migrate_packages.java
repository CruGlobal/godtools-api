package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
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
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;
import org.cru.godtools.migration.PageDirectory;
import org.cru.godtools.migration.TranslatableElements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

		TranslatableElements translatableElements = new TranslatableElements(packageCode, "en", packageStructure.getXmlContent(), packageStructure.getXmlContent());
		translatableElements.save(translationService,
				languageService,
				packageService,
				translationElementService);

		packageStructureService.insert(packageStructure);

		savePageStructures(packageStructure, packageCode);
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

			TranslatableElements translatableElements = new TranslatableElements(packageCode, "en", pageStructure.getXmlContent(), pageStructure.getXmlContent());
			translatableElements.save(translationService,
					languageService,
					packageService,
					translationElementService);

			pageStructureService.insert(pageStructure);
		}
	}
}
