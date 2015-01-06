package org.cru.godtools.api.cache;


import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Properties;

/**
 * Created by ryancarlson on 12/9/14.
 */
@Singleton
@Startup
public class MemcachedLoggingConfiguration
{

	@PostConstruct
	private void chooseLoggingImplementation()
	{
		// Tell spy to use the SunLogger
		Properties systemProperties = System.getProperties();
		systemProperties.put("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
		System.setProperties(systemProperties);
	}
}
