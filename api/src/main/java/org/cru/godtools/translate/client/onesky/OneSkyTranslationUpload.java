package org.cru.godtools.translate.client.onesky;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Multimap;
import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.translate.client.TranslationUpload;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class OneSkyTranslationUpload implements TranslationUpload
{
	private OneSkyDataService oneSkyDataService;
	private FileClient fileClient;

	private Logger log = Logger.getLogger(OneSkyTranslationUpload.class);

	@Inject
	public OneSkyTranslationUpload(OneSkyDataService oneSkyDataService, FileClient fileClient)
	{
		this.oneSkyDataService = oneSkyDataService;
		this.fileClient = fileClient;
	}

	@Override
	public void doUpload(Integer projectId, String locale)
	{
		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(projectId, locale);

		for(String pageName : translationElementMultimap.keySet())
		{
			log.info("Uploading page to OneSky: " + pageName);
			try
			{
				fileClient.uploadFile(projectId, pageName, locale, buildFile(translationElementMultimap.get(pageName)));
			}
			catch (Exception e)
			{
				log.error("Error uploading page: " + pageName, e);
			}
		}
	}

	@Override
	public void doUpload(Integer projectId, String locale, String pageName)
	{
		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(projectId, locale);

		try
		{
			fileClient.uploadFile(projectId, pageName, locale, buildFile(translationElementMultimap.get(pageName)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void recordInitialUpload(Integer oneSkyProjectId, String locale)
	{
		log.info("Recording initial translation upload for OneSky project ID: " + oneSkyProjectId + " and locale: " + locale);

		Translation translation = oneSkyDataService.getTranslation(oneSkyProjectId, locale);

		log.info("Found translation:");
		Simply.logObject(translation, OneSkyTranslationUpload.class);

		for(PageStructure pageStructure : oneSkyDataService.getPageStructures(translation.getId()))
		{
			oneSkyDataService.updateLocalTranslationStatus(translation.getId(),
					pageStructure.getId(),
					new OneSkyTranslationStatus().createInitialJustUploadedStatus(pageStructure.getFilename()));
		}
	}

	/**
	 * If the list of translation status is populated, then some pages have already been uploaded for this
	 * translation.  This method only checks if there are statuses populated.  To check if an individual
	 * page, call hasTranslationBeenUploaded(projectId, locale, pageName)
	 */
	@Override
	public boolean hasTranslationBeenUploaded(Integer projectId, String locale)
	{
		log.info("Checking translation status for OneSky project ID: " + projectId + " and locale: " + locale);
		Translation translation = oneSkyDataService.getTranslation(projectId, locale);

		log.info("Found translation:");
		Simply.logObject(translation, OneSkyTranslationUpload.class);

		return !oneSkyDataService.getTranslationStatus(oneSkyDataService.getTranslation(projectId, locale).getId()).isEmpty();
	}

	@Override
	public boolean hasTranslationBeenUploaded(Integer projectId, String locale, String pageName)
	{
		return oneSkyDataService.getTranslationStatus(oneSkyDataService.getTranslation(projectId, locale).getId(), pageName) != null;
	}

	/**
	 * Builds and returns an ObjectNode.  This node is a hash of unique identifier and base translation
	 * text (English).
	 */
	private ObjectNode buildFile(Collection<TranslationElement> translationElementList)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;

		ObjectNode objectNode = new ObjectNode(factory);

		for(TranslationElement translationElement : translationElementList)
		{
			objectNode.put(translationElement.getId().toString(), translationElement.getTranslatedText());
		}

		return objectNode;
	}
}
