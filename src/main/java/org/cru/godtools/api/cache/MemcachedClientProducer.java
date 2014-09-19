package org.cru.godtools.api.cache;

import net.spy.memcached.MemcachedClient;
import org.cru.godtools.domain.properties.GodToolsProperties;

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
	public MemcachedClient getClient() throws IOException
	{
		return new MemcachedClient(new InetSocketAddress(godToolsProperties.getNonNullProperty("memcachedHost"),
				Integer.parseInt(godToolsProperties.getNonNullProperty("memcachedPort"))));
	}
}
