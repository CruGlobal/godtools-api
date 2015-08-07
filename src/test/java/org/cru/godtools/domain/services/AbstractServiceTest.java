package org.cru.godtools.domain.services;

import org.jboss.arquillian.junit.*;
import org.junit.runner.*;
import org.sql2o.Connection;

/**
 * Created by ryancarlson on 4/2/14.
 */
@RunWith(Arquillian.class)
public class AbstractServiceTest
{
	//just to keep other tests compiling
	protected Connection sqlConnection;


}
