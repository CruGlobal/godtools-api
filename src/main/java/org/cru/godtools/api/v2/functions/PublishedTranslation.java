package org.cru.godtools.api.v2.functions;

import org.cru.godtools.api.meta.MetaService;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.s3.GodToolsS3Client;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;

public class PublishedTranslation extends AbstractTranslation
{
	@Inject
	GodToolsS3Client godToolsS3Client;

	@Inject
	MetaService metaService;

	public void pushToS3(String languageCode, String packageCode)
	{
		List<GodToolsTranslation> godToolsTranslations = retrieve(languageCode);

		if(godToolsTranslations.isEmpty()) throw new IllegalStateException("Translations list should not be empty during push to S3");

		TranslationPackager translationPackager = new TranslationPackager();

		InputStream compressedTranslation = translationPackager.compress(godToolsTranslations, true);

		godToolsS3Client.pushPackagesZippedFolder(languageCode, compressedTranslation);

		godToolsS3Client.pushMetaFile(metaService.getAllMetaResults(false, false));

		for (GodToolsTranslation translation : godToolsTranslations)
		{
			// only update the actual translation that was updated
			if (!translation.getPackageCode().equalsIgnoreCase(packageCode)) continue;

			InputStream textOnlyStream = translationPackager.compress(translation, false);
			godToolsS3Client.pushTranslationsZippedFile(languageCode, packageCode, textOnlyStream);
		}
	}
}
