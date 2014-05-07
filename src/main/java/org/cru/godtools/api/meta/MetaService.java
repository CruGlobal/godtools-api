package org.cru.godtools.api.meta;

import com.google.common.base.Strings;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.sql2o.Connection;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
	 *
	 *
	 * @param languageCode
	 * @param packageCode
	 * @param minimumInterpreterVersion
	 * @return
	 */
    public MetaResults getMetaResults(String languageCode, String packageCode, Integer minimumInterpreterVersion)
    {
        if(Strings.isNullOrEmpty(languageCode))
        {
            return getMetaResultsForAllLanguages(packageCode, minimumInterpreterVersion);
        }
        else
        {
            MetaResults results = new MetaResults();
			MetaLanguage metaLanguage = getSingleMetaLanguage(languageCode, packageCode, minimumInterpreterVersion);
			results.addLanguage(metaLanguage);

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
	 * @param minimumInterpreterVersion
	 * @return
	 */
    private MetaResults getMetaResultsForAllLanguages(String packageCode, Integer minimumInterpreterVersion)
    {
        MetaResults results = new MetaResults();
        for(Language language : languageService.selectAllLanguages())
        {
            results.addLanguage(getSingleMetaLanguage(language.getPath(), packageCode, minimumInterpreterVersion));
        }

        return results;
    }

	/**
	 * Returns a JAX-B annotated object of type MetaLanguage.  This object returns the information on what resources are available on
	 * the given language with the passed in packageCode and interpreterVersion.
	 *
	 *
	 * @param packageCode
	 * @param minimumInterpreterVersion
	 * @return
	 */
    private MetaLanguage getSingleMetaLanguage(String languageCode, String packageCode, Integer minimumInterpreterVersion)
    {
        if(Strings.isNullOrEmpty(packageCode))
        {
            return getMetaLanguageForMultiplePackages(languageCode, minimumInterpreterVersion);
        }
        else
        {
            return getMetaLanguageForSinglePackage(languageCode, packageCode, minimumInterpreterVersion);
        }
    }

    private MetaLanguage getMetaLanguageForSinglePackage(String languageCode, String packageCode, Integer minimumInterpreterVersion)
    {
		Language language = getLanguage(languageCode);
		Package gtPackage = getPackage(packageCode);

        MetaLanguage metaLanguage = new MetaLanguage(language);
		metaLanguage.setCode(languageCode);

		Translation translation = getTranslation(language.getId(), gtPackage.getId());
		if(translation.isReleased())
		{
			metaLanguage.addPackage(gtPackage.getName(), gtPackage.getCode(), getVersionNumber(translation, gtPackage));
		}
		return metaLanguage;
    }

	private MetaLanguage getMetaLanguageForMultiplePackages(String languageCode, Integer minimumInterpreterVersion)
    {
        List<Translation> translations = translationService.selectByLanguageId(getLanguage(languageCode).getId());

        MetaLanguage metaLanguage = new MetaLanguage();
		metaLanguage.setCode(languageCode);

        for(Translation translation : translations)
        {
			if(!translation.isReleased()) continue;
            Package gtPackage = packageService.selectById(translation.getPackageId());

            metaLanguage.addPackage(gtPackage.getName(), gtPackage.getCode(), getVersionNumber(translation, gtPackage));
        }

        return metaLanguage;
    }

	private BigDecimal getVersionNumber(Translation translation, Package gtPackage)
	{
		return new BigDecimal(getPackageStructure(gtPackage.getId()).getVersionNumber() + "." + translation.getVersionNumber());
	}

	private Language getLanguage(String languageCode)
	{
		return languageService.selectByLanguageCode(new LanguageCode(languageCode));
	}

	private Package getPackage(String packageCode)
	{
		return packageService.selectByCode(packageCode);
	}

	private PackageStructure getPackageStructure(UUID packageId)
	{
		return packageStructureService.selectByPackageId(packageId);
	}

	private Translation getTranslation(UUID languageId, UUID packageId)
	{
		return translationService.selectByLanguageIdPackageId(languageId, packageId);
	}
}
