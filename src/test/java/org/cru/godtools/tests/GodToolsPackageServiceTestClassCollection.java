package org.cru.godtools.tests;

import com.google.common.collect.ImmutableSet;
import org.cru.godtools.api.cache.NoOpCache;
import org.cru.godtools.api.meta.*;
import org.cru.godtools.api.translations.DraftTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.NewTranslationCreation;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.JPAStandard.*;
import org.cru.godtools.domain.services.Sql2oStandard.*;
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
				ImageService.class, Sql2oImageService.class, JPAImageService.class,
				ReferencedImageService.class, Sql2oReferencedImageService.class,
				PackageService.class, Sql2oPackageService.class, JPAPackageService.class,
				PackageStructureService.class, Sql2oPackageStructureService.class,
				LanguageService.class, Sql2oLanguageService.class, JPALanguageService.class,
				PageStructureService.class, Sql2oPageStructureService.class,
				TranslationService.class, Sql2oTranslationService.class,
				TranslationElementService.class, Sql2oTranslationElementService.class,
				OneSkyDataService.class,
				AuthorizationService.class, Sql2oAuthorizationService.class, JPAAuthorizationService.class,
				NotificationService.class, Sql2oNotificationService.class, JPANotificationService.class,
				MetaService.class,
				DeviceService.class, Sql2oDeviceService.class, JPADeviceService.class,
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
