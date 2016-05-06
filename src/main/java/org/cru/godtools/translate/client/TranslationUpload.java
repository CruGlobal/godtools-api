package org.cru.godtools.translate.client;


/**
 * Created by ryancarlson on 5/6/14.
 */
public interface TranslationUpload
{
	void doUpload(Integer projectId, String locale);
	void doUpload(Integer projectId, String locale, String pageName);
	void doUpload(Integer projectId, String locale, String pageName, boolean deprecateRemovedPhrases);

	void recordInitialUpload(Integer projectId, String locale);

	/**
	 * If the list of translation status is populated, then some pages have already been uploaded for this
	 * translation.
	 */
	boolean hasTranslationBeenUploaded(Integer projectId, String locale);
	boolean hasTranslationBeenUploaded(Integer projectId, String locale, String pageName);

}
