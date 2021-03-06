package org.cru.godtools.api.translations;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.translations.model.ContentsFile;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PixelDensity;

import org.jboss.logging.Logger;
import org.quartz.SchedulerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * Stateful object with logic to take a single or multiple GodTools translations(s) and perform necessary steps to return a javax.ws.rs.core.Response with the translation data.
 *
 * If compressed = true, the content is zipped into a zip file by using a ZipOutputStream.
 *
 * Created by ryancarlson on 3/17/14.
 */
@Default
@Deprecated
public class GodToolsTranslationRetrieval
{
	@Inject
	protected GodToolsTranslationService godToolsTranslationService;
	@Inject
	protected FileZipper fileZipper;

	protected String packageCode;
	protected LanguageCode languageCode;
	protected Integer minimumInterpreterVersion;
	protected GodToolsVersion godToolsVersion;
	protected boolean compressed;
	protected PixelDensity pixelDensity;

	protected Set<GodToolsTranslation> godToolsTranslations = Sets.newHashSet();

	Logger log = Logger.getLogger(GodToolsTranslationRetrieval.class);

	public GodToolsTranslationRetrieval setPackageCode(String packageCode)
	{
		log.info("Setting package code: " + packageCode);
		this.packageCode = packageCode;
		return this;
	}

	public GodToolsTranslationRetrieval setLanguageCode(String languageCode)
	{
		log.info("Setting language code: " + languageCode);
		this.languageCode = new LanguageCode(languageCode);
		return this;
	}

	public GodToolsTranslationRetrieval setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
	{
		log.info("Setting interpreter version: " + minimumInterpreterVersion);
		this.minimumInterpreterVersion = minimumInterpreterVersion;
		return this;
	}

	public GodToolsTranslationRetrieval setVersionNumber(GodToolsVersion godToolsVersion)
	{
		log.info("Setting version number: " + godToolsVersion == null ? "Latest published" : godToolsVersion);
		this.godToolsVersion = godToolsVersion;
		return this;
	}

	public GodToolsTranslationRetrieval setCompressed(boolean compressed)
	{
		log.info("Setting compressed: " + compressed);
		this.compressed = compressed;
		return this;
	}

	public GodToolsTranslationRetrieval setPixelDensity(PixelDensity pixelDensity)
	{
		log.info("Setting pixelDensity: " + pixelDensity);
		this.pixelDensity = pixelDensity;
		return this;
	}

	public GodToolsTranslationRetrieval loadTranslations()
	{
		log.info("Loading translations...");
		if(Strings.isNullOrEmpty(packageCode))
		{
			godToolsTranslations.addAll(godToolsTranslationService.getTranslationsForLanguage(languageCode, GodToolsVersion.LATEST_PUBLISHED_VERSION));
		}
		else
		{
			godToolsTranslations.add(godToolsTranslationService.getTranslation(languageCode, packageCode, GodToolsVersion.LATEST_PUBLISHED_VERSION));
		}

		log.info("Loaded " + godToolsTranslations.size() + " translations");
		return this;
	}

	public GodToolsTranslationRetrieval loadDrafts()
	{
		log.info("Loading drafts...");

		if(Strings.isNullOrEmpty(packageCode))
		{
			godToolsTranslations.addAll(godToolsTranslationService.getTranslationsForLanguage(languageCode, GodToolsVersion.DRAFT_VERSION));
		}
		else
		{
			godToolsTranslations.add(godToolsTranslationService.getTranslation(languageCode, packageCode, GodToolsVersion.DRAFT_VERSION));
		}

		log.info("Loaded " + godToolsTranslations.size() + " draft(s)");
		return this;
	}

	public Response buildResponse() throws IOException
	{
		if(compressed)
		{
			log.info("Building zipped response");
			return buildZippedResponse();
		}
		else
		{
			log.info("Building unzipped response");
			return buildXmlContentsResponse();
		}
	}

	public Response buildSinglePageResponse(PageStructure pageStructure) throws ParserConfigurationException
	{
		if(compressed)
		{
			ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = new ZipOutputStream(bundledStream);

			try
			{
				fileZipper.zipPageFiles(Lists.newArrayList(pageStructure), zipOutputStream);

				fileZipper.zipContentsFile(createContentsFile(), zipOutputStream);

				zipOutputStream.close();
				bundledStream.close();
			} catch (Exception e)
			{
				throw Throwables.propagate(e);
			}

			return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray()))
					.type("application/zip")
					.build();
		}
		else
		{
			return Response.ok(pageStructure.getStrippedDownCopyOfXmlContent()).build();
		}
	}

	protected Response buildXmlContentsResponse() throws IOException
	{
		if(godToolsTranslations.isEmpty())
		{
			throw new NotFoundException();
		}

		return Response
				.ok(ContentsFile.createContentsFile(godToolsTranslations, languageCode.toString()))
				.build();
	}

	protected Response buildZippedResponse() throws IOException
	{
		if(godToolsTranslations.isEmpty()) return Response.status(404).build();

		ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();

		createZipFolder(new ZipOutputStream(bundledStream));
		bundledStream.close();

		return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray()))
				.type("application/zip")
				.build();
	}

	protected void createZipFolder(ZipOutputStream zipOutputStream)
	{
		try
		{
			for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
			{
				fileZipper.zipPackageFile(godToolsTranslation.getPackageStructure(),
						godToolsTranslation.getTranslation(),
						zipOutputStream);

				fileZipper.zipPageFiles(godToolsTranslation.getPageStructureList(), zipOutputStream);
			}

			fileZipper.zipContentsFile(createContentsFile(), zipOutputStream);

			zipOutputStream.close();
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
		}
	}

	protected Document createContentsFile()
	{
		try
		{
			Document contents = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element rootElement = contents.createElement("content");
			contents.appendChild(rootElement);

			for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
			{
				Element resourceElement = contents.createElement("resource");
				resourceElement.setAttribute("package", godToolsTranslation.getPackageCode());
				resourceElement.setAttribute("language", languageCode.toString());
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
		catch(ParserConfigurationException e)
		{
			throw Throwables.propagate(e);
		}
	}
}
