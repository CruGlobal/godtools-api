package org.cru.godtools.tests;

import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.utils.GodToolsPackageServiceTestMockDataService;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.translations.TranslationService;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractFullPackageServiceTest extends AbstractServiceTest
{

	protected GodToolsPackageServiceTestMockDataService mockData;

	protected GodToolsPackageService godToolsPackageService;

	public static final UUID TRANSLATION_ID = UUID.randomUUID();
	public static final UUID LANGUAGE_ID = UUID.randomUUID();
	public static final UUID PACKAGE_ID = UUID.randomUUID();
	public static final UUID IMAGE_ID = UUID.randomUUID();

	@Override
	public void setup()
	{
		super.setup();

		LanguageService languageService = new LanguageService(sqlConnection);
		PackageService packageService = new PackageService(sqlConnection);
		TranslationService translationService = new TranslationService(sqlConnection);
		ReferencedImageService referencedImageService = new ReferencedImageService(sqlConnection);
		ImageService imageService = new ImageService(sqlConnection);

		mockData = new GodToolsPackageServiceTestMockDataService();

		mockData.persistPackage(languageService,
				packageService,
				translationService,
				imageService,
				referencedImageService);
	}

}
