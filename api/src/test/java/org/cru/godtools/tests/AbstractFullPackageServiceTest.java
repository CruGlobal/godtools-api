package org.cru.godtools.tests;

import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.utils.GodToolsPackageServiceTestMockDataService;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.onesky.client.TranslationClient;
import org.cru.godtools.onesky.io.TranslationDownload;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractFullPackageServiceTest extends AbstractServiceTest
{
	public static final UUID TRANSLATION_ID = UUID.randomUUID();
	public static final UUID LANGUAGE_ID = UUID.randomUUID();
	public static final UUID PACKAGE_ID = UUID.randomUUID();
	public static final UUID IMAGE_ID = UUID.randomUUID();
	public static final UUID PACKAGE_STRUCTURE_ID = UUID.randomUUID();

	protected GodToolsPackageServiceTestMockDataService mockData;

	protected PackageService packageService;
	protected LanguageService languageService;
	protected TranslationService translationService;
	protected PageStructureService pageStructureService;
	protected PackageStructureService packageStructureService;
	protected ImageService imageService;
	protected ReferencedImageService referencedImageService;
	protected TranslationElementService translationElementService;
	protected TranslationDownload translationDownload;

	@Override
	public void setup()
	{
		super.setup();

		languageService = new LanguageService(sqlConnection);
		packageService = new PackageService(sqlConnection);
		translationService = new TranslationService(sqlConnection);
		referencedImageService = new ReferencedImageService(sqlConnection);
		imageService = new ImageService(sqlConnection);
		packageStructureService = new PackageStructureService(sqlConnection);
		pageStructureService = new PageStructureService(sqlConnection);
		translationElementService = new TranslationElementService(sqlConnection);
		translationDownload = new TranslationDownload(new TranslationClient());

		mockData = new GodToolsPackageServiceTestMockDataService();

		mockData.persistPackage(languageService,
				packageService,
				packageStructureService,
				translationService,
				imageService,
				referencedImageService);
	}


	protected GodToolsPackageService createPackageService()
	{
		GodToolsTranslationService godToolsTranslationService = createTranslationService();

		return new GodToolsPackageService(godToolsTranslationService,imageService,referencedImageService);
	}

	private GodToolsTranslationService createTranslationService()
	{
		return new GodToolsTranslationService(packageService,
					translationService,
					languageService,
					packageStructureService,
					pageStructureService,
					translationElementService,
					referencedImageService,
					imageService,
					translationDownload);
	}


}
