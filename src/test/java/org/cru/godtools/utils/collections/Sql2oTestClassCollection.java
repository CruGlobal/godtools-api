package org.cru.godtools.utils.collections;

import com.google.common.collect.ImmutableSet;
import org.cru.godtools.utils.TestSqlConnectionProducer;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.sql2o.Connection;

/**
 * Created by ryancarlson on 7/30/14.
 */
public class Sql2oTestClassCollection
{
	static ImmutableSet<Class<?>> set = ImmutableSet.of(Connection.class,
			TestSqlConnectionProducer.class,
			GodToolsProperties.class,
			GodToolsPropertiesFactory.class);

	public static Class[] getClasses()
	{
		return set.toArray(new Class<?>[set.size()]);
	}
}
