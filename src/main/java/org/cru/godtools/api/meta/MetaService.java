package org.cru.godtools.api.meta;

import com.google.common.base.Strings;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.translations.TranslationService;
import org.sql2o.Connection;

import javax.inject.Inject;

import java.util.List;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class MetaService
{
    Connection sqlConnection;
    LanguageService languageService;
    TranslationService translationService;
    PackageService packageService;
    VersionService versionService;

    @Inject
    public MetaService(Connection sqlConnection, LanguageService languageService, TranslationService translationService, PackageService packageService, VersionService versionService)
    {
        this.sqlConnection = sqlConnection;
        this.languageService = languageService;
        this.translationService = translationService;
        this.packageService = packageService;
        this.versionService = versionService;
    }

    public MetaResults getMetaResults(String languageCode, String packageCode, Integer minimumInterpreterVersion)
    {
        Language language = languageService.selectByLanguageCode(new LanguageCode(languageCode));

        if(Strings.isNullOrEmpty(packageCode)) return getForMultiplePackages(language, minimumInterpreterVersion);
        else return getForSinglePackage(language, packageService.selectByCode(packageCode), minimumInterpreterVersion);
    }

    private MetaResults getForSinglePackage(Language language, Package gtPackage, Integer minimumInterpreterVersion)
    {
        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

        MetaResults results = new MetaResults(language);

        Version version = versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion);

        return results.withPackage(gtPackage.getName(), gtPackage.getCode(), version.getVersionNumber());
    }

    private MetaResults getForMultiplePackages(Language language, Integer minimumInterpreterVersion)
    {
        List<Translation> translations = translationService.selectByLanguageId(language.getId());

        MetaResults results = new MetaResults(language);
        for(Translation translation : translations)
        {
            Version version = versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion);
            Package gtPackage = packageService.selectById(version.getPackageId());

            results.withPackage(gtPackage.getName(), gtPackage.getCode(), version.getVersionNumber());
        }

        return results;
    }
}
