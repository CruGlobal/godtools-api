package org.cru.godtools.migration;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.Page;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.w3c.dom.Document;
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
 * Encapsulates logic for a package directory. (e.g: "/Packages/kgp")
 *
 *  - build a Package
 *  - build a list of Languages a Package is translated into
 *  - build a list of Icons represented by a Package
 */
public class PackageDirectory
{
	public static final String DIRECTORY_BASE = "/Packages/";

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
		String path = DIRECTORY_BASE + packageCode + "/";
		path += language.getCode();
		if(!Strings.isNullOrEmpty(language.getLocale())) path = path + "-" + language.getLocale();
		if(!Strings.isNullOrEmpty(language.getSubculture())) path = path + "-" + language.getSubculture();
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

		for(Translation translation : translationService.selectByPackageId(gtPackage.getId()))
		{
			TranslatableElements translatableElements = new TranslatableElements(packageStructure.getXmlContent(),
					packageStructure.getXmlContent(),
					packageCode + ".xml",
					translation.getId());

			translatableElements.save(translationElementService);
		}
		packageStructureService.insert(packageStructure);
	}

	/**
	 * Save a PageStructure for each page in each Translation of a GodTools Package.
	 */
	public void savePageStructures()
	{
		// load the package out
		Package gtPackage = packageService.selectByCode(packageCode);

		Map<UUID, Iterator<Page>> translationPageDirectoryIteratorMap = Maps.newHashMap();

		// holds a reference to the english translation of the current package in scope
		PageDirectory baseEnglishPageDirectory = new PageDirectory(packageCode, "en");

		// load the translations we know about
		for(Translation translation : translationService.selectByPackageId(gtPackage.getId()))
		{
			Language languageRepresentedByTranslation = languageService.selectLanguageById(translation.getLanguageId());
			// for each translation, load up all the pages from the filesystem and associate an iterator to those pages to the translation
			translationPageDirectoryIteratorMap.put(translation.getId(), new PageDirectory(packageCode, languageRepresentedByTranslation.getPath()).buildPages().iterator());
		}

		// loop through each translation ID
		for(UUID translationId : translationPageDirectoryIteratorMap.keySet())
		{
			// take the iterator for the pages on the translation and loop through the pages
			Iterator<Page> i = translationPageDirectoryIteratorMap.get(translationId);
			for( ; i.hasNext();)
			{
				Page translatedPage = i.next();

				// each page gets its own PageStructure
				PageStructure pageStructure = new PageStructure();

				// initialize the fields we can with data we know
				pageStructure.setId(UUID.randomUUID());
				pageStructure.setXmlContent(translatedPage.getXmlContent());
				pageStructure.setFilename(translatedPage.getFilename());
				pageStructure.setTranslationId(translationId);

				Page basePage;

				try
				{
					 basePage = baseEnglishPageDirectory.getPageByName(translatedPage.getFilename());
				}
				catch(FileNotFoundException e)
				{
					throw Throwables.propagate(e);
				}

				pageStructureService.insert(pageStructure);

				TranslatableElements translatableElements = new TranslatableElements(basePage.getXmlContent(),
						translatedPage.getXmlContent(),
						translatedPage.getFilename(),
						translationId,
						pageStructure.getId());

				translatableElements.save(translationElementService);

				pageStructureService.update(pageStructure);
			}
		}
	}

	private File getDirectory() throws URISyntaxException
	{
		URL packageFolderUrl = this.getClass().getResource(DIRECTORY_BASE + packageCode);
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
