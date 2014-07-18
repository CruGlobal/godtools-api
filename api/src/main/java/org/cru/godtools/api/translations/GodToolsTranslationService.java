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
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationDownload;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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

	private NewTranslationProcess newTranslationProcess;

	private TranslationDownload translationDownload;

	@Inject
	public GodToolsTranslationService(PackageService packageService, TranslationService translationService, LanguageService languageService, PackageStructureService packageStructureService, PageStructureService pageStructureService, TranslationElementService translationElementService, ReferencedImageService referencedImageService, ImageService imageService, NewTranslationProcess newTranslationProcess, TranslationDownload translationDownload)
	{
		this.packageService = packageService;
		this.translationService = translationService;
		this.languageService = languageService;
		this.packageStructureService = packageStructureService;
		this.pageStructureService = pageStructureService;
		this.translationElementService = translationElementService;
		this.referencedImageService = referencedImageService;
		this.imageService = imageService;
		this.newTranslationProcess = newTranslationProcess;
		this.translationDownload = translationDownload;
	}

	/**
	 * Retrieves a specific package in a specific language at a specific version.
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
											  String packageCode,
											  GodToolsVersion godToolsVersion)
	{
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, godToolsVersion);
		if(translation == null) throw new NotFoundException();

		Package gtPackage = getPackage(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByTranslationId(translation.getId());

		// draft translations are always updated
		if(!translation.isReleased()) updateTranslationFromTranslationTool(translation, pageStructures, languageCode);

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());

		return GodToolsTranslation.assembleFromComponents(packageCode, packageStructure, pageStructures, translationElementList, getImagesUsedInThisPackage(packageStructure.getId()), !translation.isReleased());
	}

	/**
	 * Retrieves the latest version of all packages for specified language.
	 *
	 * @return
	 */
	public Set<GodToolsTranslation> getTranslationsForLanguage(LanguageCode languageCode, boolean includeDrafts)
	{
		Set<GodToolsTranslation> translations = Sets.newHashSet();

		for(Package gtPackage : packageService.selectAllPackages())
		{
			try
			{
				translations.add(getTranslation(languageCode, gtPackage.getCode(), GodToolsVersion.LATEST_PUBLISHED_VERSION));
			}
			catch(NotFoundException notFound) { /*oh well..*/ }
			if(includeDrafts)
			{
				try
				{
					GodToolsTranslation possibleDraftTranslation = getTranslation(languageCode, gtPackage.getCode(), GodToolsVersion.LATEST_VERSION);
					if (!translations.contains(possibleDraftTranslation)) translations.add(possibleDraftTranslation);
				}
				catch(NotFoundException notFound)
				{
					notFound.printStackTrace(); /*oh well..*/
				}
			}
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
		Package gtPackage = getPackage(packageCode);
		Language language = languageService.getOrCreateLanguage(languageCode);

		Translation currentTranslation = getTranslationFromDatabase(new LanguageCode(language.getCode()), gtPackage.getCode(), GodToolsVersion.LATEST_VERSION);
		Translation newTranslation = newTranslationProcess.saveNewTranslation(gtPackage, language, currentTranslation);

		if(currentTranslation != null)
		{
			newTranslationProcess.copyPageAndTranslationData(currentTranslation, newTranslation);
		}
		else
		{
			newTranslationProcess.copyPageAndTranslationData(newTranslation,loadBaseTranslation(gtPackage));
			newTranslationProcess.uploadTranslatableElementsToTranslationTool(gtPackage, language);
		}

		return newTranslation;
	}

	public void publishDraftTranslation(LanguageCode languageCode, String packageCode)
	{
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, GodToolsVersion.LATEST_VERSION);
		translation.setReleased(true);
		translationService.update(translation);
	}

	private Translation loadBaseTranslation(Package gtPackage)
	{
		return translationService.selectByLanguageIdPackageIdVersionNumber(languageService.selectByLanguageCode(new LanguageCode("en")).getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_PUBLISHED_VERSION);
	}

	private void updateTranslationFromTranslationTool(Translation translation, List<PageStructure> pageStructures, LanguageCode languageCode)
	{
		for(PageStructure pageStructure : pageStructures)
		{
			updateLocalTranslationElementsFromTranslationTool(translationDownload.doDownload(packageService.selectById(translation.getPackageId()).getTranslationProjectId(),
					languageCode.toString(),
					pageStructure.getFilename()), translation);
		}
	}

	private void updateLocalTranslationElementsFromTranslationTool(TranslationResults translationResults, Translation translation)
	{
		for(UUID elementId : translationResults.keySet())
		{
			TranslationElement element = translationElementService.selectyByIdTranslationId(elementId, translation.getId());
			element.setTranslatedText(translationResults.get(elementId));
			translationElementService.update(element);
		}
	}

	private Translation getTranslationFromDatabase(LanguageCode languageCode, String packageCode, GodToolsVersion godToolsVersion)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		return translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), gtPackage.getId(), godToolsVersion);
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
