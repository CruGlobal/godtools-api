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
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.PixelDensity;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

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
	@Inject
	protected PackageService packageService;
	@Inject
	protected TranslationService translationService;
	@Inject
	protected LanguageService languageService;
	@Inject
	protected PackageStructureService packageStructureService;
	@Inject
	protected PageStructureService pageStructureService;
	@Inject
	protected TranslationElementService translationElementService;
	@Inject
	protected ReferencedImageService referencedImageService;
	@Inject
	protected ImageService imageService;
	@Inject
	private NewTranslationCreation newTranslationProcess;
	@Inject
	private DraftTranslationUpdate draftTranslationUpdateProcess;

	/**
	 * Retrieves a specific package in a specific language at a specific version.
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
											  String packageCode,
											  GodToolsVersion godToolsVersion, PixelDensity pixelDensity)
	{
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, godToolsVersion);
		if(translation == null) throw new NotFoundException();

		Package gtPackage = packageService.selectByCode(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByTranslationId(translation.getId());

		if(translation.isDraft())
		{
			draftTranslationUpdateProcess.updateFromTranslationTool(gtPackage.getTranslationProjectId(),
					translation,
					pageStructures,
					languageCode);
		}

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());

		return GodToolsTranslation.assembleFromComponents(packageCode,
				translation.getTranslatedName(),
				translation.getVersionNumber(),
				packageStructure,
				pageStructures,
				translationElementList,
				getImagesUsedInThisTranslation(packageStructure, pixelDensity),
				!translation.isReleased(),
				loadIcon(packageCode));
	}

	/**
	 * Retrieves the latest published version of all packages for specified language.
	 *
	 * @return
	 */
	public Set<GodToolsTranslation> getTranslationsForLanguage(LanguageCode languageCode, GodToolsVersion godToolsVersion, PixelDensity pixelDensity)
	{
		Set<GodToolsTranslation> translations = Sets.newHashSet();

		for(Package gtPackage : packageService.selectAllPackages())
		{
			try
			{
				translations.add(getTranslation(languageCode, gtPackage.getCode(), godToolsVersion, pixelDensity));
			}
			catch(NotFoundException notFound) { /*oh well..*/ }
		}

		return translations;
	}

	/**
	 * Creates a new translations for the languageCode and packageCode combination that's specified.
	 *
	 * If the languageCode doesn't reference a valid language, a new language is created.
	 *
	 * The newly created translation is version 1 for a brand new translation, or if it's a new version of an existing
	 * translation, then it takes the next highest version number.
	 */
	public Translation setupNewTranslation(LanguageCode languageCode, String packageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(languageCode);

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = getTranslationFromDatabase(new LanguageCode(language.getCode()), gtPackage.getCode(), GodToolsVersion.LATEST_VERSION);

		// save a new translation for this package language combo
		Translation newTranslation = newTranslationProcess.saveNewTranslation(gtPackage, language, currentTranslation);

		// if we found a current translation, then copy page structures and translation elements from the current translation
		// to the new
		if(currentTranslation != null)
		{
			newTranslationProcess.copyPageAndTranslationData(currentTranslation, newTranslation);
			newTranslationProcess.copyPackageTranslationData(currentTranslation, newTranslation);
		}
		else
		{
			Translation baseTranslation = loadBaseTranslation(gtPackage);
			newTranslationProcess.copyPageAndTranslationData(baseTranslation, newTranslation);
			newTranslationProcess.copyPackageTranslationData(baseTranslation, newTranslation);
		}

		newTranslationProcess.uploadToTranslationTool(gtPackage, language);

		return newTranslation;
	}

	public void publishDraftTranslation(LanguageCode languageCode, String packageCode)
	{
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, GodToolsVersion.LATEST_VERSION);
		translation.setReleased(true);
		translationService.update(translation);
	}

	private List<Image> getImagesUsedInThisTranslation(PackageStructure packageStructure, PixelDensity pixelDensity)
	{
		String density = pixelDensity.toString();

		List<ReferencedImage> referencedImages = referencedImageService.selectByPackageStructureIdAndDensity(packageStructure.getId(), density);

		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImages)
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return imageList;
	}

	private Translation loadBaseTranslation(Package gtPackage)
	{
		return translationService.selectByLanguageIdPackageIdVersionNumber(languageService.selectByLanguageCode(new LanguageCode("en")).getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_PUBLISHED_VERSION);
	}

	private Translation getTranslationFromDatabase(LanguageCode languageCode, String packageCode, GodToolsVersion godToolsVersion)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		return translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), gtPackage.getId(), godToolsVersion);
	}

	private Image loadIcon(String packageCode)
	{
		return imageService.selectByFilename(Image.buildFilename(packageCode, "icon@2x.png"));
	}
}
