package org.cru.godtools.api.packages.utils;

import com.google.common.base.Throwables;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by ryancarlson on 3/25/14.
 */
public class XmlDocumentStreamConverter
{
    public static ByteArrayOutputStream xmlToStream(Document xmlFile)
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try
        {
            Source source = new DOMSource(xmlFile);
            Result result = new StreamResult(byteStream);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.transform(source, result);
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
        }
        return byteStream;
    }

	public static Document streamToXml(InputStream inputStream)
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			return builder.parse(inputStream);
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null; /*unreachable*/
		}
	}
}
