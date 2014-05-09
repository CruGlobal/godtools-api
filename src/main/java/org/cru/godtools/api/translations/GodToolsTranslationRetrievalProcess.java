package org.cru.godtools.api.translations;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.domain.PixelDensity;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.packages.utils.GodToolsVersion;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentStreamConverter;
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
import java.math.BigDecimal;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * Logic to take a single or multiple GodTools package(s) and xmlToStream it/them into a javax.ws.rs.core.Response.
 *
 * The content is zipped into a zip file by using a ZipOutputStream
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

    @Inject
    public GodToolsTranslationRetrievalProcess(GodToolsTranslationService godToolsTranslationService, FileZipper fileZipper)
    {
        this.godToolsTranslationService = godToolsTranslationService;
        this.fileZipper = fileZipper;
    }

    public GodToolsTranslationRetrievalProcess setPackageCode(String packageCode)
    {
        this.packageCode = packageCode;
        return this;
    }

    public GodToolsTranslationRetrievalProcess setLanguageCode(String languageCode)
    {
        this.languageCode = new LanguageCode(languageCode);
        return this;
    }

    public GodToolsTranslationRetrievalProcess setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
    {
        this.minimumInterpreterVersion = minimumInterpreterVersion;
        return this;
    }

    public GodToolsTranslationRetrievalProcess setVersionNumber(GodToolsVersion godToolsVersion)
    {
        this.godToolsVersion = godToolsVersion;
        return this;
    }

    public GodToolsTranslationRetrievalProcess setCompressed(boolean compressed)
    {
        this.compressed = compressed;
        return this;
    }

	public GodToolsTranslationRetrievalProcess setIncludeDrafts(boolean includeDrafts)
	{
		this.includeDrafts = includeDrafts;
		return this;
	}

    public GodToolsTranslationRetrievalProcess setPixelDensity(PixelDensity pixelDensity)
    {
        this.pixelDensity = pixelDensity;
        return this;
    }

	public GodToolsTranslationRetrievalProcess loadTranslations()
	{
		if(Strings.isNullOrEmpty(packageCode))
		{
			godToolsTranslations.addAll(godToolsTranslationService.getTranslationsForLanguage(languageCode, minimumInterpreterVersion));
		}
		else
		{
			godToolsTranslations.add(godToolsTranslationService.getTranslation(languageCode, packageCode, godToolsVersion, minimumInterpreterVersion));
		}

		return this;
	}

//	public GodToolsTranslationRetrievalProcess loadPackages()
//	{
//		if(Strings.isNullOrEmpty(packageCode))
//		{
//			godToolsPackages.addAll(godToolsTranslationService.getPackagesForLanguage(languageCode, minimumInterpreterVersion, pixelDensity));
//		}
//		else
//		{
//			godToolsPackages.add(godToolsTranslationService.getPackage(languageCode, packageCode, godToolsVersion, minimumInterpreterVersion, pixelDensity));
//		}
//
//		return this;
//	}

    public Response buildResponse() throws IOException
    {
        if(compressed)
        {
            return buildZippedResponse();
        }
        else
        {
            return buildXmlContentsResponse();
        }
    }

    private Response buildXmlContentsResponse() throws IOException
    {
        if(this.godToolsTranslations.isEmpty())
        {
            throw new NotFoundException();
        }

        ByteArrayOutputStream bundledStream = XmlDocumentStreamConverter.xmlToStream(createContentsFile());
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
            PriorityQueue<String> imagesAlreadyZipped = new PriorityQueue<String>();

            for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
            {
                fileZipper.zipPackageFile(godToolsTranslation, zipOutputStream);

                fileZipper.zipPageFiles(godToolsTranslation, zipOutputStream);

				if(godToolsTranslation instanceof GodToolsPackage)
				{
					fileZipper.zipImageFiles((GodToolsPackage) godToolsTranslation, zipOutputStream, imagesAlreadyZipped);
				}
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

            for(GodToolsTranslation godToolsPackage : godToolsTranslations)
            {
                Element resourceElement = contents.createElement("resource");
                resourceElement.setAttribute("package", packageCode);
                resourceElement.setAttribute("language", languageCode.toString());
                resourceElement.setAttribute("config", ShaGenerator.calculateHash(godToolsPackage.getPackageStructure().getXmlContent()) + ".xml");

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
