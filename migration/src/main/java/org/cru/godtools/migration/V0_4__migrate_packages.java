package org.cru.godtools.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;


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

		MigrationStatus.verifyPackageMigration(sqlConnection);
	}

	/**
	 * Saves package
	 */
	private void savePackage(Package gtPackage) throws Exception
	{
		PackageDirectory packageDirectory = new PackageDirectory(gtPackage.getCode());
		Package packageCreatedFromDirectory = packageDirectory.buildPackage();

		packageCreatedFromDirectory.setOneskyProjectId(gtPackage.getOneskyProjectId());

		packageService.insert(packageCreatedFromDirectory);
	}

	/**
	 * Saves language the first time it is encountered.  So if language is encountered
	 * on a previous package, it is not saved, nor updated again.
	 *
	 */
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

	/**
	 * Saves initial translation of each package and language combination
	 */
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
