package org.cru.godtools.api.cache;

import com.google.common.base.Throwables;
import net.spy.memcached.MemcachedClient;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by ryancarlson on 9/19/14.
 */
public class MemcachedClientProducer
{
	@Inject
	GodToolsProperties godToolsProperties;

	@Produces
	public MemcachedClient getClient()
	{
		// mainly for quartz where CDI doesn't work...
		if(godToolsProperties == null) godToolsProperties = new GodToolsPropertiesFactory().get();

		try
		{
			return new MemcachedClient(new InetSocketAddress(godToolsProperties.getNonNullProperty("memcachedHost"),
					Integer.parseInt(godToolsProperties.getNonNullProperty("memcachedPort"))));
		}
		catch (IOException e)
		{
			throw Throwables.propagate(e);
		}
	}
}
