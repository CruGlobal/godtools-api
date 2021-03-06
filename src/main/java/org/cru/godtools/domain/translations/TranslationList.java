package org.cru.godtools.domain.translations;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/28/15.
 */
public class TranslationList extends ForwardingList<Translation>
{
	final List<Translation> translationList = Lists.newArrayList();

	public TranslationList(List<Translation> translationList)
	{
		if(translationList != null)
		{
			this.translationList.addAll(translationList);
		}
	}

	@Override
	protected List<Translation> delegate()
	{
		return translationList;
	}

	/**
	 * This method pares the list of results down to the Translation with the latest version number for each package.
	 * This method assumes the packages are already filtered by released status.
	 */
	public TranslationList pareResults()
	{
		Map<UUID, Translation> latestTranslationForPackageId = Maps.newHashMap();

		for(Translation translation : translationList)
		{
			if(!latestTranslationForPackageId.containsKey(translation.getPackageId()) ||
					latestTranslationForPackageId.get(translation.getPackageId()).getVersionNumber() < translation.getVersionNumber())
			{
				latestTranslationForPackageId.put(translation.getPackageId(), translation);
			}
		}

		return new TranslationList(new ArrayList<>(latestTranslationForPackageId.values()));
	}

	public TranslationList pareResults(Boolean released)
	{
		List<Translation> translations = Lists.newArrayList();

		for(Translation translation : translationList)
		{
			if(translation.isReleased() == released)
			{
				translations.add(translation);
			}
		}

		return new TranslationList(translations);
	}

	public TranslationList pareResults(UUID packageId)
	{
		List<Translation> translations = Lists.newArrayList();

		for(Translation translation : translationList)
		{
			if(translation.getPackageId().equals(packageId))
			{
				translations.add(translation);
			}
		}

		return new TranslationList(translations);
	}


}
