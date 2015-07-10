package org.cru.godtools.api.meta;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageList;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureList;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationList;
import org.cru.godtools.domain.translations.TranslationService;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.*;


/**
 * Created by ryancarlson on 3/26/14.
 */
public class MetaService
{
	Connection sqlConnection;
	LanguageService languageService;
	TranslationService translationService;
	PackageService packageService;
	PackageStructureService packageStructureService;

	@Inject
	public MetaService(Connection sqlConnection, LanguageService languageService, TranslationService translationService, PackageService packageService, PackageStructureService packageStructureService)
	{
		this.sqlConnection = sqlConnection;
		this.languageService = languageService;
		this.translationService = translationService;
		this.packageService = packageService;
		this.packageStructureService = packageStructureService;
	}

	/**
	 * Returns a JAX-B annotated object of type MetaResults.  This object returns the information on what resources are available given the
	 * passed in languageCode, packageCode and interpreterVersion.
	 *
	 * Note, languageCode and packageCode could optionally either or both be null.
	 *
	 * Structure:
	 * MetaResults
	 * 	-Set<MetaLanguage>
	 *    -Set<MetaPackage>
	 */
	public MetaResults getMetaResults(String languageCode, String packageCode, boolean draftsOnly, boolean allResults)
	{
		if(Strings.isNullOrEmpty(languageCode))
		{
			return getAllMetaResults(packageCode,
					new PackageList(packageService.selectAllPackages()),
					new PackageStructureList(packageStructureService.selectAll()),
					allResults ? createTranslationsMap() : createReleasedTranslationsMap(!draftsOnly),
					draftsOnly,
					allResults);
		}
		else
		{
			MetaResults results = new MetaResults();

			results.addLanguage(buildMetaLanguage(languageService.selectByLanguageCode(new LanguageCode(languageCode)),
					packageCode,
					new PackageList(packageService.selectAllPackages()),
					new PackageStructureList(packageStructureService.selectAll()),
					(allResults || !Strings.isNullOrEmpty(packageCode)) ? createTranslationsMap() : createReleasedTranslationsMap(!draftsOnly),
					draftsOnly,
					allResults));

			return results;
		}
	}

	/**
	 * Returns a JAX-B annotated object of type MetaResults.  This object returns the information on what resources are available given the
	 * passed in languageCode, packageCode and interpreterVersion.
	 *
	 * Assumes languageCode was null, so look up the package information across all languages.
	 *
	 * @param packageCode
	 * @return
	 */
	private MetaResults getAllMetaResults(String packageCode, PackageList packages, PackageStructureList packageStructures, Multimap<UUID, Translation> translationsMap, boolean draftsOnly, boolean allResults)
	{
		MetaResults results = new MetaResults();

		for(Language language : languageService.selectAllLanguages())
		{
			results.addLanguage(buildMetaLanguage(language, packageCode, packages, packageStructures, translationsMap, draftsOnly, allResults));
		}

		return results;
	}

	private MetaLanguage buildMetaLanguage(Language language, String packageCode, PackageList packages, PackageStructureList packageStructures, Multimap<UUID, Translation> translationsMap, boolean draftsOnly, boolean allResults)
	{
		MetaLanguage metaLanguage = new MetaLanguage(language);

		if(Strings.isNullOrEmpty(packageCode))
		{
			TranslationList translations = new TranslationList((List<Translation>) translationsMap.get(language.getId()));

			if(!allResults)
			{
				translations = translations.pareResults();
			}

			for (Translation translation : translations)
			{
				Package gtPackage = packages.getPackageById(translation.getPackageId()).get();

				metaLanguage.addPackage(
						gtPackage.getCode(),
						new GodToolsVersion(
								packageStructures.getByPackageId(gtPackage.getId()).get(),
								translation),
						translation.isReleased());
			}
		}
		else
		{
			Package gtPackage = packages.getPackageByCode(packageCode).get();
			TranslationList translations = new TranslationList((List<Translation>) translationsMap.get(language.getId()))
					.pareResults(!draftsOnly)
					.pareResults(gtPackage.getId());
			translations = draftsOnly ? translations : translations.pareResults();
			Translation translation = !translations.isEmpty() ? translations.get(0) : null;

			if(translation != null)
			{
				metaLanguage.addPackage(
						packageCode,
						new GodToolsVersion(
								packageStructures.getByPackageId(gtPackage.getId()).get(),
								translation),
						translation.isReleased());
			}
		}

		return metaLanguage;
	}

	private Multimap<UUID,Translation> createTranslationsMap()
	{
		List<Translation> translations = translationService.selectAll();
		Multimap<UUID,Translation> translationsMap = ArrayListMultimap.create();

		for(Translation translation : translations)
		{
			translationsMap.put(translation.getLanguageId(),translation);
		}

		return translationsMap;
	}

	private Multimap<UUID,Translation> createReleasedTranslationsMap(boolean draftsOnly)
	{
		List<Translation> translations = translationService.selectAllByReleased(draftsOnly);
		Multimap<UUID,Translation> translationsMap = ArrayListMultimap.create();

		for(Translation translation : translations)
		{
			translationsMap.put(translation.getLanguageId(),translation);
		}

		return translationsMap;
	}
}
