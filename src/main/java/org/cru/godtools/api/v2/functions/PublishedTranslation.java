package org.cru.godtools.api.v2.functions;

import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.s3.GodToolsS3Client;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;

public class PublishedTranslation extends AbstractTranslation
{
	@Inject
	GodToolsS3Client godToolsS3Client;

	public void pushToS3(String languageCode)
	{
		List<GodToolsTranslation> godToolsTranslations = retrieve(languageCode);

		if(godToolsTranslations.isEmpty()) throw new IllegalStateException("Translations list should not be empty during push to S3");

		TranslationPackager translationPackager = new TranslationPackager();

		InputStream compressedTranslation = translationPackager.compress(godToolsTranslations);

		godToolsS3Client.pushPackagesZippedFolder(languageCode, compressedTranslation);
	}
}
