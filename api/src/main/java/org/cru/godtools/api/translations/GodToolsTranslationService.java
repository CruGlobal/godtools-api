package org.cru.godtools.api.translations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.api.utilities.ResourceNotFoundException;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.onesky.io.TranslationDownload;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service which uses lower-level domain services to assemble XML structure files for a translation of a GodTools translation
 * and return the results bundled as a GodToolsTranslation.
 *
 * There are services for a specific language & package combo, as well as a service which would load all packages
 * for a language and return them as a Set.
 *
 * Created by ryancarlson on 4/8/14.
 */

public class GodToolsTranslationService
{
	protected PackageService packageService;
	protected TranslationService translationService;
	protected LanguageService languageService;
	protected PackageStructureService packageStructureService;
	protected PageStructureService pageStructureService;
	protected TranslationElementService translationElementService;
	protected ReferencedImageService referencedImageService;
	protected ImageService imageService;

	private TranslationDownload translationDownload;

	@Inject
	public GodToolsTranslationService(PackageService packageService, TranslationService translationService, LanguageService languageService, PackageStructureService packageStructureService, PageStructureService pageStructureService, TranslationElementService translationElementService, ReferencedImageService referencedImageService, ImageService imageService, TranslationDownload translationDownload)
	{
		this.packageService = packageService;
		this.translationService = translationService;
		this.languageService = languageService;
		this.packageStructureService = packageStructureService;
		this.pageStructureService = pageStructureService;
		this.translationElementService = translationElementService;
		this.referencedImageService = referencedImageService;
		this.imageService = imageService;
		this.translationDownload = translationDownload;
	}

	/**
	 * Retrieves a specific package in a specific language at a specific version.
	 *
	 *
	 * @param languageCode
	 * @param packageCode
	 * @param godToolsVersion
	 * @param includeDrafts
	 * @return
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
											  String packageCode,
											  GodToolsVersion godToolsVersion,
											  boolean includeDrafts,
											  Integer minimumInterpreterVersion)
	{
		Translation translation = getTranslation(packageCode, languageCode, godToolsVersion, includeDrafts);
		Package gtPackage = getPackage(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByPackageStructureId(packageStructure.getId());

		//draft translations are always updated
		if(!translation.isReleased()) updateTranslationFromTranslationTool(translation, pageStructures);

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());

		return GodToolsTranslation.assembleFromComponents(packageCode, packageStructure, pageStructures, translationElementList, getImagesUsedInThisPackage(packageStructure.getId()));
	}

	/**
	 * Retrieves the latest version of all packages for specified language.
	 *
	 *
	 * @param languageCode
	 * @param includeDrafts
	 * @return

	 */
	public Set<GodToolsTranslation> getTranslationsForLanguage(LanguageCode languageCode, boolean includeDrafts, Integer minimumInterpreterVersion)
	{
		Set<GodToolsTranslation> translations = Sets.newHashSet();

		Language language = languageService.selectByLanguageCode(languageCode);
		List<Translation> translationsForLanguage = translationService.selectByLanguageId(language.getId());

		for(Translation translation : translationsForLanguage)
		{
			try
			{
				Package gtPackage = packageService.selectById(translation.getPackageId());
				translations.add(getTranslation(languageCode, gtPackage.getCode(), GodToolsVersion.LATEST_VERSION, includeDrafts, minimumInterpreterVersion));
			}
			//if the desired revision doesn't exist.. that's fine, just continue on to the next translation.
			catch(NotFoundException e){ continue; }
		}

		return translations;
	}

	public Translation setupNewTranslation(LanguageCode languageCode, String packageCode)
	{
		Package gtPackage = getPackage(packageCode);
		Language language = languageService.getOrCreateLanguage(languageCode);
		Translation latestVersionExistingTranslation = getTranslation(packageCode, languageCode, GodToolsVersion.LATEST_VERSION, true);

		int nextVersionNumber;

		if(latestVersionExistingTranslation == null)
		{
			nextVersionNumber = 1;
		}
		else if(latestVersionExistingTranslation.isReleased())
		{
			nextVersionNumber = latestVersionExistingTranslation.getVersionNumber() + 1;
		}
		else
		{
			throw new WebApplicationException("A draft version of this translation already exists.  See version " + latestVersionExistingTranslation.getVersionNumber());
		}

		Translation newTranslation = new Translation();
		newTranslation.setId(UUID.randomUUID());
		newTranslation.setLanguageId(language.getId());
		newTranslation.setPackageId(gtPackage.getId());
		newTranslation.setVersionNumber(nextVersionNumber);
		newTranslation.setReleased(false);

		translationService.insert(newTranslation);

		translationElementService.createTranslatableElements(translationService, newTranslation, gtPackage);

		return newTranslation;
	}

	public void updateTranslationsFromTranslationTool(LanguageCode languageCode, String packageCode)
	{
		Translation translation = getTranslation(packageCode, languageCode, GodToolsVersion.LATEST_VERSION, true);
		Package gtPackage = getPackage(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByPackageStructureId(packageStructure.getId());

		updateTranslationFromTranslationTool(translation, pageStructures);
	}

	private void updateTranslationFromTranslationTool(Translation translation, List<PageStructure> pageStructures)
	{
		for(PageStructure pageStructure : pageStructures)
		{
			translationDownload.doDownload(translation.getId(), pageStructure.getId());
		}
	}

	private Translation getTranslation(String packageCode, LanguageCode languageCode, GodToolsVersion godToolsVersion, boolean includeDrafts)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		Translation translation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), gtPackage.getId(), godToolsVersion);

		if(!(includeDrafts || translation.isReleased())) throw new ResourceNotFoundException(Translation.class);

		return translation;
	}

	private Package getPackage(String packageCode)
	{
		return packageService.selectByCode(packageCode);
	}

	private List<Image> getImagesUsedInThisPackage(UUID packageStructureId)
	{
		List<ReferencedImage> referencedImages = referencedImageService.selectByPackageStructureId(packageStructureId);

		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImages)
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return imageList;
	}
}
