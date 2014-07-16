package org.cru.godtools.api.packages;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

import org.ccci.util.xml.XmlDocumentStreamConverter;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PixelDensity;
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
import java.util.PriorityQueue;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * Stateful object with logic to take a single or multiple GodTools package(s) and perform necessary steps to return a javax.ws.rs.core.Response with the package data.
 *
 * If compressed = true, the content is zipped into a zip file by using a ZipOutputStream.
 *
 * Created by ryancarlson on 3/17/14.
 */
public class GodToolsPackageRetrievalProcess
{
    GodToolsPackageService packageService;
    FileZipper fileZipper;

    String packageCode;
    LanguageCode languageCode;
    Integer minimumInterpreterVersion;
    GodToolsVersion godToolsVersion;
    boolean compressed;

    PixelDensity pixelDensity;

	Set<GodToolsPackage> godToolsPackages = Sets.newHashSet();

    @Inject
    public GodToolsPackageRetrievalProcess(GodToolsPackageService packageService, FileZipper fileZipper)
    {
        this.packageService = packageService;
        this.fileZipper = fileZipper;
    }

    public GodToolsPackageRetrievalProcess setPackageCode(String packageCode)
    {
        this.packageCode = packageCode;
        return this;
    }

    public GodToolsPackageRetrievalProcess setLanguageCode(String languageCode)
    {
        this.languageCode = new LanguageCode(languageCode);
        return this;
    }

    public GodToolsPackageRetrievalProcess setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
    {
        this.minimumInterpreterVersion = minimumInterpreterVersion;
        return this;
    }

    public GodToolsPackageRetrievalProcess setVersionNumber(GodToolsVersion godToolsVersion)
    {
        this.godToolsVersion = godToolsVersion;
        return this;
    }

    public GodToolsPackageRetrievalProcess setCompressed(boolean compressed)
    {
        this.compressed = compressed;
        return this;
    }

    public GodToolsPackageRetrievalProcess setPixelDensity(PixelDensity pixelDensity)
    {
        this.pixelDensity = pixelDensity;
        return this;
    }

	public GodToolsPackageRetrievalProcess loadPackages()
	{
		if(Strings.isNullOrEmpty(packageCode))
		{
			godToolsPackages.addAll(packageService.getPackagesForLanguage(languageCode, minimumInterpreterVersion, false, pixelDensity));
		}
		else
		{
			godToolsPackages.add(packageService.getPackage(languageCode, packageCode, godToolsVersion, minimumInterpreterVersion, false, pixelDensity));
		}

		return this;
	}

    public Response buildResponse() throws IOException
    {
        if(compressed)
        {
            return buildZippedResponse();
        }
        else
        {
            return buildNonZippedResponse();
        }
    }

    private Response buildNonZippedResponse() throws IOException
    {
        if(this.godToolsPackages.isEmpty())
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
        if(godToolsPackages.isEmpty()) return Response.status(404).build();

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

            for(GodToolsPackage godToolsPackage : godToolsPackages)
            {
                fileZipper.zipPackageFile(godToolsPackage.getPackageStructure(), zipOutputStream);

                fileZipper.zipPageFiles(godToolsPackage.getPageStructureList(), zipOutputStream);

				fileZipper.zipImageFiles(godToolsPackage.getImages(), zipOutputStream, imagesAlreadyZipped);

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

            for(GodToolsPackage godToolsPackage : godToolsPackages)
            {
                Element resourceElement = contents.createElement("resource");
                resourceElement.setAttribute("package", godToolsPackage.getPackageCode());
                resourceElement.setAttribute("language", languageCode.toString());
                resourceElement.setAttribute("config", GuavaHashGenerator.calculateHash(godToolsPackage.getPackageStructure().getXmlContent()) + ".xml");

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