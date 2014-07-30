package org.cru.godtools.domain;

import org.jboss.arquillian.testng.Arquillian;
import org.sql2o.Connection;
import org.testng.annotations.AfterClass;

import java.sql.SQLException;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractServiceTest extends Arquillian
{
	//just to keep other tests compiling
	protected Connection sqlConnection;
	public void setup()
	{
		UnittestDatabaseBuilder.build();
	}
}
