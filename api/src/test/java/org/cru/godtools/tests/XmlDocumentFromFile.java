package org.cru.godtools.tests;

import com.google.common.base.Throwables;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class XmlDocumentFromFile
{

	public static Document get(String filePath)
	{
		try
		{
			InputStream inputStream = XmlDocumentFromFile.class.getResourceAsStream(filePath);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.parse(inputStream);
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}
}
