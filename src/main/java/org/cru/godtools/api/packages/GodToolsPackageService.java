package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.translations.TranslationService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
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

    @Inject
    public GodToolsPackageService(PackageService packageService,
                                  VersionService versionService,
                                  TranslationService translationService,
                                  LanguageService languageService,
                                  PageService pageService)
    {
        this.packageService = packageService;
        this.versionService = versionService;
        this.translationService = translationService;
        this.languageService = languageService;
        this.pageService = pageService;
    }

    @Override
    public GodToolsPackage getPackage(String languageCode, String packageCode)
    {
//        Language language = languageService.selectLanguageByCode(languageCode);
//        Package gtPackage = packageService.selectByCode(packageCode);
//        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());
//        Version version = versionService.selectLatestVersionForTranslation(translation.getId());
//        List<Page> pages = pageService.selectByVersionId(version.getId());
//
//        GodToolsPackagePage godToolsPackagePage = new GodToolsPackagePage(pages.get(0).getXmlContent(), "");
//
//        GodToolsPackage godToolsPackage = new GodToolsPackage(version.getPackageStructure(), Lists.newArrayList(godToolsPackagePage), languageCode, packageCode);
        /*
        - lookup language
        - lookup package
        - find translation based on language & package
        - find latest version for translation
        - lookup pages
        - lookup images
         */
          return null;
//        return godToolsPackage;
    }

    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode)
    {
        return null;
    }
}
