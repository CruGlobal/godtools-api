package db.migration;

import com.google.common.collect.Multimap;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.onesky.client.FileClient;

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
	TranslationElementService translationElementService = new TranslationElementService(sqlConnection);

	OneSkyDataService oneSkyDataService = new OneSkyDataService(translationElementService, languageService, packageService, translationService);

	@Override
	public void migrate(Connection connection) throws Exception
	{
		for(Package gtPackage : KnownGodtoolsPackages.packages)
		{
			Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(gtPackage.getCode(), new LanguageCode("en"));
			FileClient phraseCollectionsEndpoint = new FileClient();

			for(String pageName : translationElementMultimap.keySet())
			{
				phraseCollectionsEndpoint.uploadFile(gtPackage.getOneskyProjectId(), pageName, translationElementMultimap.get(pageName));
			}
		}
	}
}
