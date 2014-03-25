package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.exceptions.LanguageNotFoundException;
import org.cru.godtools.api.packages.exceptions.MissingVersionException;
import org.cru.godtools.api.packages.exceptions.NoTranslationException;
import org.cru.godtools.api.packages.exceptions.PackageNotFoundException;
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
    ImagePageRelationshipService imagePageRelationshipService;

    @Inject
    public GodToolsPackageService(PackageService packageService,
                                  VersionService versionService,
                                  TranslationService translationService,
                                  LanguageService languageService,
                                  PageService pageService,
                                  ImagePageRelationshipService imagePageRelationshipService)
    {
        this.packageService = packageService;
        this.versionService = versionService;
        this.translationService = translationService;
        this.languageService = languageService;
        this.pageService = pageService;
        this.imagePageRelationshipService = imagePageRelationshipService;
    }

    @Override
    public GodToolsPackage getPackage(String languageCode, String packageCode) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException
    {
        Language language = languageService.selectLanguageByCodeLocaleSubculture(new LanguageCode(languageCode));
        Package gtPackage = packageService.selectByCode(packageCode);
        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());
        Version version = versionService.selectLatestVersionForTranslation(translation.getId());


        List<Page> pages = pageService.selectByVersionId(version.getId());
        Set<Image> images = getImages(pages);

        return new GodToolsPackage(version.getPackageStructure(),
                GodToolsPackagePage.createList(pages),
                GodToolsPackageImage.createSet(images),
                languageCode,
                packageCode);

    }

    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException
    {
        Set<GodToolsPackage> packages = Sets.newHashSet();

        Language language = languageService.selectLanguageByCodeLocaleSubculture(new LanguageCode(languageCode));
        List<Translation> translationsForLanguage = translationService.selectByLanguageId(language.getId());

        for(Translation translation : translationsForLanguage)
        {
            packages.add(getPackage(languageCode, packageService.selectById(translation.getPackageId()).getCode()));
        }

        return packages;
    }

    private Set<Image> getImages(List<Page> pages)
    {
        Set<Image> images = Sets.newHashSet();

        for(Page page : pages)
        {
            images.addAll(imagePageRelationshipService.selectImagesByPageId(page.getId()));
        }

        return images;
    }

}
