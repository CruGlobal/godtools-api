package org.cru.godtools.translate.client.onesky;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.ccci.util.time.Clock;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * A service composed of domain services.  It provides functionality that the OneSkyTranslationUpload and OneSkkyTranslationDownload classes
 * need to manage translations.
 *
 * Created by ryancarlson on 5/2/14.
 */
public class OneSkyDataService
{
	private TranslationElementService translationElementService;
	private TranslationService translationService;
	private PackageService packageService;
	private LanguageService languageService;
	private PageStructureService pageStructureService;

	private Clock clock;

	@Inject
	public OneSkyDataService(TranslationElementService translationElementService, TranslationService translationService, PackageService packageService, LanguageService languageService, PageStructureService pageStructureService, Clock clock)
	{
		this.translationElementService = translationElementService;
		this.translationService = translationService;
		this.packageService = packageService;
		this.languageService = languageService;
		this.pageStructureService = pageStructureService;
		this.clock = clock;
	}

	public Multimap<String, TranslationElement> getTranslationElements(Integer oneskyProjectId, String locale)
	{
		Translation translation = getTranslation(oneskyProjectId, locale);

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId(), "page_name", "display_order");

		Multimap<String, TranslationElement> translationElementMultimap = LinkedListMultimap.create();

		for(TranslationElement translationElement : translationElementList)
		{
			translationElementMultimap.put(translationElement.getPageName(), translationElement);
		}

		return translationElementMultimap;
	}

	public Translation getTranslation(Integer oneskyProjectId, String locale)
	{
		Language language = languageService.selectByLanguageCode(new LanguageCode(locale));
		Package gtPackage = packageService.selectByOneskyProjectId(oneskyProjectId);

		return translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_VERSION);

	}

	public List<PageStructure> getTranslationStatus(UUID translationId)
	{
		return pageStructureService.selectByTranslationId(translationId);
	}

	public PageStructure getTranslationStatus(UUID translationId, String pageName)
	{
		for(PageStructure pageStructure : pageStructureService.selectByTranslationId(translationId))
		{
			if(pageStructure.getFilename().equals(pageName))
			{
				return pageStructure;
			}
		}

		return null;
	}

	public void updateLocalTranslationStatus(PageStructure pageStructure, OneSkyTranslationStatus oneSkyTranslationStatus)
	{
		pageStructure.updateCachedStatus(oneSkyTranslationStatus.getPercentCompleted(),
				oneSkyTranslationStatus.getWordCount(),
				oneSkyTranslationStatus.getStringCount(),
				clock.currentDateTime());

		pageStructureService.update(pageStructure);
	}

	public List<PageStructure> getPageStructures(UUID translationId)
	{
		return pageStructureService.selectByTranslationId(translationId);
	}
}
