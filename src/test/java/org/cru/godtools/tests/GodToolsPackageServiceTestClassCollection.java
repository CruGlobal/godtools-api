package org.cru.godtools.tests;

import com.google.common.collect.ImmutableSet;
import org.cru.godtools.api.cache.NoOpCache;
import org.cru.godtools.api.meta.*;
import org.cru.godtools.api.services.*;
import org.cru.godtools.api.services.JPAStandard.*;
import org.cru.godtools.api.services.Sql2oStandard.*;
import org.cru.godtools.api.translations.DraftTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.NewTranslationCreation;
import org.cru.godtools.api.services.ReferencedImageService;
import org.cru.godtools.api.services.LanguageService;
import org.cru.godtools.api.services.PackageService;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.NoOpTranslationUpload;
import org.cru.godtools.translate.client.onesky.FileClient;
import org.cru.godtools.translate.client.onesky.OneSkyDataService;
import org.cru.godtools.translate.client.onesky.OneSkyTranslationDownload;
import org.cru.godtools.translate.client.onesky.TranslationClient;

/**
 * Created by ryancarlson on 7/30/14.
 */
public class GodToolsPackageServiceTestClassCollection
{
	static ImmutableSet<Class<?>> classSet = ImmutableSet.of(
				GodToolsTranslationService.class,
				ReferencedImageService.class, Sql2oReferencedImageService.class,
				PackageService.class,
				TranslationService.class,
				LanguageService.class, Sql2oLanguageService.class,
				PackageStructureService.class, Sql2oPackageService.class,
				PageStructureService.class,
				TranslationElementService.class,
				OneSkyDataService.class,
				AuthorizationService.class, Sql2oAuthorizationService.class, JPAAuthorizationService.class,
				ImageService.class, Sql2oImageService.class,
				NotificationService.class, Sql2oNotificationService.class,
				MetaService.class,
				DeviceService.class, Sql2oDeviceService.class,
			NewTranslationCreation.class,
			DraftTranslation.class,
			OneSkyTranslationDownload.class,
			NoOpTranslationUpload.class,
			FileClient.class,
			TranslationClient.class,
			NoOpCache.class);

	public static Class<?>[] getClasses()
	{
		return classSet.toArray(new Class<?>[classSet.size()]);
	}
}
