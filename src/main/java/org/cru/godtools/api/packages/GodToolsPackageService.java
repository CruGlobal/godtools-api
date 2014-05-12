package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.images.domain.ReferencedImage;
import org.cru.godtools.api.images.domain.ReferencedImageService;
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
	private ReferencedImageService referencedImageService;

	@Inject
	public GodToolsPackageService(GodToolsTranslationService godToolsTranslationService,ImageService imageService, ReferencedImageService referencedImageService)
	{
		this.godToolsTranslationService = godToolsTranslationService;
		this.imageService = imageService;
		this.referencedImageService = referencedImageService;
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
        GodToolsPackage godToolsPackage = new GodToolsPackage(godToolsTranslationService.getTranslation(languageCode,
				packageCode,
				godToolsVersion,
				includeDrafts,
				minimumInterpreterVersion));

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
		List<ReferencedImage> referencedImages = referencedImageService.selectByPackageStructureId(godToolsPackage.getPackageStructure().getId());

		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImages)
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return imageList;
	}
}
