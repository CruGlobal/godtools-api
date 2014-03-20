package org.cru.godtools.api.packages;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.packages.utils.GodToolsPackageFilenameUtilities;
import org.cru.godtools.api.packages.utils.GodToolsPackageShaGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    GodToolsPackageShaGenerator shaGenerator;
    GodToolsPackageFilenameUtilities filenameUtilities;
    FileZipper fileZipper;

    @Inject
    public GodToolsResponseAssemblyProcess(IGodToolsPackageService packageService,
                                           GodToolsPackageShaGenerator shaGenerator,
                                           GodToolsPackageFilenameUtilities filenameUtilities,
                                           FileZipper fileZipper)
    {
        this.packageService = packageService;
        this.shaGenerator = shaGenerator;
        this.filenameUtilities = filenameUtilities;
        this.fileZipper = fileZipper;
    }

    public Response buildZippedResponse(String languageCode, String packageCode) throws IOException
    {
        try
        {
            ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(bundledStream);
            Set<GodToolsPackage> packages;

            if(Strings.isNullOrEmpty(packageCode))
            {
                packages = packageService.getPackagesForLanguage(languageCode);
            }
            else
            {
                packages = Sets.newHashSet(packageService.getPackage(languageCode, packageCode));
            }

            shaGenerator.setHashes(packages);

            filenameUtilities.replaceFilenamesWithHash(packages);

            createZipFolder(zipOutputStream, packages);

            bundledStream.close();

            return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray())).build();
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null;
        }
    }

    private void createZipFolder(ZipOutputStream zipOutputStream, Set<GodToolsPackage> packages) throws Exception
    {
        for(GodToolsPackage godToolsPackage : packages)
        {
            fileZipper.zipPackageFile(godToolsPackage, zipOutputStream);

            fileZipper.zipPageFiles(godToolsPackage, zipOutputStream);

            fileZipper.zipImageFiles(godToolsPackage, zipOutputStream);
        }

        fileZipper.zipContentsFile(createContentsFile(packages), zipOutputStream);

        zipOutputStream.close();
    }

    private Document createContentsFile(Set<GodToolsPackage> packages) throws ParserConfigurationException
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
}