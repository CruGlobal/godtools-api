package org.cru.godtools.api.cache;

import com.google.common.base.Optional;
import net.spy.memcached.MemcachedClient;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.jboss.logging.Logger;

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
	Logger log = Logger.getLogger(MemcachedGodToolsCache.class);

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
			try
			{
				return Optional.fromNullable((GodToolsTranslation) memcachedClient.get(translationId.toString()));
			}
			catch(Exception e)
			{
				log.error(String.format("Error retrieving translation w/ ID: %s from cache", translationId.toString()),
						e);
			}
		}

		return Optional.absent();
	}

	@Override
	public void add(GodToolsTranslation godToolsTranslation)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			try
			{
				memcachedClient.add(godToolsTranslation.getTranslation().getId().toString(),
						CACHE_EXPIRATION_SECONDS,
						godToolsTranslation);
			}
			catch (Exception e)
			{
				log.error(String.format("Error adding translation w/ ID: %s to cache", godToolsTranslation.getTranslation().getId().toString()),
						e);
			}
		}
	}

	@Override
	public Optional<GodToolsTranslation> remove(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			try
			{
				Optional<GodToolsTranslation> optionalTranslation = Optional.fromNullable((GodToolsTranslation) memcachedClient.get(translationId.toString()));
				memcachedClient.delete(translationId.toString());

				return optionalTranslation;
			}
			catch(Exception e)
			{
				log.error(String.format("Error removing translation w/ ID: %s from cache", translationId.toString()),
						e);
			}
		}

		return Optional.absent();
	}

	@Override
	public void replace(GodToolsTranslation godToolsTranslation)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			try
			{
				memcachedClient.replace(godToolsTranslation.getTranslation().getId().toString(),
						CACHE_EXPIRATION_SECONDS,
						godToolsTranslation);
			}
			catch(Exception e)
			{
				log.error(String.format("Error replacing translation w/ ID: %s in cache", godToolsTranslation.getTranslation().getId().toString()),
						e);
			}
		}
	}

	@Override
	public Optional<Boolean> getMarker(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			try
			{
				String marker = (String) memcachedClient.get(buildMarkerKey(translationId));
				return Optional.of(marker != null);
			}
			catch(Exception e)
			{
				log.error(String.format("Error getting translation build marker for translation w/ ID: %s in cache", translationId.toString()),
						e);
			}
		}

		return Optional.absent();
	}

	@Override
	public void recordMarker(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			try
			{
				memcachedClient.add(buildMarkerKey(translationId), 30, new String("marker"));
			}
			catch(Exception e)
			{
				log.error(String.format("Error recording translation build marker for translation w/ ID: %s in cache", translationId.toString()),
						e);
			}
		}
	}

	@Override
	public void removeMarker(UUID translationId)
	{
		if(Boolean.parseBoolean(properties.getProperty("memcachedEnabled", "false")))
		{
			try
			{
				memcachedClient.delete(buildMarkerKey(translationId));
			}
			catch(Exception e)
			{
				log.error(String.format("Error removing translation build marker for translation w/ ID: %s from cache", translationId.toString()),
						e);
			}
		}
	}

	private String buildMarkerKey(UUID translationId)
	{
		return translationId.toString() + "-updating-marker";
	}
}
