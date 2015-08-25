package org.cru.godtools.api.v2.functions;

import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationUpload;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class DraftTranslation extends AbstractTranslation
{
	@Inject
	TranslationUpload translationUpload;
	@Inject
	TranslationDownload translationDownload;

	public void create(String languageCode, String packageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(new LanguageCode(languageCode));
		Translation baseTranslation = null;

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_VERSION);

		// only allow one draft per translation
		if(currentTranslation != null && currentTranslation.isDraft()) return;

		Translation newTranslation = saveNewTranslation(gtPackage, language, currentTranslation);

		if(currentTranslation == null)
		{
			baseTranslation = loadBaseTranslation(gtPackage.getId());
		}

		copyPageAndTranslationData(currentTranslation == null ? baseTranslation : currentTranslation,
				newTranslation,
				false);

		copyPackageTranslationData(currentTranslation == null ? baseTranslation : currentTranslation,
				newTranslation);

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

		currentTranslation.setReleased(true);
		translationService.update(currentTranslation);
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
	private void copyPageAndTranslationData(Translation baseTranslation,
											Translation newTranslation,
											boolean translationIsNew)
	{
		List<PageStructure> pageStructures = pageStructureService.selectByTranslationId(baseTranslation.getId());

		for(PageStructure currentPageStructure : pageStructures)
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
			copyTranslationElements(baseTranslation, newTranslation, currentPageStructure, copy);
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

	private Translation loadBaseTranslation(UUID packageId)
	{
		return translationService.selectByLanguageIdPackageIdVersionNumber(languageService.selectByLanguageCode(new LanguageCode("en")).getId(),
				packageId,
				GodToolsVersion.LATEST_PUBLISHED_VERSION);
	}

}
