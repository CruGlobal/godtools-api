package org.cru.godtools.api.packages;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.apache.http.impl.auth.NTLMEngineException;
import org.cru.godtools.api.packages.exceptions.LanguageNotFoundException;
import org.cru.godtools.api.packages.exceptions.MissingVersionException;
import org.cru.godtools.api.packages.exceptions.NoTranslationException;
import org.cru.godtools.api.packages.exceptions.PackageNotFoundException;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.packages.utils.GodToolsPackageFilenameUtilities;
import org.cru.godtools.api.packages.utils.XmlDocumentStreamConverter;
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
import java.util.PriorityQueue;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * Logic to take a single or multiple GodTools package(s) and convert it/them into a javax.ws.rs.core.Response.
 *
 * The content is zipped into a zip file by using a ZipOutputStream
 *
 * Created by ryancarlson on 3/17/14.
 */
public class GodToolsResponseAssemblyProcess
{
    IGodToolsPackageService packageService;
    GodToolsPackageFilenameUtilities filenameUtilities;
    FileZipper fileZipper;

    Integer minimumInterpreterVersion;
    Integer revisionNumber;
    boolean compressed;

    @Inject
    public GodToolsResponseAssemblyProcess(IGodToolsPackageService packageService,
                                           GodToolsPackageFilenameUtilities filenameUtilities,
                                           FileZipper fileZipper)
    {
        this.packageService = packageService;
        this.filenameUtilities = filenameUtilities;
        this.fileZipper = fileZipper;
    }

    public GodToolsResponseAssemblyProcess forMinimumInterpreterVersion(Integer minimumInterpreterVersion)
    {
        this.minimumInterpreterVersion = minimumInterpreterVersion;
        return this;
    }

    public GodToolsResponseAssemblyProcess atRevisionNumber(Integer revisionNumber)
    {
        this.revisionNumber = revisionNumber;
        return this;
    }

    public GodToolsResponseAssemblyProcess compressed(boolean compressed)
    {
        this.compressed = compressed;
        return this;
    }

    public Response buildResponse(String languageCode, String packageCode) throws IOException
    {
        try
        {
            if(compressed)
            {
                return buildZippedResponse(loadPackages(languageCode, packageCode));
            }
            else
            {
                return buildXmlContentsResponse(loadPackages(languageCode, packageCode));
            }
        }
        catch(PackageNotFoundException | LanguageNotFoundException | NoTranslationException | MissingVersionException e)
        {
            return Response.status(404).build();
        }
    }

    private Response buildXmlContentsResponse(Set<GodToolsPackage> packages) throws IOException
    {
        if(packages.isEmpty())
        {
            return Response.status(404).build();
        }

        ByteArrayOutputStream bundledStream = XmlDocumentStreamConverter.convert(createContentsFile(packages));
        bundledStream.close();

        return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray()))
                .type(MediaType.APPLICATION_XML)
                .build();
    }

    private Response buildZippedResponse(Set<GodToolsPackage> packages) throws IOException
    {
        if(packages.isEmpty()) return Response.status(404).build();

        ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();

        createZipFolder(new ZipOutputStream(bundledStream), packages);
        bundledStream.close();

        return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray()))
                .type("application/zip")
                .build();
    }

    private Set<GodToolsPackage> loadPackages(String languageCode, String packageCode) throws LanguageNotFoundException, PackageNotFoundException, NoTranslationException, MissingVersionException
    {
        Set<GodToolsPackage> packages;
        if(Strings.isNullOrEmpty(packageCode))
        {
            packages = packageService.getPackagesForLanguage(languageCode, revisionNumber);
        }
        else
        {
            packages = Sets.newHashSet(packageService.getPackage(languageCode, packageCode, revisionNumber));
        }
        return packages;
    }

    private void createZipFolder(ZipOutputStream zipOutputStream, Set<GodToolsPackage> packages)
    {
        try
        {
            PriorityQueue<String> imagesAlreadyZipped = new PriorityQueue<String>();

            for(GodToolsPackage godToolsPackage : packages)
            {
                fileZipper.zipPackageFile(godToolsPackage, zipOutputStream);

                fileZipper.zipPageFiles(godToolsPackage, zipOutputStream);

                fileZipper.zipImageFiles(godToolsPackage, zipOutputStream, imagesAlreadyZipped);
            }

            fileZipper.zipContentsFile(createContentsFile(packages), zipOutputStream);

            zipOutputStream.close();
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
        }
    }

    private Document createContentsFile(Set<GodToolsPackage> packages)
    {
        try
        {
            Document contents = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element rootElement = contents.createElement("content");
            contents.appendChild(rootElement);

            for(GodToolsPackage godToolsPackage : packages)
            {
                Element resourceElement = contents.createElement("resource");
                resourceElement.setAttribute("package", godToolsPackage.getPackageCode());
                resourceElement.setAttribute("language", godToolsPackage.getLanguageCode());
                resourceElement.setAttribute("config", godToolsPackage.getPackageXmlHash() + ".xml");

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
