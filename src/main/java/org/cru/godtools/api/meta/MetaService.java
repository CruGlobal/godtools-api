package org.cru.godtools.api.meta;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import org.cru.godtools.domain.GodToolsVersion;
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

import java.util.List;
import java.util.Map;
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
            return getAllMetaResults(packageCode, packageService.selectAllPackages(), draftsOnly, allResults);
        }
        else
        {
            MetaResults results = new MetaResults();

            results.addLanguage(buildMetaLanguage(languageService.selectByLanguageCode(new LanguageCode(languageCode)),
                    packageCode,
                    packageService.selectAllPackages(),
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
    private MetaResults getAllMetaResults(String packageCode, List<Package> packages, boolean draftsOnly, boolean allResults)
    {
        MetaResults results = new MetaResults();
        for(Language language : languageService.selectAllLanguages())
        {
            results.addLanguage(buildMetaLanguage(language, packageCode, packages, draftsOnly, allResults));
        }

        return results;
    }

    private MetaLanguage buildMetaLanguage(Language language, String packageCode, List<Package> packages, boolean draftsOnly, boolean allResults)
    {
        MetaLanguage metaLanguage = new MetaLanguage(language);

        if(Strings.isNullOrEmpty(packageCode))
        {
            List<Translation> translations = allResults ?
                    translationService.selectByLanguageId(language.getId()) : translationService.selectByLanguageIdReleased(language.getId(), !draftsOnly);

            for (Translation translation : translations)
            {
                Package gtPackage = getPackageById(translation.getPackageId(), packages).get();

                metaLanguage.addPackage(gtPackage.getCode(), getVersionNumber(translation, gtPackage), translation.isReleased());
            }
        }
        else
        {
            Package gtPackage = getPackageByCode(packageCode, packages).get();
            Translation translation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
                    gtPackage.getId(),
                    draftsOnly ? GodToolsVersion.DRAFT_VERSION : GodToolsVersion.LATEST_PUBLISHED_VERSION);

            if(translation != null)
            {
                metaLanguage.addPackage(packageCode, getVersionNumber(translation, gtPackage), translation.isReleased());
            }
        }

        return metaLanguage;
    }

    private String getVersionNumber(Translation translation, Package gtPackage)
    {
        return getPackageStructure(gtPackage.getId()).getVersionNumber() + "." + translation.getVersionNumber();
    }

    private Optional<Package> getPackageByCode(final String packageCode, List<Package> packages)
    {
        return FluentIterable.from(packages).firstMatch(new Predicate<Package>()
        {
            public boolean apply(Package input)
            {
                return packageCode.equals(input.getCode());
            }
        });
    }

    private Optional<Package> getPackageById(final UUID packageId, List<Package> packages)
    {
        return FluentIterable.from(packages).firstMatch(new Predicate<Package>()
        {
            public boolean apply(Package input)
            {
                return packageId.equals(input.getId());
            }
        });
    }

    private PackageStructure getPackageStructure(UUID packageId)
    {
        return packageStructureService.selectByPackageId(packageId);
    }
}
