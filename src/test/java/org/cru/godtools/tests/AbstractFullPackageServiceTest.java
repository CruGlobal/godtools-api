package org.cru.godtools.tests;

import org.cru.godtools.api.translations.GodToolsTranslationServiceTestMockData;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.jboss.arquillian.testng.Arquillian;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractFullPackageServiceTest extends Arquillian
{
	public static final UUID TRANSLATION_ID = UUID.randomUUID();
	public static final UUID LANGUAGE_ID = UUID.randomUUID();
	public static final UUID PACKAGE_ID = UUID.randomUUID();
	public static final UUID IMAGE_ID = UUID.randomUUID();
	public static final UUID ICON_ID = UUID.randomUUID();
	public static final UUID PACKAGE_STRUCTURE_ID = UUID.randomUUID();
	public static final UUID PAGE_STRUCTURE_ID = UUID.randomUUID();


	@Inject
	private PackageService packageService;
	@Inject
	private LanguageService languageService;
	@Inject
	private TranslationService translationService;
	@Inject
	PageStructureService pageStructureService;
	@Inject
	TranslationElementService translationElementService;
	@Inject
	private PackageStructureService packageStructureService;
	@Inject
	private ImageService imageService;
	@Inject
	private ReferencedImageService referencedImageService;

	protected void saveTestPackage()
	{
		GodToolsTranslationServiceTestMockData.persistPackage(languageService,
				packageService,
				packageStructureService,
				pageStructureService,
				translationElementService,
				translationService,
				imageService,
				referencedImageService);
	}

	protected void setTestPackageDraftStatus()
	{
		Translation translation = translationService.selectById(TRANSLATION_ID);
		translation.setReleased(false);
		translation.setVersionNumber(2);
		translation.setTranslatedName("Connaitre Dieu Personellement");
		translationService.update(translation);
	}
}
