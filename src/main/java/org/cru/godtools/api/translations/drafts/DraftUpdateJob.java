package org.cru.godtools.api.translations.drafts;

import com.google.common.base.Optional;
import org.cru.godtools.api.cache.GodToolsCache;
import org.cru.godtools.api.cache.MemcachedClientProducer;
import org.cru.godtools.api.cache.MemcachedGodToolsCache;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.services.TranslationElementService;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.onesky.OneSkyTranslationDownload;
import org.cru.godtools.translate.client.onesky.TranslationClient;
import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.*;
import java.util.Set;
import java.util.UUID;

/**
 * A Quartz job that when triggered will download the latest translated elements from the translation tool.
 *
 * Once the file of downloaded elements is retrieved, each updated element's text is saved to our database (for each file
 * if there are many).
 *
 * Once all updates are made, this translation is cleared from the cache (memcached) if there is one
 * there to prompt a rebuild from the updated data next time it is requested.
 *
 * The job takes several parameters which are outlined below to help it know which elements to download.
 *
 */
public class DraftUpdateJob implements Job
{
	/**
	 * A key that callers can use to when setting the value "Project Id".  Project ID is the unique identifier in the
	 * translation tool which represents our resource (e.g. KGP) in their tool.
	 */
	public static final String PROJECT_ID_KEY = "projectIdKey";
	/**
	 * A key that callers can use when setting the value "locale".  Locale is actually the language code.  The language code
	 * represents language in which elements are downloaded (e.g. "fr")
	 */
	public static final String LOCALE_KEY = "localeKey";
	/**
	 * A key that callers can use when setting the value "pageNameSet".  Page Name Set is a set of all of the pages that should
	 * be requested from the translation tool.
	 */
	public static final String PAGE_NAME_SET_KEY = "pageNameSetKey";
	/**
	 * A key that callers can use when setting the value "translation".  @see org.cru.godtools.domain.Translation is an entity
	 * from the database.  The passed in translation is the context for this download.
	 * (e.g. We are downloading 'KGP' in 'french' version 1.5)
	 */
	public static final String TRANSLATION_KEY = "translationKey";
	/**
	 * A key that callers can use to force this download and save to the database to happen.  Normally, another server
	 * can place a marker in the cache which would prevent all others from making the same update on the same
	 * translation.  when forceUpdate is true, this check is ignored.
	 */
	public static final String FORCE_UPDATE_KEY = "forceUpdateKey";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	TranslationDownload translationDownload = new OneSkyTranslationDownload(new TranslationClient(), properties);

	@Inject
	TranslationElementService translationElementService;

	GodToolsCache cache = new MemcachedGodToolsCache(new MemcachedClientProducer().getClient(),
			properties);

	Logger log = Logger.getLogger(DraftUpdateJob.class);

	/**
	 * Required parameters (as set as key/values in jobExecutionContext.getMergedJobDataMap()
	 *  - projectId : The ID of the project in the translation tool
	 *  - locale : language code representing desired language for download
	 *  - pageNames : a Set<String> which contains the names of pages to be downloaded
	 *  - translation : a Translation entity, the context for this download
	 *
	 *  Optional parameters
	 *   - forceUpdate : overrides mechanism to ensure multiple servers don't do the same update at the same time.
	 */
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

		boolean forceUpdate = jobExecutionContext.getMergedJobDataMap().getBoolean(FORCE_UPDATE_KEY);
		log.info(String.format("Found forceUpdate: %s", forceUpdate));

		// use the cache to determine if another server has started an update on this draft in the
		// last 30s.  if so, let it do its thing
		Optional<Boolean> optionalMarker = cache.getMarker(translation.getId());

		if(!forceUpdate &&
				optionalMarker.isPresent() &&
				optionalMarker.get()) return;

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
		cache.removeMarker(translation.getId());
	}

	private void clearCache(Translation translation)
	{
		log.info(String.format("removing translation %s from cache", translation.getId()));

		cache.remove(translation.getId());
	}
}
