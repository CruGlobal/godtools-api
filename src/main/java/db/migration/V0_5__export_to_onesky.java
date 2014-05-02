package db.migration;

import com.google.common.collect.Multimap;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.onesky.client.PhraseCollections;

import java.sql.Connection;

/**
 * Created by ryancarlson on 5/1/14.
 */
public class V0_5__export_to_onesky implements JdbcMigration
{

	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	LanguageService languageService = new LanguageService(sqlConnection);
	PackageService packageService = new PackageService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);
	PackageStructureService packageStructureService = new PackageStructureService(sqlConnection);
	PageStructureService pageStructureService = new PageStructureService(sqlConnection);
	TranslationElementService translationElementService = new TranslationElementService(sqlConnection);

	OneSkyDataService oneSkyDataService = new OneSkyDataService(translationElementService, languageService, packageService, translationService);

	@Override
	public void migrate(Connection connection) throws Exception
	{
		for(String packageCode : KnownGodtoolsPackages.packageNames)
		{
			Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(packageCode, new LanguageCode("en"));
			PhraseCollections phraseCollectionsEndpoint = new PhraseCollections();

			for(String pageName : translationElementMultimap.keySet())
			{
				phraseCollectionsEndpoint.importPhraseCollections("25945", pageName, translationElementMultimap.get(pageName));
			}
		}
	}

}
