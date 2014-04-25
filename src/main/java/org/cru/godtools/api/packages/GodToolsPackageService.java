package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.domain.TranslationService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */

@Default
public class GodToolsPackageService extends GodToolsTranslationService
{
	private ImageService imageService;


	@Inject
    public GodToolsPackageService(PackageService packageService,
								  VersionService versionService,
								  TranslationService translationService,
								  LanguageService languageService,
								  PageService pageService,
								  ImageService imageService)
    {
		super(packageService,versionService,translationService,languageService,pageService);
		this.imageService = imageService;
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

        GodToolsPackage godToolsPackage = new GodToolsPackage(godToolsTranslation.getPackageXml(),
				godToolsTranslation.getPageFiles(),
				godToolsTranslation.getCurrentVersion(),
                languageCode.toString(),
                packageCode);

		godToolsPackage.setImages(loadImages(godToolsPackage));

		return godToolsPackage;
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
			GodToolsPackage godToolsPackage = new GodToolsPackage(godToolsTranslation);
			godToolsPackage.setImages(loadImages(godToolsPackage));
			godToolsPackages.add(godToolsPackage);
		}

		return godToolsPackages;
    }

	private List<Image> loadImages(GodToolsPackage godToolsPackage)
	{
		return imageService.selectyByVersionId(godToolsPackage.getCurrentVersion().getId());
	}
}
