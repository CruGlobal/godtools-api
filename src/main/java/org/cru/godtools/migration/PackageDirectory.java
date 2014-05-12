package org.cru.godtools.migration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.PackageStructureService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.packages.domain.PageStructureService;
import org.cru.godtools.api.packages.domain.TranslationElementService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Encapsulates logic for a package directory. (e.g: "Packages/kgp")
 *
 *  - build a Package
 *  - build a list of Languages a Package is translated into
 *  - build a list of Icons represented by a Package
 */
public class PackageDirectory
{
    private String packageCode;

	private PackageService packageService;
	private LanguageService languageService;
	private TranslationService translationService;
	private TranslationElementService translationElementService;
	private PackageStructureService packageStructureService;
	private PageStructureService pageStructureService;

    public PackageDirectory(String packageCode)
    {
        this.packageCode = packageCode;
    }

	public PackageDirectory(String packageCode, PackageService packageService, LanguageService languageService, TranslationService translationService, TranslationElementService translationElementService, PackageStructureService packageStructureService, PageStructureService pageStructureService)
	{
		this.packageCode = packageCode;
		this.packageService = packageService;
		this.languageService = languageService;
		this.translationService = translationService;
		this.translationElementService = translationElementService;
		this.packageStructureService = packageStructureService;
		this.pageStructureService = pageStructureService;
	}

	public Package buildPackage() throws Exception
    {
        File directory = getDirectory();

        for(File nextFile : directory.listFiles())
        {
            if(nextFile.isFile() && nextFile.getName().equalsIgnoreCase("en.xml"))
            {
                Package gtPackage = new Package();

                gtPackage.setId(UUID.randomUUID());
                gtPackage.setName(getPackageName(getPackageDescriptorXml(nextFile)));
                gtPackage.setCode(packageCode);

                return gtPackage;
            }
        }

        throw new RuntimeException("unable to build package for packageCode: " + packageCode);
    }

    /**
     * Returns a list of all the languages represented in this package directory.
     *
     * @return
     * @throws Exception
     */
    public List<Language> buildLanguages() throws Exception
    {
        List<Language> languages = Lists.newArrayList();
        File directory = getDirectory();

        for(File nextFile : directory.listFiles())
        {
            if(nextFile.isFile() && nextFile.getName().endsWith(".xml"))
            {
                PackageDescriptorFile packageDescriptorFile = new PackageDescriptorFile(nextFile);

                Language language = new Language();

                language.setId(UUID.randomUUID());
                language.setCode(packageDescriptorFile.getLanguageCode());
                language.setLocale(packageDescriptorFile.getLocaleCode());
                language.setSubculture(packageDescriptorFile.getSubculture());

                languages.add(language);
            }
        }

        return languages;
    }

    public Document getPackageDescriptorXml(Language language) throws IOException, SAXException, ParserConfigurationException
    {
        String path = "/data/SnuffyPackages/" + packageCode + "/";
        path += language.getCode();
        if(!Strings.isNullOrEmpty(language.getLocale())) path = path + "_" + language.getLocale();
        if(!Strings.isNullOrEmpty(language.getSubculture())) path = path + "_" + language.getSubculture();
        path += ".xml";

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(this.getClass().getResourceAsStream(path));
    }

	public ImageDirectory getSharedImageDirectory() throws URISyntaxException, FileNotFoundException
	{
		File thisDirectory = getDirectory();

		for(File possibleSharedDirectory : thisDirectory.listFiles())
		{
			if(possibleSharedDirectory.isDirectory() && possibleSharedDirectory.getName().equals("shared"))
			{
				return new ImageDirectory(possibleSharedDirectory);
			}
		}

		throw new FileNotFoundException("no shared images directory found");
	}

	public ImageDirectory getIconDirectory() throws URISyntaxException, FileNotFoundException
	{
		File thisDirectory = getDirectory();

		for(File possibleSharedDirectory : thisDirectory.listFiles())
		{
			if(possibleSharedDirectory.isDirectory() && possibleSharedDirectory.getName().equals("icons"))
			{
				return new ImageDirectory(possibleSharedDirectory);
			}
		}

		throw new FileNotFoundException("no shared images directory found");
	}

	public void savePackageStructures() throws Exception
	{
		Package gtPackage = packageService.selectByCode(packageCode);

		PackageStructure packageStructure = new PackageStructure();

		packageStructure.setId(UUID.randomUUID());
		packageStructure.setPackageId(gtPackage.getId());
		packageStructure.setXmlContent(getPackageDescriptorXml(languageService.selectByLanguageCode(new LanguageCode("en"))));
		packageStructure.setVersionNumber(1);

		Map<UUID, Document> packageStructures = Maps.newHashMap();

		for(Translation translation : translationService.selectByPackageId(gtPackage.getId()))
		{
			packageStructures.put(translation.getId(), getPackageDescriptorXml(languageService.selectLanguageById(translation.getLanguageId())));
		}

		TranslatableElements translatableElements = new TranslatableElements(packageStructure.getXmlContent(), gtPackage.getName(), packageStructures);

		translatableElements.save(translationElementService);

		packageStructureService.insert(packageStructure);
	}

	public void savePageStructures()
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());

		Map<UUID, Iterator<Page>> pageDirectoryMap = Maps.newHashMap();
		PageDirectory baseEnglishPageDirectory = new PageDirectory(packageCode, "en");

		for(Translation translation : translationService.selectByPackageId(gtPackage.getId()))
		{
			pageDirectoryMap.put(translation.getId(), new PageDirectory(packageCode, languageService.selectLanguageById(translation.getLanguageId()).getPath()).buildPages().iterator());
		}

		for(Page baseEnglishPage : baseEnglishPageDirectory.buildPages())
		{
			PageStructure pageStructure = new PageStructure();

			pageStructure.setId(UUID.randomUUID());
			pageStructure.setPackageStructureId(packageStructure.getId());
			pageStructure.setXmlContent(baseEnglishPage.getXmlContent());
			pageStructure.setFilename(baseEnglishPage.getFilename());

			Map<UUID, Document> translatablePageMap = Maps.newHashMap();

			for(UUID translationId : pageDirectoryMap.keySet())
			{
				translatablePageMap.put(translationId, pageDirectoryMap.get(translationId).next().getXmlContent());
			}

			TranslatableElements translatableElements = new TranslatableElements(baseEnglishPage.getXmlContent(), baseEnglishPage.getFilename(), translatablePageMap);
			translatableElements.save(translationElementService);

			pageStructureService.insert(pageStructure);
		}
	}

    private File getDirectory() throws URISyntaxException
    {
        URL packageFolderUrl = this.getClass().getResource("/data/SnuffyPackages/" + packageCode);
        return new File(packageFolderUrl.toURI());
    }

    private Document getPackageDescriptorXml(File packageDescriptor) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        return builder.parse(packageDescriptor);
    }

    private String getPackageName(Document packageDescriptorXml)
    {
        NodeList nodes = packageDescriptorXml.getElementsByTagName("packagename");

        for(int i = 0; i < nodes.getLength(); i++)
        {
            if(!Strings.isNullOrEmpty(nodes.item(i).getTextContent()))
            {
                return nodes.item(i).getTextContent();
            }
        }

        return null;
    }
}
