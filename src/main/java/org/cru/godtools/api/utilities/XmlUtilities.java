package org.cru.godtools.api.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.ws.rs.BadRequestException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;

/**
 * Created by laelwatkins on 4/28/16.
 */
public class XmlUtilities
{
    public static String xmlDocumentOrNodeToString(DOMSource source) throws IOException,TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
        StreamResult result = new StreamResult( new StringWriter());
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public static void verifyDifferentXml(ByteArrayOutputStream firstByteArrayOutputStream,
                                    ByteArrayOutputStream secondByteArrayOutputStream)
    {
        String firstArrayToString = new String (firstByteArrayOutputStream.toByteArray());
        String secondArrayToString = new String (secondByteArrayOutputStream.toByteArray());

        firstArrayToString = firstArrayToString.replaceAll("(\\r|\\n|\\s)", "");
        secondArrayToString = secondArrayToString.replaceAll("(\\r|\\n|\\s)", "");

        if(firstArrayToString.equals(secondArrayToString))
        {
            throw new BadRequestException("The document submitted is the same as the current one.");
        }
    }
}
