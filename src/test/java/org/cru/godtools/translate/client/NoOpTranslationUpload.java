package org.cru.godtools.translate.client;

/**
 * Created by ryancarlson on 7/31/14.
 */
public class NoOpTranslationUpload implements TranslationUpload
{
	@Override
	public void doUpload(Integer projectId, String locale)
	{

	}

	@Override
	public void doUpload(Integer projectId, String locale, String pageName)
	{

	}

	@Override
	public void recordInitialUpload(Integer projectId, String locale)
	{

	}

	@Override
	public boolean hasTranslationBeenUploaded(Integer projectId, String locale)
	{
		return false;
	}

	@Override
	public boolean hasTranslationBeenUploaded(Integer projectId, String locale, String pageName)
	{
		return false;
	}
}
