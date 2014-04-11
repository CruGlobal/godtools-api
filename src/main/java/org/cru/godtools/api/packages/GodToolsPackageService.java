package org.cru.godtools.api.packages;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */

@Default
public class GodToolsPackageService extends GodToolsTranslationService
{
    @Inject
    public GodToolsPackageService(PackageService packageService,
								  VersionService versionService,
								  TranslationService translationService,
								  LanguageService languageService,
								  PageService pageService,
								  ImageService imageService)
    {
		super(packageService,versionService,translationService,languageService,pageService, imageService);
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
    public GodToolsPackage getPackage(LanguageCode languageCode,
                                      String packageCode,
                                      Integer revisionNumber,
                                      Integer minimumInterpreterVersion,
                                      PixelDensity pixelDensity)
    {
		GodToolsTranslation godToolsTranslation = getTranslation(languageCode, packageCode, revisionNumber, minimumInterpreterVersion);

        return new GodToolsPackage(godToolsTranslation.getPackageXml(),
				godToolsTranslation.getPageFiles(),
                languageCode.toString(),
                packageCode,
				getImages(packageCode));
    }

    private List<Image> getImages(String packageCode)
    {
		return null;
//        return imageService.selectByPackageId(packageService.selectByCode(packageCode).getId());
    }


    /**
     * Retrieves all packages for specified language at specified revision
     *
     *
     * @param languageCode
     * @return

     */
    public Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode,
                                                       Integer minimumInterpreterVersion,
                                                       PixelDensity pixelDensity)
    {
		Set<GodToolsTranslation> godToolsTranslations = getTranslationsForLanguage(languageCode, minimumInterpreterVersion);

        Set<GodToolsPackage> godToolsPackages = Sets.newHashSet();

        for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
        {
			godToolsPackages.add(new GodToolsPackage(godToolsTranslation, getImages(godToolsTranslation.getPackageCode())));
		}

        return godToolsPackages;
    }
}
