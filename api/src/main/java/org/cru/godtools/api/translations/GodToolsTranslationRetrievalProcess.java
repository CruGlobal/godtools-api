package org.cru.godtools.api.translations;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.ccci.util.xml.XmlDocumentStreamConverter;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PixelDensity;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
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
public class GodToolsTranslationRetrievalProcess
{
    GodToolsTranslationService godToolsTranslationService;
    FileZipper fileZipper;

    String packageCode;
    LanguageCode languageCode;
    Integer minimumInterpreterVersion;
    GodToolsVersion godToolsVersion;
    boolean compressed;
	boolean includeDrafts;
    PixelDensity pixelDensity;

	Set<GodToolsTranslation> godToolsTranslations = Sets.newHashSet();

	Logger log = Logger.getLogger(GodToolsTranslationRetrievalProcess.class);

	@Inject
    public GodToolsTranslationRetrievalProcess(GodToolsTranslationService godToolsTranslationService, FileZipper fileZipper)
    {
        this.godToolsTranslationService = godToolsTranslationService;
        this.fileZipper = fileZipper;
    }

    public GodToolsTranslationRetrievalProcess setPackageCode(String packageCode)
    {
		log.info("Setting package code: " + packageCode);
		this.packageCode = packageCode;
        return this;
    }

    public GodToolsTranslationRetrievalProcess setLanguageCode(String languageCode)
    {
		log.info("Setting language code: " + languageCode);
		this.languageCode = new LanguageCode(languageCode);
        return this;
    }

    public GodToolsTranslationRetrievalProcess setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
    {
		log.info("Setting interpreter version: " + minimumInterpreterVersion);
        this.minimumInterpreterVersion = minimumInterpreterVersion;
        return this;
    }

    public GodToolsTranslationRetrievalProcess setVersionNumber(GodToolsVersion godToolsVersion)
    {
		log.info("Setting version number: " + godToolsVersion == null ? "Latest published" : godToolsVersion);
        this.godToolsVersion = godToolsVersion;
        return this;
    }

    public GodToolsTranslationRetrievalProcess setCompressed(boolean compressed)
    {
		log.info("Setting compressed: " + compressed);
        this.compressed = compressed;
        return this;
    }

	public GodToolsTranslationRetrievalProcess setIncludeDrafts(boolean includeDrafts)
	{
		log.info("Setting includeDrafts: " + includeDrafts);
		this.includeDrafts = includeDrafts;
		return this;
	}

    public GodToolsTranslationRetrievalProcess setPixelDensity(PixelDensity pixelDensity)
    {
		log.info("Setting pixelDensity: " + pixelDensity);
        this.pixelDensity = pixelDensity;
        return this;
    }

	public GodToolsTranslationRetrievalProcess loadTranslations()
	{
		log.info("Loading translations...");
		if(Strings.isNullOrEmpty(packageCode))
		{
			godToolsTranslations.addAll(godToolsTranslationService.getTranslationsForLanguage(languageCode));
		}
		else
		{
			godToolsTranslations.add(godToolsTranslationService.getTranslation(languageCode, packageCode, GodToolsVersion.LATEST_PUBLISHED_VERSION));
		}

		log.info("Loaded " + godToolsTranslations.size() + " translations");
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

    private Response buildXmlContentsResponse() throws IOException
    {
        if(godToolsTranslations.isEmpty())
        {
            throw new NotFoundException();
        }

        ByteArrayOutputStream bundledStream = XmlDocumentStreamConverter.writeToByteArrayStream(createContentsFile());
        bundledStream.close();

        return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray()))
                .type(MediaType.APPLICATION_XML)
                .build();
    }

    private Response buildZippedResponse() throws IOException
    {
        if(godToolsTranslations.isEmpty()) return Response.status(404).build();

        ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();

        createZipFolder(new ZipOutputStream(bundledStream));
        bundledStream.close();

        return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray()))
                .type("application/zip")
                .build();
    }

    private void createZipFolder(ZipOutputStream zipOutputStream)
    {
        try
        {
            for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
            {
                fileZipper.zipPackageFile(godToolsTranslation.getPackageStructure(), zipOutputStream);

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

    private Document createContentsFile()
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
                resourceElement.setAttribute("config", GuavaHashGenerator.calculateHash(godToolsTranslation.getPackageStructure().getXmlContent()) + ".xml");
				resourceElement.setAttribute("status", godToolsTranslation.isDraft ? "draft" : "live");

                rootElement.appendChild(resourceElement);
            }
            return contents;
        }
        catch(ParserConfigurationException e)
        {
            Throwables.propagate(e);
            return null;
        }
    }
}
