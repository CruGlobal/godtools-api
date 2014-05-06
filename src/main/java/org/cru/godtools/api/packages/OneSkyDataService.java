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
import java.util.UUID;

/**
 * Created by ryancarlson on 5/2/14.
 */
public class OneSkyDataService
{
	TranslationElementService translationElementService;
	TranslationService translationService;
	PackageService packageService;
	LanguageService languageService;

	@Inject
	public OneSkyDataService(TranslationElementService translationElementService, TranslationService translationService, PackageService packageService, LanguageService languageService)
	{
		this.translationElementService = translationElementService;
		this.translationService = translationService;
		this.packageService = packageService;
		this.languageService = languageService;
	}

	public Multimap<String, TranslationElement> getTranslationElements(UUID translationId)
	{
		Translation translation = translationService.selectById(translationId);

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId(), "page_name", "display_order desc");

		Multimap<String, TranslationElement> translationElementMultimap = ArrayListMultimap.create();

		for(TranslationElement translationElement : translationElementList)
		{
			translationElementMultimap.put(translationElement.getPageName(), translationElement);
		}

		return translationElementMultimap;
	}

	public void saveTranslationElements(Multimap<String, TranslationElement> translationElementMultimap)
	{
		for(String pageName : translationElementMultimap.keySet())
		{
			for(TranslationElement translationElement : translationElementMultimap.get(pageName))
			{
				translationElementService.update(translationElement);
			}
		}
	}

	public Integer getOneskyProjectId(UUID translationId)
	{
		Translation translation = translationService.selectById(translationId);

		return packageService.selectById(translation.getPackageId()).getOneskyProjectId();
	}

	public String getLocale(UUID translationId)
	{
		Translation translation = translationService.selectById(translationId);

		return languageService.selectLanguageById(translation.getLanguageId()).getPath();
	}
}
