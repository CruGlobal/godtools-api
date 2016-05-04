package org.cru.godtools.api.utilities;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.collect.Lists;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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

	public static Node getPreviousSiblingElement(Node node)
	{
		while(node != null)
		{
			node = node.getPreviousSibling();
			if(node instanceof Element)
			{
				return node;
			}
		}

		return node;
	}

	public static Node getNextSiblingElement(Node node)
	{
		while(node != null)
		{
			node = node.getNextSibling();
			if(node instanceof Element)
			{
				return node;
			}
		}

		return node;
	}

	public static Node getFirstChild(Node node)
	{
		Node child = node.getFirstChild();

		while(child != null && !(child instanceof Element))
		{
			child = child.getNextSibling();
		}

		return child;
	}

	public static boolean hasChildNodes(Node node)
	{
		for(int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			if(node.getChildNodes().item(i) instanceof Element)
			{
				return true;
			}
		}

		return false;
	}

	public static List<Node> getChildNodes(Node node)
	{
		List<Node> nodeList = Lists.newArrayList();

		for(int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			nodeList.add(node.getChildNodes().item(i));
		}

		return nodeList;
	}

	public static boolean nodesMatch(Node baseNode, Node updatedNode)
	{
		if(!(baseNode instanceof Element) || !(updatedNode instanceof Element)) return true;
		if(!baseNode.getNodeName().equals(updatedNode.getNodeName())) return false;

		NamedNodeMap originalNamedNodeMap = baseNode.getAttributes();
		NamedNodeMap additionNamedNodeMap = updatedNode.getAttributes();

		if(originalNamedNodeMap.getLength() != additionNamedNodeMap.getLength()) return false;

		for (int n = 0; n < additionNamedNodeMap.getLength(); n++)
		{
			Attr a1 = (Attr) additionNamedNodeMap.item(n);
			Attr o1 = (Attr) originalNamedNodeMap.item(n);

			if(a1 == null ^ o1 ==null) return false;

			if (!o1.getName().equals(a1.getName()) || !o1.getValue().equals(a1.getValue()))
			{
				return false;
			}
		}

		return true;
	}
}
