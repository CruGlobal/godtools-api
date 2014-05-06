package org.cru.godtools.api.packages;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.onesky.domain.TranslationStatus;
import org.cru.godtools.onesky.domain.TranslationStatusService;

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

	public void updateLocalTranslationStatuses(UUID translationId, org.cru.godtools.onesky.client.TranslationStatus remoteTranslationStatus)
	{
		translationStatusService.updateAllRelatedToTranslations(translationId,
				remoteTranslationStatus.getPercentCompleted(),
				remoteTranslationStatus.getStringCount(),
				remoteTranslationStatus.getWordCount(),
				clock.currentDateTime());
	}

	public TranslationStatus getLocalTranslationStatus(UUID translationId, UUID pageStructureId)
	{
		return translationStatusService.selectByTranslationIdPageStructureId(translationId, pageStructureId);
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

	public String getPageFilename(UUID pageStructureId)
	{
		return pageStructureService.selectByid(pageStructureId).getFilename();
	}
}
