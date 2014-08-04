package org.cru.godtools.api.translations;

import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.TranslationUpload;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class NewTranslationCreation
{

	@Inject TranslationService translationService;
	@Inject PageStructureService pageStructureService;
	@Inject TranslationElementService translationElementService;
	@Inject TranslationUpload translationUpload;
	/**
	 * Inserts a new Translation record for the package and language that are passed in.  If currentTranslation is
	 * present, then the new translation takes the next version number.  If not, then the new translation takes
	 * version number 1.
	 *
	 * A copy of the new translation is returned.
	 */
	public Translation saveNewTranslation(Package gtPackage, Language language, Translation currentTranslation)
	{
		int nextVersionNumber = (currentTranslation == null) ? 1 : currentTranslation.getVersionNumber() + 1;

		Translation newTranslation = new Translation();
		newTranslation.setId(UUID.randomUUID());
		newTranslation.setLanguageId(language.getId());
		newTranslation.setPackageId(gtPackage.getId());
		newTranslation.setVersionNumber(nextVersionNumber);
		newTranslation.setReleased(false);
		newTranslation.setTranslatedName(currentTranslation == null ? "" : currentTranslation.getTranslatedName());

		translationService.insert(newTranslation);
		return newTranslation;
	}

	/**
	 * Loads up the PageStructures for the currentTranslation and saves a copy of each one associated to the newTranslation.
	 * Just after a new PageStructure is saved, a copied set of current PageStructure's TranslationElements are saved associated
	 * to the new PageStructure.
	 */
	public void copyPageAndTranslationData(Translation currentTranslation, Translation newTranslation)
	{
		for(PageStructure currentPageStructure : pageStructureService.selectByTranslationId(currentTranslation.getId()))
		{
			PageStructure copy = PageStructure.copyOf(currentPageStructure);
			copy.setId(UUID.randomUUID());
			copy.setTranslationId(newTranslation.getId());
			pageStructureService.insert(copy);

			// it's easier to do this in the context of the new page, so that we don't have to remember
			// the link b/w the old page and new page for a separate method call
			copyTranslationElements(currentTranslation, newTranslation, currentPageStructure, copy);
		}
	}

	public void copyPackageTranslationData(Translation currentTranslation, Translation newTranslation)
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

	public void uploadToTranslationTool(Package gtPackage, Language language)
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
