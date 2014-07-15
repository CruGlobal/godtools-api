package org.cru.godtools.onesky.io;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Multimap;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.onesky.client.FileClient;
import org.cru.godtools.onesky.client.OneSkyTranslationStatus;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationUpload
{
	private OneSkyDataService oneSkyDataService;
	private FileClient fileClient;

	@Inject
	public TranslationUpload(OneSkyDataService oneSkyDataService, FileClient fileClient)
	{
		this.oneSkyDataService = oneSkyDataService;
		this.fileClient = fileClient;
	}

	public void doUpload(Integer oneSkyProjectId, String locale)
	{
		Translation translation = oneSkyDataService.getTranslation(oneSkyProjectId, locale);

		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translation.getId());

		for(String pageName : translationElementMultimap.keySet())
		{
			try
			{
				fileClient.uploadFile(oneSkyProjectId, pageName, locale, buildFile(translationElementMultimap.get(pageName)));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public void recordInitialUpload(Integer oneSkyProjectId, String locale)
	{
		Translation translation = oneSkyDataService.getTranslation(oneSkyProjectId, locale);

		for(PageStructure pageStructure : oneSkyDataService.getPageStructures(translation.getId()))
		{
			oneSkyDataService.updateLocalTranslationStatus(translation.getId(),
					pageStructure.getId(),
					OneSkyTranslationStatus.createInitialJustUploadedStatus(pageStructure.getFilename()));
		}
	}

	/**
	 * If the list of translation status is populated, then some pages have already been uploaded for this
	 * translation.
	 */
	public boolean checkHasTranslationAlreadyBeenUploaded(Integer oneSkyProjectId, String locale)
	{
		Translation translation = oneSkyDataService.getTranslation(oneSkyProjectId, locale);
		return !oneSkyDataService.getCurrentTranslationStatus(translation.getId()).isEmpty();
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
