package org.cru.godtools.tests;

import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.translations.TranslationService;

import javax.inject.Inject;
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
	public static final UUID ICON_ID = UUID.randomUUID();
	public static final UUID PACKAGE_STRUCTURE_ID = UUID.randomUUID();

	@Inject
	protected PackageService packageService;
	@Inject
	protected LanguageService languageService;
	@Inject
	protected TranslationService translationService;
	@Inject
	protected PackageStructureService packageStructureService;
	@Inject
	protected ImageService imageService;
	@Inject
	protected ReferencedImageService referencedImageService;
}
