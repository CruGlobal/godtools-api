package org.cru.godtools.api.packages;

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
import org.cru.godtools.domain.translations.TranslationStatus;
import org.cru.godtools.domain.translations.TranslationStatusService;
import org.cru.godtools.translate.client.onesky.OneSkyTranslationStatus;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/2/14.
 */
public class OneSkyDataService
{
	private TranslationElementService translationElementService;
	private TranslationService translationService;
	private TranslationStatusService translationStatusService;
	private PackageService packageService;
	private LanguageService languageService;
	private PageStructureService pageStructureService;

	private Clock clock;

	@Inject
	public OneSkyDataService(TranslationElementService translationElementService, TranslationService translationService, TranslationStatusService translationStatusService, PackageService packageService, LanguageService languageService, PageStructureService pageStructureService, Clock clock)
	{
		this.translationElementService = translationElementService;
		this.translationService = translationService;
		this.translationStatusService = translationStatusService;
		this.packageService = packageService;
		this.languageService = languageService;
		this.pageStructureService = pageStructureService;
		this.clock = clock;
	}

	public Multimap<String, TranslationElement> getTranslationElements(UUID translationId)
	{
		Translation translation = translationService.selectById(translationId);

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

	public List<TranslationStatus> getCurrentTranslationStatus(UUID translationId)
	{
		return translationStatusService.selectByTranslationId(translationId);
	}

	public void updateLocalTranslationStatus(UUID translationId, UUID pageStructureId, OneSkyTranslationStatus oneSkyTranslationStatus)
	{
		
		if(translationStatusService.selectByTranslationIdPageStructureId(translationId,pageStructureId) != null)
		{
			translationStatusService.update(new TranslationStatus(translationId,
					pageStructureId,
					oneSkyTranslationStatus.getPercentCompleted(),
					oneSkyTranslationStatus.getStringCount(),
					oneSkyTranslationStatus.getWordCount(),
					clock.currentDateTime()));
		}
		else
		{
			translationStatusService.insert(new TranslationStatus(translationId,
					pageStructureId,
					oneSkyTranslationStatus.getPercentCompleted(),
					oneSkyTranslationStatus.getStringCount(),
					oneSkyTranslationStatus.getWordCount(),
					clock.currentDateTime()));
		}
	}

	public List<PageStructure> getPageStructures(UUID translationId)
	{
		return pageStructureService.selectByTranslationId(translationId);
	}
}
