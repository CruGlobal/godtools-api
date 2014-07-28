package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PixelDensity;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Service which uses lower-level domain services to assemble XML structure files and associated images for
 * a translation of a GodTools package and return the results bundled as a GodToolsPackage.
 *
 * There are services for a specific language & package combo, as well as a service which would load all packages
 * for a language and return them as a Set.
 *
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
                                      PixelDensity pixelDensity)
	{
		GodToolsTranslation godToolsTranslation = godToolsTranslationService.getTranslation(languageCode,
				packageCode,
				godToolsVersion);

		return GodToolsPackage.assembleFromComponents(godToolsTranslation,
				loadImages(godToolsTranslation.getPackageStructure()),
				loadIcon(godToolsTranslation.getPackageCode()));
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
		Set<GodToolsTranslation> godToolsTranslations = godToolsTranslationService.getTranslationsForLanguage(languageCode, GodToolsVersion.LATEST_PUBLISHED_VERSION);

        Set<GodToolsPackage> godToolsPackages = Sets.newHashSet();

        for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
        {
			godToolsPackages.add(GodToolsPackage.assembleFromComponents(godToolsTranslation,
					loadImages(godToolsTranslation.getPackageStructure()),
					loadIcon(godToolsTranslation.getPackageCode())));
		}

		return godToolsPackages;
    }

	private List<Image> loadImages(PackageStructure packageStructure)
	{
		List<ReferencedImage> referencedImages = referencedImageService.selectByPackageStructureId(packageStructure.getId());

		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImages)
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return imageList;
	}

	private Image loadIcon(String packageCode)
	{
		return imageService.selectByFilename(Image.buildFilename(packageCode, "icon@2x.png"));
	}
}
