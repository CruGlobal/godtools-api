package org.cru.godtools.api.packages;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.cru.godtools.api.packages.exceptions.LanguageNotFoundException;
import org.cru.godtools.api.packages.exceptions.MissingVersionException;
import org.cru.godtools.api.packages.exceptions.NoTranslationException;
import org.cru.godtools.api.packages.exceptions.PackageNotFoundException;
import org.cru.godtools.api.packages.utils.ImageNameList;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.PageNameList;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/14/14.
 */
@Mock
public class MockGodToolsPackageService implements IGodToolsPackageService
{

    public GodToolsPackage getPackage(LanguageCode languageCode, String packageCode)
    {
        try
        {
            DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document packageFile =  documentBuilder.parse(this.getClass().getResourceAsStream("/data/packages-" + languageCode + "-" + packageCode + ".xml"));

            GodToolsPackage godToolsPackage = new GodToolsPackage(packageFile,
                    getPageFiles(languageCode.toString(), packageCode, new PageNameList().fromContentsFile(packageFile)),
                    null,
                    languageCode.toString(),
                    packageCode);

            godToolsPackage.setImageFiles(getImages(
                    languageCode,
                    packageCode,
                    new ImageNameList().fromPageFiles(godToolsPackage.getPageFiles())));

            return godToolsPackage;
        }

        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
        }
    }

    @Override
    public GodToolsPackage getPackage(LanguageCode languageCode, String packageCode, Integer revisionNumber) throws LanguageNotFoundException, PackageNotFoundException, NoTranslationException, MissingVersionException
    {
        return getPackage(languageCode, packageCode);
    }

    public Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode)
    {
        GodToolsPackage kgp = getPackage(languageCode, "kgp");
        GodToolsPackage satisfied = getPackage(languageCode, "satisfied");

        return Sets.newHashSet(kgp, satisfied);
    }

    @Override
    public Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode, Integer revisionNumber) throws LanguageNotFoundException, PackageNotFoundException, NoTranslationException, MissingVersionException
    {
        return getPackagesForLanguage(languageCode, null);
    }

    private List<GodToolsPackagePage> getPageFiles(String languageCode, String packageCode, List<String> fileNames)
    {
        List<GodToolsPackagePage> pages = Lists.newArrayList();

        try
        {
            DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();

            for(String filename : fileNames)
            {
                String path = path(languageCode,packageCode,filename);

                Document xmlPage = documentBuilder.parse(this.getClass().getResourceAsStream(path));
                pages.add(new GodToolsPackagePage(xmlPage, filename));
            }
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
        }
       return pages;
    }

    private Set<GodToolsPackageImage> getImages(LanguageCode languageCode, String packageCode, Set<String> imageNames)
    {
        Set<GodToolsPackageImage> images = Sets.newHashSet();

        for(String imageName : imageNames)
        {
            String path = imagePath(languageCode.toString(), packageCode, imageName);

            try
            {
                byte[] imageBytes = IOUtils.toByteArray(this.getClass().getResourceAsStream(path));
                images.add(new GodToolsPackageImage(imageBytes, imageName));
            }
            catch (IOException e)
            {
                Throwables.propagate(e);
            }
        }

        return images;
    }

    private String path(String languageCode, String packageCode, String filename)
    {
        return "/data/packages/" + languageCode + "/" + packageCode + "/" + filename;
    }

    private String imagePath(String languageCode, String packageCode, String filename)
    {
        return "/data/packages/" + languageCode + "/" + packageCode + "/images/" + filename;
    }

}
