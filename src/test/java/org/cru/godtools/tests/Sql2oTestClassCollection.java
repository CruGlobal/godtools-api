package org.cru.godtools.tests;

import com.google.common.collect.ImmutableSet;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.sql2o.Connection;

/**
 * Created by ryancarlson on 7/30/14.
 */
public class Sql2oTestClassCollection
{
	ImmutableSet<Class<?>> set = ImmutableSet.of(Connection.class,
			TestSqlConnectionProducer.class,
			GodToolsProperties.class,
			GodToolsPropertiesFactory.class);

	public Class[] getClasses()
	{
		return set.toArray(new Class<?>[set.size()]);
	}
}
