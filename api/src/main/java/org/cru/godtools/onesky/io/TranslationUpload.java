package org.cru.godtools.onesky.io;

import com.google.common.collect.Multimap;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.onesky.client.FileClient;

import javax.inject.Inject;
import java.util.UUID;

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

	public void doUpload(UUID translationId)
	{
		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translationId);

		for(String pageName : translationElementMultimap.keySet())
		{
			try
			{
				fileClient.uploadFile(oneSkyDataService.getOneskyProjectId(translationId), pageName, translationElementMultimap.get(pageName));
			}
			catch(Exception e)
			{
				e.printStackTrace();;
			}
		}
	}
}
