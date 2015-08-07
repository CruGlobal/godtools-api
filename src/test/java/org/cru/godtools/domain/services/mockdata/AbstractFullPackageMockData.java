package org.cru.godtools.domain.services.mockdata;

import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.model.Translation;
import org.jboss.arquillian.junit.*;
import org.junit.runner.*;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
@RunWith(Arquillian.class)
public class AbstractFullPackageMockData
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
