package org.cru.godtools.api.packages;

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

    /**
     * Retrieves the latest revision of a specific package in a specific language.
     *
     * @param languageCode
     * @param packageCode
     * @return
     * @throws LanguageNotFoundException
     * @throws PackageNotFoundException
     * @throws NoTranslationException
     * @throws MissingVersionException
     */
    @Override
    public GodToolsPackage getPackage(String languageCode, String packageCode) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException
    {
        return getPackage(languageCode, packageCode, null);
    }

    /**
     * Retrieves a specific package in a specific language at a specific revision if revision number is passed, or the latest version if null.
     *
     * @param languageCode
     * @param packageCode
     * @param revisionNumber
     * @return
     * @throws LanguageNotFoundException
     * @throws PackageNotFoundException
     * @throws NoTranslationException
     * @throws MissingVersionException
     */
    @Override
    public GodToolsPackage getPackage(String languageCode, String packageCode, Integer revisionNumber) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException
    {
        Language language = languageService.selectLanguageByCodeLocaleSubculture(new LanguageCode(languageCode));
        Package gtPackage = packageService.selectByCode(packageCode);
        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

        Version version = revisionNumber != null ? versionService.selectByTranslationIdVersionNumber(translation.getId(), revisionNumber)
                                                 : versionService.selectLatestVersionForTranslation(translation.getId());

        List<Page> pages = pageService.selectByVersionId(version.getId());
        Set<Image> images = getImages(pages);

        return new GodToolsPackage(version.getPackageStructure(),
                GodToolsPackagePage.createList(pages),
                GodToolsPackageImage.createSet(images),
                languageCode,
                packageCode);
    }

    /**
     * Retrieves the latest version of all packages for a specified language.
     *
     * @param languageCode
     * @return
     * @throws LanguageNotFoundException
     * @throws PackageNotFoundException
     * @throws NoTranslationException
     * @throws MissingVersionException
     */
    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException
    {
        return getPackagesForLanguage(languageCode, null);
    }

    /**
     * Retrieves all packages for specified language at specified revision
     *
     * @param languageCode
     * @param revisionNumber
     * @return
     * @throws LanguageNotFoundException
     * @throws PackageNotFoundException
     * @throws NoTranslationException
     * @throws MissingVersionException
     */
    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode, Integer revisionNumber) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException
    {
        Set<GodToolsPackage> packages = Sets.newHashSet();

        Language language = languageService.selectLanguageByCodeLocaleSubculture(new LanguageCode(languageCode));
        List<Translation> translationsForLanguage = translationService.selectByLanguageId(language.getId());

        for(Translation translation : translationsForLanguage)
        {
            try
            {
                packages.add(getPackage(languageCode, packageService.selectById(translation.getPackageId()).getCode(), revisionNumber));
            }
            //if the desired revision doesn't exist.. that's fine, just continue on to the next translation.
            catch(MissingVersionException missingVersion){ continue; }
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
