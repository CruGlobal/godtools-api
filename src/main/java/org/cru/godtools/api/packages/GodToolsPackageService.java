package org.cru.godtools.api.packages;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.translations.TranslationService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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
     * Retrieves a specific package in a specific language at a specific revision if revision number is passed, or the latest version if null.
     *
     *
     * @param languageCode
     * @param packageCode
     * @param revisionNumber
     * @return
     */
    @Override
    public GodToolsPackage getPackage(LanguageCode languageCode,
                                      String packageCode,
                                      Integer revisionNumber,
                                      Integer minimumInterpreterVersion,
                                      PixelDensity pixelDensity)
    {
        Language language = languageService.selectByLanguageCode(languageCode);
        Package gtPackage = packageService.selectByCode(packageCode);
        Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

        Version version = getVersion(revisionNumber, minimumInterpreterVersion, translation);
        List<Page> pages = pageService.selectByVersionId(version.getId());

        return new GodToolsPackage(version.getPackageStructure(),
                pages,
                getImages(pixelDensity, pages),
                languageCode.toString(),
                packageCode);
    }

    private Set<Image> getImages(PixelDensity pixelDensity, List<Page> pages)
    {
        return imagePageRelationshipService.selectImagesForAllPages(pages, pixelDensity);
    }

    private Version getVersion(Integer revisionNumber, Integer minimumInterpreterVersion, Translation translation)
    {
        if(minimumInterpreterVersion == null)
        {
            return revisionNumber != null ? versionService.selectSpecificVersionForTranslation(translation.getId(), revisionNumber)
                                                     : versionService.selectLatestVersionForTranslation(translation.getId());
        }
        else
        {
            return revisionNumber != null ? versionService.selectSpecificVersionForTranslation(translation.getId(), revisionNumber, minimumInterpreterVersion)
                    : versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion);
        }
    }

    /**
     * Retrieves all packages for specified language at specified revision
     *
     *
     * @param languageCode
     * @param revisionNumber
     * @return

     */
    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode,
                                                       Integer revisionNumber,
                                                       Integer minimumInterpreterVersion,
                                                       PixelDensity pixelDensity)
    {
        Set<GodToolsPackage> packages = Sets.newHashSet();

        Language language = languageService.selectByLanguageCode(languageCode);
        List<Translation> translationsForLanguage = translationService.selectByLanguageId(language.getId());

        for(Translation translation : translationsForLanguage)
        {
            try
            {
                Package gtPackage = packageService.selectById(translation.getPackageId());
                packages.add(getPackage(languageCode, gtPackage.getCode(), revisionNumber, minimumInterpreterVersion, pixelDensity));
            }
            //if the desired revision doesn't exist.. that's fine, just continue on to the next translation.
            catch(NotFoundException e){ continue; }
        }

        return packages;
    }
}
