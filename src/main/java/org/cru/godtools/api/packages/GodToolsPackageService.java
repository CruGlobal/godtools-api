package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.GodToolsVersion;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */

@Default
public class GodToolsPackageService
{
	private GodToolsTranslationService godToolsTranslationService;
	private ImageService imageService;


	@Inject
	public GodToolsPackageService(GodToolsTranslationService godToolsTranslationService,ImageService imageService)
	{
		this.godToolsTranslationService = godToolsTranslationService;
		this.imageService = imageService;
	}


	/**
     * Retrieves a specific package in a specific language at a specific revision if revision number is passed, or the latest version if null.
     *
     *
     * @param languageCode
     * @param packageCode
     * @return
     */
    public GodToolsPackage getPackage(LanguageCode languageCode,
                                      String packageCode,
                                      GodToolsVersion godToolsVersion,
                                      Integer minimumInterpreterVersion,
									  boolean includeDrafts,
                                      PixelDensity pixelDensity)
    {
		GodToolsTranslation godToolsTranslation = godToolsTranslationService.getTranslation(languageCode, packageCode, godToolsVersion, includeDrafts, minimumInterpreterVersion);

        GodToolsPackage godToolsPackage = new GodToolsPackage(godToolsTranslation);

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
													   boolean includeDrafts,
                                                       PixelDensity pixelDensity)
    {
		Set<GodToolsTranslation> godToolsTranslations = godToolsTranslationService.getTranslationsForLanguage(languageCode, includeDrafts, minimumInterpreterVersion);

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
		return Lists.newArrayList();
	}
}
