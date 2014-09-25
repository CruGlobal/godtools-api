package org.cru.godtools.api.translations.drafts;

import net.spy.memcached.MemcachedClient;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 9/24/14.
 */
public class DraftUpdateJob implements Job
{
	public static final String PROJECT_ID_KEY = "projectIdKey";
	public static final String LOCALE_KEY = "localeKey";
	public static final String PAGE_NAME_SET_KEY = "pageNameSetKey";
	public static final String TRANSLATION_KEY = "translationKey";

	@Inject
	TranslationDownload translationDownload;
	@Inject
	TranslationElementService translationElementService;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	MemcachedClient cache;

	Logger log = Logger.getLogger(DraftUpdateJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		Integer projectId = (Integer) jobExecutionContext.getMergedJobDataMap().get(PROJECT_ID_KEY);
		log.info(String.format("Found projectId: %s", projectId.toString()));

		String locale = (String) jobExecutionContext.getMergedJobDataMap().get(LOCALE_KEY);
		log.info(String.format("Found locale: %s", locale));

		Set<String> pageNames = (Set<String>) jobExecutionContext.getMergedJobDataMap().get(PAGE_NAME_SET_KEY);

		Translation translation = (Translation) jobExecutionContext.getMergedJobDataMap().get(TRANSLATION_KEY);
		log.info(String.format("Found translation: %s", translation.getId().toString()));

		for(String pageName : pageNames)
		{
			TranslationResults translationResults = translationDownload.doDownload(projectId, locale, pageName);

			for(UUID elementId : translationResults.keySet())
			{
				TranslationElement element = translationElementService.selectyByIdTranslationId(elementId, translation.getId());
				element.setTranslatedText(translationResults.get(elementId));
				translationElementService.update(element);
			}
		}

		GodToolsTranslation godToolsTranslation = godToolsTranslationService.getTranslation(translation);

		log.info(String.format("adding translation %s to cache", translation.getId()));
		cache.add(godToolsTranslation.getTranslation().getId().toString(), 3600, godToolsTranslation);
	}
}
