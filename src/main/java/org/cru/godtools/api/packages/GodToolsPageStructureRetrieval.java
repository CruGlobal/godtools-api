package org.cru.godtools.api.packages;

import com.google.common.base.Throwables;
import org.ccci.util.xml.XmlDocumentStreamConverter;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.PageStructure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by matthewfrederick on 8/4/14.
 */
public class GodToolsPageStructureRetrieval
{
	@Inject
	LanguageService languageService;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	GodToolsTranslation godToolsTranslation;

	List<PageStructure> pageStructures;


	public void getPageStructures(Set<GodToolsTranslation> translations)
	{
		pageStructures = new ArrayList<>();

		if (translations == null)
		{
			System.out.println("Translations is null");
		} else
		{
			for (GodToolsTranslation translation : translations)
			{
				pageStructures.addAll(translation.getPageStructureList());
			}
		}
	}

	public Response buildXMLResponse() throws IOException
	{
		ByteArrayOutputStream outputStream = XmlDocumentStreamConverter.writeToByteArrayStream(createXmlDocument());
		outputStream.close();

		return Response.ok(new ByteArrayInputStream(outputStream.toByteArray()))
				.type(MediaType.APPLICATION_XML)
				.build();
	}

	public Document createXmlDocument()
	{
		try
		{
			Document contents = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element root = contents.createElement("Page_Structures");
			contents.appendChild(root);

			for (PageStructure pageStructure : pageStructures)
			{
				if (pageStructure != null)
				{
					Element structure = contents.createElement("Page_Structure");
					structure.setAttribute("ID", pageStructure.getId().toString());
					structure.setAttribute("Translation_ID", pageStructure.getTranslationId().toString());

					// Currently Description is empty, so it will not be included
					// structure.setAttribute("Description", pageStructure.getDescription());

					structure.setAttribute("filename", pageStructure.getFilename());
					root.appendChild(structure);
				}
			}
			return contents;

		} catch (ParserConfigurationException e)
		{
			throw Throwables.propagate(e);
		}
	}
}
