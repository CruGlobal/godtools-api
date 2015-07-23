package org.cru.godtools.api.v2.functions;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationUpload;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TranslationDraft
{
	@Inject
	TranslationService translationService;
	@Inject
	LanguageService languageService;
	@Inject
	PackageService packageService;
	@Inject
	PackageStructureService packageStructureService;
	@Inject
	PageStructureService pageStructureService;
	@Inject
	TranslationElementService translationElementService;
	@Inject
	ReferencedImageService referencedImageService;
	@Inject
	ImageService imageService;
	@Inject
	TranslationUpload translationUpload;
	@Inject
	TranslationDownload translationDownload;

	static final Set<String> packageCodes = Sets.newHashSet("kgp", "fourlaws", "satisfied");

	public List<GodToolsTranslation> retrieve(String languageCode)
	{
		List<GodToolsTranslation> godToolsTranslationList = Lists.newArrayList();

		for(String packageCode : packageCodes)
		{
			Optional<GodToolsTranslation> godToolsTranslationOptional = retrieve(languageCode, packageCode);

			if(godToolsTranslationOptional.isPresent())
			{
				godToolsTranslationList.add(godToolsTranslationOptional.get());
			}
		}

		return godToolsTranslationList;
	}

	public Optional<GodToolsTranslation> retrieve(String languageCode, String packageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(new LanguageCode(languageCode));

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_VERSION);

		if(currentTranslation == null || currentTranslation.isReleased()) return Optional.absent();

		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructureList = pageStructureService.selectByTranslationId(currentTranslation.getId());
		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(currentTranslation.getId());
		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImageService.selectByPackageStructureId(packageStructure.getId()))
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return Optional.fromNullable(
				GodToolsTranslation.assembleFromComponents(gtPackage,
						language,
						currentTranslation,
						packageStructure,
						pageStructureList,
						translationElementList,
						imageList,
						imageService.selectByFilename(
								Image.buildFilename(packageCode, "icon@2x.png"))));
	}

	public void create(String languageCode, String packageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(new LanguageCode(languageCode));

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_VERSION);

		// only allow one draft per translation
		if(currentTranslation != null && currentTranslation.isDraft()) return;

		Translation newTranslation = saveNewTranslation(gtPackage, language, currentTranslation);

		copyPageAndTranslationData(currentTranslation, newTranslation, false);
		copyPackageTranslationData(currentTranslation, newTranslation);

		uploadToTranslationTool(gtPackage, language);
	}

	public void publish(String languageCode, String packageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(new LanguageCode(languageCode));

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_VERSION);

		if(currentTranslation == null ||
				currentTranslation.isReleased()) throw new IllegalStateException("No draft to be published");

		downloadLatestTranslations(gtPackage,language,currentTranslation);
	}

	private void downloadLatestTranslations(Package gtPackage, Language language, Translation currentTranslation)
	{
		for(PageStructure pageStructure : pageStructureService.selectByTranslationId(currentTranslation.getId()))
		{
			TranslationResults downloadedTranslations = translationDownload.doDownload(gtPackage.getTranslationProjectId(),
					language.getPath(),
					pageStructure.getFilename());

			for(UUID translatedElementId : downloadedTranslations.keySet())
			{
				translationElementService.update(translatedElementId,
						currentTranslation.getId(),
						downloadedTranslations.get(translatedElementId));
			}
		}
		currentTranslation.setReleased(true);
		translationService.update(currentTranslation);
	}

	/**
	 * Inserts a new Translation record for the package and language that are passed in.  If currentTranslation is
	 * present, then the new translation takes the next version number.  If not, then the new translation takes
	 * version number 1.
	 *
	 * A copy of the new translation is returned.
	 */
	private Translation saveNewTranslation(Package gtPackage, Language language, Translation currentTranslation)
	{
		int nextVersionNumber = (currentTranslation == null) ? 1 : currentTranslation.getVersionNumber() + 1;

		Translation newTranslation = new Translation();
		newTranslation.setId(UUID.randomUUID());
		newTranslation.setLanguageId(language.getId());
		newTranslation.setPackageId(gtPackage.getId());
		newTranslation.setVersionNumber(nextVersionNumber);
		newTranslation.setReleased(false);
		newTranslation.setTranslatedName(currentTranslation == null ? gtPackage.getName() : currentTranslation.getTranslatedName());

		translationService.insert(newTranslation);
		return newTranslation;
	}

	/**
	 * Loads up the PageStructures for the currentTranslation and saves a copy of each one associated to the newTranslation.
	 * Just after a new PageStructure is saved, a copied set of current PageStructure's TranslationElements are saved associated
	 * to the new PageStructure.
	 */
	private void copyPageAndTranslationData(Translation currentTranslation, Translation newTranslation, boolean translationIsNew)
	{
		for(PageStructure currentPageStructure : pageStructureService.selectByTranslationId(currentTranslation.getId()))
		{
			PageStructure copy = PageStructure.copyOf(currentPageStructure);
			copy.setId(UUID.randomUUID());
			copy.setTranslationId(newTranslation.getId());

			// when starting a new translation, set these fields to null so they get pushed to OneSky
			if(translationIsNew)
			{
				copy.setPercentCompleted(null);
				copy.setWordCount(null);
				copy.setStringCount(null);
				copy.setLastUpdated(null);
			}

			pageStructureService.insert(copy);

			// it's easier to do this in the context of the new page, so that we don't have to remember
			// the link b/w the old page and new page for a separate method call
			copyTranslationElements(currentTranslation, newTranslation, currentPageStructure, copy);
		}
	}

	private void copyPackageTranslationData(Translation currentTranslation, Translation newTranslation)
	{
		for(TranslationElement currentTranslationElement : translationElementService.selectByTranslationId(currentTranslation.getId()))
		{
			if(currentTranslationElement.getPageStructureId() == null)
			{
				TranslationElement copy = TranslationElement.copyOf(currentTranslationElement);
				copy.setTranslationId(newTranslation.getId());
				translationElementService.insert(copy);
			}
		}
	}

	/**
	 * Creates new copies of translation_elements for the new Translation and PageStructure.
	 *
	 * It's imperative that the TranslationElement copy.id stays the same, b/c that is our reference to the element in
	 * translate.  If that ID were to change, then things would be bad.  translation_elements has a composite key(id, translation_id)
	 */
	private void copyTranslationElements(Translation currentTranslation, Translation newTranslation, PageStructure currentPage, PageStructure newPage)
	{
		for(TranslationElement currentTranslationElement : translationElementService.selectByTranslationIdPageStructureId(currentTranslation.getId(), currentPage.getId()))
		{
			TranslationElement copy = TranslationElement.copyOf(currentTranslationElement);
			copy.setTranslationId(newTranslation.getId());
			copy.setPageStructureId(newPage.getId());
			translationElementService.insert(copy);
		}
	}

	private void uploadToTranslationTool(Package gtPackage, Language language)
	{
		// if not, copy page structures and translation elements from the base translation (loaded here) which is likely English
		// and copy them to the new translation.  also upload the translation elements to translation tool.
		if(!translationUpload.hasTranslationBeenUploaded(gtPackage.getTranslationProjectId(),language.getPath()))
		{
			translationUpload.doUpload(gtPackage.getTranslationProjectId(), language.getPath());
			translationUpload.recordInitialUpload(gtPackage.getTranslationProjectId(), language.getPath());
		}
	}
}
