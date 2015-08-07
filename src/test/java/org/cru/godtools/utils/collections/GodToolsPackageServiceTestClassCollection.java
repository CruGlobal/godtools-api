package org.cru.godtools.utils.collections;

import com.google.common.collect.ImmutableSet;
import org.cru.godtools.utils.NoOpCache;
import org.cru.godtools.api.meta.*;
import org.cru.godtools.api.translations.DraftTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.NewTranslationCreation;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.JPAStandard.*;
import org.cru.godtools.domain.services.Sql2oStandard.*;
import org.cru.godtools.utils.NoOpTranslationUpload;
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
				ImageService.class, Sql2oImageService.class, JPAImageService.class,
				ReferencedImageService.class, Sql2oReferencedImageService.class, JPAReferencedImageService.class,
				PackageService.class, Sql2oPackageService.class, JPAPackageService.class,
				PackageStructureService.class, Sql2oPackageStructureService.class, JPAPackageStructureService.class,
				LanguageService.class, Sql2oLanguageService.class, JPALanguageService.class,
				PageStructureService.class, Sql2oPageStructureService.class, JPAPageStructureService.class,
				TranslationService.class, Sql2oTranslationService.class, JPATranslationService.class,
				TranslationElementService.class, Sql2oTranslationElementService.class, JPATranslationElementService.class,
				AuthorizationService.class, Sql2oAuthorizationService.class, JPAAuthorizationService.class,
				NotificationService.class, Sql2oNotificationService.class, JPANotificationService.class,
				DeviceService.class, Sql2oDeviceService.class, JPADeviceService.class,
			GodToolsTranslationService.class,
			MetaService.class,
			OneSkyDataService.class,
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