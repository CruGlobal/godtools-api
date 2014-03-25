package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.translations.TranslationService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */

@Default
public class GodToolsPackageService implements IGodToolsPackageService
{

    PackageService packageService;
    VersionService versionService;
    TranslationService translationService;
    LanguageService languageService;
    PageService pageService;
    ImageService imageService;

    @Inject
    public GodToolsPackageService(PackageService packageService,
                                  VersionService versionService,
                                  TranslationService translationService,
                                  LanguageService languageService,
                                  PageService pageService,
                                  ImageService imageService)
    {
        this.packageService = packageService;
        this.versionService = versionService;
        this.translationService = translationService;
        this.languageService = languageService;
        this.pageService = pageService;
        this.imageService = imageService;
    }

    @Override
    public GodToolsPackage getPackage(String languageCode, String packageCode)
    {
        Language language = languageService.selectLanguageByCodeLocaleSubculture(new LanguageCode(languageCode));
        Package gtPackage = packageService.selectByCode(packageCode);

        if(language == null || gtPackage == null)
        {
            throw new InvalidParameterException("No package found for language/package combination: " + languageCode + "," + packageCode);
        }

        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

        if(translation == null)
        {
            throw new InvalidParameterException("No translation found for language/package combination: " + languageCode + "," + packageCode);
        }

        Version version = versionService.selectLatestVersionForTranslation(translation.getId());

        if(version == null)
        {
            throw new IllegalStateException("No version found for translation: " + translation.getId());
        }

        List<Page> pages = pageService.selectByVersionId(version.getId());

//        List<Image> images = imageService.
        GodToolsPackage godToolsPackage = new GodToolsPackage(version.getPackageStructure(), GodToolsPackagePage.createList(pages), languageCode, packageCode);

        return godToolsPackage;
    }

    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode)
    {
        return null;
    }
}
