package org.cru.godtools.api.packages;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by ryancarlson on 5/2/14.
 */
public class OneSkyDataService
{
	TranslationElementService translationElementService;
	LanguageService languageService;
	PackageService packageService;
	TranslationService translationService;

	@Inject
	public OneSkyDataService(TranslationElementService translationElementService, LanguageService languageService, PackageService packageService, TranslationService translationService)
	{
		this.translationElementService = translationElementService;
		this.languageService = languageService;
		this.packageService = packageService;
		this.translationService = translationService;
	}

	public Multimap<String, TranslationElement> getTranslationElements(String packageCode, LanguageCode languageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.selectByLanguageCode(languageCode);
		Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId(), "page_name", "display_order");

		Multimap<String, TranslationElement> translationElementMultimap = ArrayListMultimap.create();

		for(TranslationElement translationElement : translationElementList)
		{
			translationElementMultimap.put(translationElement.getPageName(), translationElement);
		}

		return translationElementMultimap;
	}
}
