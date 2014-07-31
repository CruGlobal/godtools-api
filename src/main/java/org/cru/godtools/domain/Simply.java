package org.cru.godtools.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.io.IOException;

/**
 * Created by ryancarlson on 7/21/14.
 */
public class Simply
{
	public static void logObject(Object object, Class clazz)
	{
		Logger logger = Logger.getLogger(clazz);

		if(object == null)
			return;

		try
		{
			logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
