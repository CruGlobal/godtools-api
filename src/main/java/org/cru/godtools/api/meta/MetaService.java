package org.cru.godtools.api.meta;

import com.google.common.base.Strings;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
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
     */
    public MetaResults getMetaResults(String languageCode, String packageCode, boolean draftsOnly, boolean allResults)
    {
        if(Strings.isNullOrEmpty(languageCode))
        {
            return getMetaResultsForAllLanguages(packageCode, draftsOnly, allResults);
        }
        else
        {
            MetaResults results = new MetaResults();
            MetaLanguage metaLanguage = getSingleMetaLanguage(languageService.selectByLanguageCode(new LanguageCode(languageCode)),
                    packageCode,
                    draftsOnly,
                    allResults);

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
     * @return
     */
    private MetaResults getMetaResultsForAllLanguages(String packageCode, boolean draftsOnly, boolean allResults)
    {
        MetaResults results = new MetaResults();
        for(Language language : languageService.selectAllLanguages())
        {
            results.addLanguage(getSingleMetaLanguage(language, packageCode, draftsOnly, allResults));
        }

        return results;
    }

    /**
     * Returns a JAX-B annotated object of type MetaLanguage.  This object returns the information on what resources are available on
     * the given language with the passed in packageCode and interpreterVersion.
     *
     *
     * @param packageCode
     * @param draftsOnly
     * @return
     */
    private MetaLanguage getSingleMetaLanguage(Language language, String packageCode, boolean draftsOnly, boolean allResults)
    {
        if(Strings.isNullOrEmpty(packageCode))
        {
            return getMetaLanguageForMultiplePackages(language, draftsOnly, allResults);
        }
        else
        {
            return getMetaLanguageForSinglePackage(language, packageCode, draftsOnly);
        }
    }

    private MetaLanguage getMetaLanguageForSinglePackage(Language language, String packageCode, boolean draftsOnly)
    {
        Package gtPackage = getPackage(packageCode);

        MetaLanguage metaLanguage = new MetaLanguage(language);
        metaLanguage.setCode(LanguageCode.fromLanguage(language).toString());
        metaLanguage.setName(language.getName());

        Translation translation = getTranslation(language.getId(), gtPackage.getId());

        if((draftsOnly && translation.isDraft()) || (!draftsOnly && translation.isReleased()))
        {
            metaLanguage.addPackage(gtPackage.getCode(), getVersionNumber(translation, gtPackage), translation.isReleased());
        }

        return metaLanguage;
    }

    private MetaLanguage getMetaLanguageForMultiplePackages(Language language, boolean draftsOnly, boolean allResults)
    {
        List<Translation> translations = translationService.selectByLanguageId(language.getId());

        MetaLanguage metaLanguage = new MetaLanguage();
        metaLanguage.setCode(LanguageCode.fromLanguage(language).toString());
        metaLanguage.setName(language.getName());

        for(Translation translation : translations)
        {
            if(allResults || (draftsOnly && translation.isDraft()) || (!draftsOnly && translation.isReleased()))
            {
                Package gtPackage = packageService.selectById(translation.getPackageId());

                metaLanguage.addPackage(gtPackage.getCode(), getVersionNumber(translation, gtPackage), translation.isReleased());
            }
        }

        return metaLanguage;
    }

    private String getVersionNumber(Translation translation, Package gtPackage)
    {
        return getPackageStructure(gtPackage.getId()).getVersionNumber() + "." + translation.getVersionNumber();
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
        List<Translation> translationList = translationService.selectByLanguageIdPackageId(languageId, packageId);

        return translationList.get(0);
    }
}
