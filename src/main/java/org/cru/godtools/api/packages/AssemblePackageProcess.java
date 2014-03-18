package org.cru.godtools.api.packages;

import com.google.common.base.Throwables;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.packages.utils.PageFilenameList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * Created by ryancarlson on 3/17/14.
 */
public class AssemblePackageProcess
{
    final String languageCode;
    final String packageCode;

    MockPackageService packageService;

    public AssemblePackageProcess(MockPackageService packageService, String languageCode, String packageCode)
    {
        this.packageService = packageService;
        this.languageCode = languageCode;
        this.packageCode = packageCode;
    }

    public Response buildZippedResponse() throws IOException
    {
        try
        {
            Document  contentsFile = packageService.getContentsFile(languageCode, packageCode);
            Map<String, Document> packagePages = packageService.getPageFiles(languageCode, packageCode, new PageFilenameList().fromContentsFile(contentsFile));

            ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(bundledStream);
            FileZipper fileZipper = new FileZipper();

            String packageFileChecksum = fileZipper.zipPackageFile(contentsFile, zipOutputStream);

            fileZipper.zipPageFiles(packagePages, zipOutputStream);

            fileZipper.zipContentsFile(createContentsFile(packageFileChecksum), zipOutputStream);

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

    private Document createContentsFile(String packageFileChecksum) throws ParserConfigurationException
    {
        Document contents = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element rootElement = contents.createElement("content");
        contents.appendChild(rootElement);

        Element resourceElement = contents.createElement("resource");
        resourceElement.setAttribute("package", packageCode);
        resourceElement.setAttribute("language", languageCode);
        resourceElement.setAttribute("config", packageFileChecksum + ".xml");

        rootElement.appendChild(resourceElement);

        return contents;

    }
}
