package org.cru.godtools.tests;

import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.images.domain.ReferencedImageService;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.GodToolsPackageServiceTestMockDataService;
import org.cru.godtools.api.translations.domain.TranslationService;

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
	public static final UUID VERSION_ID = UUID.randomUUID();
	public static final UUID PAGE_ID = UUID.randomUUID();
	public static final UUID IMAGE_ID = UUID.randomUUID();

	@Override
	public void setup()
	{
		super.setup();

		LanguageService languageService = new LanguageService(sqlConnection);
		PackageService packageService = new PackageService(sqlConnection);
		TranslationService translationService = new TranslationService(sqlConnection);
		ReferencedImageService referencedImageService = new ReferencedImageService(sqlConnection);
		ImageService imageService = new ImageService(sqlConnection, referencedImageService);

//		godToolsPackageService = new GodToolsPackageService(packageService, versionService, translationService, languageService, pageService, imageService);

		mockData = new GodToolsPackageServiceTestMockDataService();

		mockData.persistPackage(languageService,
				packageService,
				translationService,
				imageService,
				referencedImageService);
	}

}
