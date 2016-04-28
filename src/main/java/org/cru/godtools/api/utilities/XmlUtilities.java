package org.cru.godtools.api.utilities;

import java.io.IOException;
import java.io.StringWriter;
import javax.ws.rs.BadRequestException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

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

    public static void verifyDifferentXml(Document firstXmlDocument,
                                          Document secondXmlDocument) throws IOException,TransformerException
    {
        String firstArrayToString = xmlDocumentOrNodeToString(new DOMSource(firstXmlDocument));
        String secondArrayToString = xmlDocumentOrNodeToString(new DOMSource(secondXmlDocument));

        //remove spaces and carriage returns
        firstArrayToString = firstArrayToString.replaceAll("(\\r|\\n|\\s)", "");
        secondArrayToString = secondArrayToString.replaceAll("(\\r|\\n|\\s)", "");

        if(firstArrayToString.equals(secondArrayToString))
        {
            throw new BadRequestException("The document submitted is the same as the current one.");
        }
    }

    public static boolean hasSameAttributes(Element oElement, Element aElement)
    {
        NamedNodeMap originalNamedNodeMap = oElement.getAttributes();
        NamedNodeMap additionNamedNodeMap = aElement.getAttributes();

        boolean attrMatch = true;

        for (int n = 0; n < additionNamedNodeMap.getLength(); n++)
        {
            Attr a1 = (Attr) additionNamedNodeMap.item(n);
            Attr o1 = (Attr) originalNamedNodeMap.item(n);

            if (!o1.getName().equals(a1.getName()) || !o1.getValue().equals(a1.getValue()))
            {
                attrMatch = false;
                break;
            }
        }
        return attrMatch;
    }
}
