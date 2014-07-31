package org.cru.godtools.tests;

import com.google.common.collect.ImmutableSet;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.translations.DraftTranslationUpdateProcess;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.NewTranslationProcess;
import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.onesky.FileClient;
import org.cru.godtools.translate.client.onesky.OneSkyDataService;
import org.cru.godtools.translate.client.onesky.OneSkyTranslationDownload;
import org.cru.godtools.translate.client.onesky.OneSkyTranslationUpload;
import org.cru.godtools.translate.client.onesky.TranslationClient;

/**
 * Created by ryancarlson on 7/30/14.
 */
public class GodToolsPackageServiceTestClassCollection
{
	static ImmutableSet<Class<?>> classSet = ImmutableSet.of(GodToolsTranslationService.class, ImageService.class,
			GodToolsPackageService.class, ReferencedImageService.class, PackageService.class,
			TranslationService.class, LanguageService.class, PackageStructureService.class,
			PageStructureService.class, TranslationElementService.class, NewTranslationProcess.class,
			DraftTranslationUpdateProcess.class, OneSkyTranslationDownload.class,
			OneSkyTranslationUpload.class, FileClient.class, TranslationClient.class, TestClockImpl.class,
			OneSkyDataService.class);

	public static Class<?>[] getClasses()
	{
		return classSet.toArray(new Class<?>[classSet.size()]);
	}
}
