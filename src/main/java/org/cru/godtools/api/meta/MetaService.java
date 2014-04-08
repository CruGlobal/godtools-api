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
        if(Strings.isNullOrEmpty(languageCode))
        {
            return getForMultipleLanguages(languageService.selectAllLanguages(),packageCode,minimumInterpreterVersion);
        }
        else
        {
            MetaResults results = new MetaResults();
            LanguageCode languangeCode = new LanguageCode(languageCode);
            return results.withLanguage(getForSingleLanguage(languageService.selectByLanguageCode(languangeCode),
                    packageCode,
                    minimumInterpreterVersion).setCode(languangeCode.toString()));
        }
    }

    private MetaResults getForMultipleLanguages(List<Language> languages, String packageCode, Integer minimumInterpreterVersion)
    {
        MetaResults results = new MetaResults();
        for(Language language : languages)
        {
            results.withLanguage(getForSingleLanguage(language, packageCode, minimumInterpreterVersion)
                    .setCode(LanguageCode.fromLanguage(language).toString()));
        }

        return results;
    }

    private MetaLanguage getForSingleLanguage(Language language, String packageCode, Integer minimumInterpreterVersion)
    {
        if(Strings.isNullOrEmpty(packageCode))
        {
            return getForMultiplePackages(language, minimumInterpreterVersion);
        }
        else
        {
            return getForSinglePackage(language, packageService.selectByCode(packageCode), minimumInterpreterVersion)
                    .setCode(LanguageCode.fromLanguage(language).toString());
        }
    }

    private MetaLanguage getForSinglePackage(Language language, Package gtPackage, Integer minimumInterpreterVersion)
    {
        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

        MetaLanguage metaLanguage = new MetaLanguage(language);

        Version version = versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion);

        return metaLanguage.withPackage(gtPackage.getName(), gtPackage.getCode(), version.getVersionNumber());
    }

    private MetaLanguage getForMultiplePackages(Language language, Integer minimumInterpreterVersion)
    {
        List<Translation> translations = translationService.selectByLanguageId(language.getId());

        MetaLanguage metaLanguage = new MetaLanguage();

        for(Translation translation : translations)
        {
            Version version = versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion);
            Package gtPackage = packageService.selectById(version.getPackageId());

            metaLanguage.withPackage(gtPackage.getName(), gtPackage.getCode(), version.getVersionNumber());
        }

        return metaLanguage;
    }
}
