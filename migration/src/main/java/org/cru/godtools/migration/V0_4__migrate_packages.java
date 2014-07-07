package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.domain.PackageStructureService;
import org.cru.godtools.api.packages.domain.PageStructureService;
import org.cru.godtools.api.packages.domain.TranslationElementService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.PackageDirectory;


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
	PackageStructureService packageStructureService = new PackageStructureService(sqlConnection);
	PageStructureService pageStructureService = new PageStructureService(sqlConnection);
	TranslationElementService translationElementService = new TranslationElementService(sqlConnection);

	@Override
	public void migrate(Connection connection) throws Exception
	{

		for(Package gtPackage : KnownGodtoolsPackages.packages)
		{
			PackageDirectory packageDirectory = new PackageDirectory(gtPackage.getCode(),
					packageService,
					languageService,
					translationService,
					translationElementService,
					packageStructureService,
					pageStructureService);

			savePackage(gtPackage);
			saveLanguages(gtPackage.getCode());
			saveTranslations(gtPackage.getCode());
			packageDirectory.savePackageStructures();
			packageDirectory.savePageStructures();
		}
	}

	private void savePackage(Package gtPackage) throws Exception
	{
		PackageDirectory packageDirectory = new PackageDirectory(gtPackage.getCode());
		Package packageCreatedFromDirectory = packageDirectory.buildPackage();

		packageCreatedFromDirectory.setOneskyProjectId(gtPackage.getOneskyProjectId());

		packageService.insert(packageCreatedFromDirectory);
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
			Translation translation = new Translation(packageService.selectByCode(packageCode), retrievedLanguage);
			translation.setVersionNumber(1);
			translation.setReleased(true);

			translationService.insert(translation);
		}
	}
}