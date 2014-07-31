package org.cru.godtools.translate.client;

/**
 *
 * Created by ryancarlson on 5/6/14.
 */
public interface TranslationDownload
{
	public TranslationResults doDownload(Integer projectId, String locale, String pageName);
	public TranslationStatus checkTranslationStatus(Integer projectId, String locale, String pageName);
}
