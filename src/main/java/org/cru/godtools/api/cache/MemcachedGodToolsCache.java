package org.cru.godtools.api.cache;

import com.google.common.base.Optional;
import net.spy.memcached.MemcachedClient;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.properties.GodToolsProperties;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 9/26/14.
 */
public class MemcachedGodToolsCache implements GodToolsCache
{
	private static final int CACHE_EXPIRATION_SECONDS = 3600;

	MemcachedClient memcachedClient;
	GodToolsProperties properties;

	@Inject
	public MemcachedGodToolsCache(MemcachedClient memcachedClient, GodToolsProperties properties)
	{
		this.memcachedClient = memcachedClient;
		this.properties = properties;
	}

	@Override
	public Optional<GodToolsTranslation> get(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			return Optional.fromNullable((GodToolsTranslation)memcachedClient.get(translationId.toString()));
		}
		else return Optional.absent();

	}

	@Override
	public void add(GodToolsTranslation godToolsTranslation)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			memcachedClient.add(godToolsTranslation.getTranslation().getId().toString(),
					CACHE_EXPIRATION_SECONDS,
					godToolsTranslation);
		}
	}

	@Override
	public Optional<GodToolsTranslation> remove(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			Optional<GodToolsTranslation> optionalTranslation = Optional.fromNullable((GodToolsTranslation) memcachedClient.get(translationId.toString()));
			memcachedClient.delete(translationId.toString());

			return optionalTranslation;
		}
		else return Optional.absent();
	}

	@Override
	public void replace(GodToolsTranslation godToolsTranslation)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			memcachedClient.replace(godToolsTranslation.getTranslation().getId().toString(),
					CACHE_EXPIRATION_SECONDS,
					godToolsTranslation);
		}
	}

	@Override
	public Optional<Boolean> getMarker(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			String marker = (String) memcachedClient.get(buildMarkerKey(translationId));
			return Optional.of(marker != null);
		}
		else return Optional.absent();
	}

	@Override
	public void recordMarker(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			String updateMarkerKey = buildMarkerKey(translationId);

			memcachedClient.add(updateMarkerKey, 30, new String("marker"));
		}
	}

	@Override
	public void removeMarker(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			String updateMarkerKey = buildMarkerKey(translationId);

			memcachedClient.delete(updateMarkerKey);
		}
	}

	private String buildMarkerKey(UUID translationId)
	{
		return translationId.toString() + "-updating-marker";
	}
}
