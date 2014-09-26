package org.cru.godtools.api.cache;

import com.google.common.base.Optional;
import net.spy.memcached.MemcachedClient;
import org.cru.godtools.api.translations.GodToolsTranslation;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 9/26/14.
 */
public class MemcachedGodToolsCache implements GodToolsCache
{
	private static final int CACHE_EXPIRATION_SECONDS = 3600;

	@Inject
	MemcachedClient memcachedClient;

	@Override
	public Optional<GodToolsTranslation> get(UUID translationId)
	{
		return Optional.of((GodToolsTranslation)memcachedClient.get(translationId.toString()));
	}

	@Override
	public void add(GodToolsTranslation godToolsTranslation)
	{
		memcachedClient.add(godToolsTranslation.getTranslation().getId().toString(),
				CACHE_EXPIRATION_SECONDS,
				godToolsTranslation);
	}

	@Override
	public Optional<GodToolsTranslation> remove(UUID translationId)
	{
		Optional<GodToolsTranslation> optionalTranslation = Optional.of((GodToolsTranslation) memcachedClient.get(translationId.toString()));
		memcachedClient.delete(translationId.toString());

		return optionalTranslation;
	}

	@Override
	public void replace(GodToolsTranslation godToolsTranslation)
	{
		memcachedClient.replace(godToolsTranslation.getTranslation().getId().toString(),
				CACHE_EXPIRATION_SECONDS,
				godToolsTranslation);
	}
}
