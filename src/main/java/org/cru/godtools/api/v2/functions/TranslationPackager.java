package org.cru.godtools.api.v2.functions;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class TranslationPackager
{
	protected FileZipper fileZipper = new FileZipper();

	public InputStream compress(List<GodToolsTranslation> godToolsTranslationList)
	{
		try
		{
			ByteArrayOutputStream bundledOutputStream = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = new ZipOutputStream(bundledOutputStream);

			if (!godToolsTranslationList.isEmpty())
			{
				fileZipper.zipContentsFile(createContentsFile(godToolsTranslationList), zipOutputStream);

				for (GodToolsTranslation godToolsTranslation : godToolsTranslationList)
				{
					addTranslationToZipStream(godToolsTranslation, zipOutputStream);
				}
			}

			zipOutputStream.close();
			bundledOutputStream.close();

			return new ByteArrayInputStream(bundledOutputStream.toByteArray());
		}
		catch(Exception e)
		{
			throw Throwables.propagate(e);
		}
	}

	public InputStream compress(GodToolsTranslation godToolsTranslation)
	{
		try
		{
			ByteArrayOutputStream bundledOutputStream = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = new ZipOutputStream(bundledOutputStream);

			fileZipper.zipContentsFile(createContentsFile(Lists.newArrayList(godToolsTranslation)), zipOutputStream);

			addTranslationToZipStream(godToolsTranslation, zipOutputStream);

			zipOutputStream.close();
			bundledOutputStream.close();

			return new ByteArrayInputStream(bundledOutputStream.toByteArray());
		}
		catch (Exception e)
		{
			throw Throwables.propagate(e);
		}
	}

	private void addTranslationToZipStream(GodToolsTranslation godToolsTranslation, ZipOutputStream zipOutputStream) throws Exception
	{
		fileZipper.zipPackageFile(godToolsTranslation.getPackageStructure(),
				godToolsTranslation.getTranslation(),
				zipOutputStream);

		fileZipper.zipPageFiles(godToolsTranslation.getPageStructureList(), zipOutputStream);
	}

	private Document createContentsFile(List<GodToolsTranslation> godToolsTranslationList) throws ParserConfigurationException
	{
		Document contents = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		Element rootElement = contents.createElement("content");
		contents.appendChild(rootElement);

		for(GodToolsTranslation godToolsTranslation : godToolsTranslationList)
		{
			Element resourceElement = contents.createElement("resource");
			resourceElement.setAttribute("package", godToolsTranslation.getPackageCode());
			resourceElement.setAttribute("language", godToolsTranslation.getLanguage().getPath());
			resourceElement.setAttribute("config", godToolsTranslation.getTranslation().getId() + ".xml");
			resourceElement.setAttribute("status", godToolsTranslation.isDraft() ? "draft" : "live");
			resourceElement.setAttribute("name", godToolsTranslation.getPackageStructure().getPackageName());
			resourceElement.setAttribute("version", godToolsTranslation.getVersionNumber().toPlainString());

			if(godToolsTranslation.getIcon() != null)
			{
				resourceElement.setAttribute("icon", GuavaHashGenerator.calculateHash(godToolsTranslation.getIcon().getImageContent()) + ".png");
			}
			else
			{
				resourceElement.setAttribute("icon", "missing");
			}
			rootElement.appendChild(resourceElement);
		}
		return contents;
	}
}

