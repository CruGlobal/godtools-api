package org.cru.godtools.api.packages;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * Created by ryancarlson on 3/17/14.
 */
public class AssemblePackageProcess
{
    MockPackageService packageService;

    public AssemblePackageProcess(MockPackageService packageService)
    {
        this.packageService = packageService;
    }

    public Response buildZippedResponse(String languageCode, String packageCode) throws IOException
    {
        try
        {
            ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(bundledStream);
            FileZipper fileZipper = new FileZipper();
            Set<GodToolsPackage> packages;

            if(Strings.isNullOrEmpty(packageCode))
            {
                packages = packageService.getPackagesForLanguage(languageCode);
            }
            else
            {
                packages = Sets.newHashSet(packageService.getPackage(languageCode, packageCode));
            }

            for(GodToolsPackage godToolsPackage : packages)
            {
                godToolsPackage.setPackageFileChecksum(fileZipper.zipPackageFile(godToolsPackage.getPackageFile(), zipOutputStream));

                fileZipper.zipPageFiles(godToolsPackage.getPageFiles(), zipOutputStream);
            }

            fileZipper.zipContentsFile(createContentsFile(packages), zipOutputStream);

            zipOutputStream.close();

            bundledStream.close();

            return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray())).build();
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null;
        }
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
            resourceElement.setAttribute("config", godToolsPackage.getPackageFileChecksum() + ".xml");

            rootElement.appendChild(resourceElement);
        }
        return contents;

    }
}
