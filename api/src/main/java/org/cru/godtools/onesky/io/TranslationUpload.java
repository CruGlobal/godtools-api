package org.cru.godtools.onesky.io;

import com.google.common.collect.Multimap;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.onesky.client.FileClient;
import org.cru.godtools.onesky.client.OneSkyTranslationStatus;

import javax.inject.Inject;

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

	public void doUpload(Integer oneskyProjectId, String locale)
	{
		Translation translation = oneSkyDataService.getTranslation(oneskyProjectId, locale);

		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translation.getId());

		for(String pageName : translationElementMultimap.keySet())
		{
			try
			{
				fileClient.uploadFile(oneskyProjectId, pageName, locale, translationElementMultimap.get(pageName));
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
	public void recordInitialUpload(Integer oneskyProjectId, String locale)
	{
		Translation translation = oneSkyDataService.getTranslation(oneskyProjectId, locale);

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
	public boolean checkHasTranslationAlreadyBeenUploaded(Integer oneskyProjectId, String locale)
	{
		Translation translation = oneSkyDataService.getTranslation(oneskyProjectId, locale);
		return !oneSkyDataService.getCurrentTranslationStatus(translation.getId()).isEmpty();
	}
}
