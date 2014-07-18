package org.cru.godtools.translate.client;

/**
 * Created by ryancarlson on 5/6/14.
 */
public interface TranslationUpload
{
	void doUpload(Integer projectId, String locale);
	void recordInitialUpload(Integer projectId, String locale);

	/**
	 * If the list of translation status is populated, then some pages have already been uploaded for this
	 * translation.
	 */
	boolean checkHasTranslationAlreadyBeenUploaded(Integer projectId, String locale);

}
