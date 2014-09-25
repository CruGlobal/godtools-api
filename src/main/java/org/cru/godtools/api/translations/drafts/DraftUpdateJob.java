package org.cru.godtools.api.translations.drafts;

import net.spy.memcached.MemcachedClient;
import org.cru.godtools.api.cache.MemcachedClientProducer;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.domain.database.SqlConnectionProducer;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.onesky.OneSkyTranslationDownload;
import org.cru.godtools.translate.client.onesky.TranslationClient;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
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

	TranslationDownload translationDownload = new OneSkyTranslationDownload(new TranslationClient());

	TranslationElementService translationElementService = new TranslationElementService(new SqlConnectionProducer().getSqlConnection());

	MemcachedClient cache = new MemcachedClientProducer().getClient();

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

		// use the cache to determine if another server has started an update on this draft in the
		// last 30s.  if so, let it do its thing
		String updateMarkerKey = translation.getId().toString() + "-updating-marker";
		if(cache.get(updateMarkerKey) == null) return;

		// if we are the first one to do this update, then add a marker to the cache, claiming it
		cache.add(updateMarkerKey, 30, new Object());

		for (String pageName : pageNames)
		{
			TranslationResults translationResults = translationDownload.doDownload(projectId, locale, pageName);

			for (UUID elementId : translationResults.keySet())
			{
				TranslationElement element = translationElementService.selectyByIdTranslationId(elementId, translation.getId());
				element.setTranslatedText(translationResults.get(elementId));
				translationElementService.update(element);
			}
		}

		clearCache(translation);

		// delete the marker (it was only good for 30s anyways)
		cache.delete(updateMarkerKey);
	}

	private void clearCache(Translation translation)
	{
		log.info(String.format("removing translation %s from cache", translation.getId()));

		cache.delete(translation.getId().toString());
	}
}
