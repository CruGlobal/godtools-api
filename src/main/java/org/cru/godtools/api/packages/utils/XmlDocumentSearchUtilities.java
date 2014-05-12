package org.cru.godtools.api.packages.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by ryancarlson on 3/19/14.
 */
public class XmlDocumentSearchUtilities
{
	/**
	 * Retrieves a list of all attribute values it finds within a specific element.  Or more simply, this method will
	 * scan the document for all elements named "elementName".  If those elements have an attribute named "attributeName", it will
	 * return a list of all of those attribute values.
	 *
	 * @param document
	 * @param elementName
	 * @param attributeName
	 * @return
	 */
	public static List<Element> findElementsWithAttribute(Document document, String elementName, String attributeName)
	{
		List<Element> list = Lists.newArrayList();

		NodeList pageNodes = document.getElementsByTagName(elementName);

		for(int i = 0; i < pageNodes.getLength(); i++)
		{
			Node node = pageNodes.item(i);

			if(node instanceof Element)
			{
				Element page = (Element) node;
				if(Strings.isNullOrEmpty(page.getAttribute(attributeName))) continue;

				list.add(page);
			}
		}

		return list;
	}

	public static List<Element> findElementsWithAttribute(Document document, String attribute)
	{
		List<Element> list = Lists.newArrayList();

		recursiveDocumentSearch(list, document.getDocumentElement(), attribute);

		return list;
	}

	private static void recursiveDocumentSearch(List<Element> list, Node baseNode, String attribute)
	{
		NodeList children = baseNode.getChildNodes();

		for(int i = 0; i < children.getLength(); i++)
		{
			Node node = children.item(i);
			if(node instanceof Element)
			{
				Element currentElement = (Element) node;
				if(!Strings.isNullOrEmpty(currentElement.getAttribute(attribute)))
				{
					list.add((Element)children.item(i));
				}
			}
			recursiveDocumentSearch(list, node, attribute);
		}
	}

	public static List<Element> findElements(Document document, String elementName)
	{
		List<Element> list = Lists.newArrayList();

		NodeList nodes = document.getElementsByTagName(elementName);

		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);

			if(node instanceof Element)
			{
				list.add((Element) node);
			}
		}

		return list;
	}
}
