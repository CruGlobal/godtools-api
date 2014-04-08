package org.cru.godtools.tests;

import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.GodToolsPackageServiceTestMockDataService;
import org.cru.godtools.api.translations.GodToolsTranslationService;
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
	public static final UUID IMAGE_PAGE_RELATIONSHIP_ID = UUID.randomUUID();

	@Override
	public void setup()
	{
		super.setup();

		LanguageService languageService = new LanguageService(sqlConnection);
		PackageService packageService = new PackageService(sqlConnection);
		TranslationService translationService = new TranslationService(sqlConnection);
		VersionService versionService = new VersionService(sqlConnection);
		PageService pageService = new PageService(sqlConnection);
		ImagePageRelationshipService imagePageRelationshipService = new ImagePageRelationshipService(sqlConnection);
		ImageService imageService = new ImageService(sqlConnection, imagePageRelationshipService);
		GodToolsTranslationService godToolsTranslationService = new GodToolsTranslationService(packageService, versionService, translationService, languageService, pageService);

		godToolsPackageService = new GodToolsPackageService(godToolsTranslationService,imageService);

		mockData = new GodToolsPackageServiceTestMockDataService();

		mockData.persistPackage(languageService,
				packageService,
				translationService,
				versionService,
				pageService,
				imageService,
				imagePageRelationshipService);
	}

}
