package org.cru.godtools.api.v2.functions;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.ccci.util.time.Clock;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 11/9/16.
 */
public class NewPackage {

    private static Document EMPTY_PAGE_XML_DOCUMENT;
    private static Document EMPTY_ABOUT_XML_DOCUMENT;

    @Inject
    PackageService packageService;

    @Inject
    TranslationService translationService;

    @Inject
    PageStructureService pageStructureService;

    @Inject
    PackageStructureService packageStructureService;

    @Inject
    LanguageService languageService;

    @Inject
    Clock clock;

    public void create(org.cru.godtools.domain.packages.Package gtPackage,
                       Integer numberOfPages,
                       List<String> languageCodes) {
        validatePackage(gtPackage);

        packageService.insert(gtPackage);

        final List<Translation> createdTranslations = createAndSaveTranslations(gtPackage, languageCodes);

        final List<PageStructure> pageStructures = createAndSavePageStructures(createdTranslations, numberOfPages);

        createAndSavePackageStructure(gtPackage, pageStructures);
    }

    private void validatePackage(final Package gtPackage) {
        if (gtPackage.getId() == null) {
            gtPackage.setId(UUID.randomUUID());
        }
    }

    private void createAndSavePackageStructure(final Package gtPackage, final List<PageStructure> pageStructures) {
        PackageStructure packageStructure = new PackageStructure();
        packageStructure.setId(UUID.randomUUID());
        packageStructure.setPackageId(gtPackage.getId());
        packageStructure.setVersionNumber(1);
        packageStructure.setFilename(gtPackage.getCode().toLowerCase() + ".xml");
        packageStructure.setXmlContent(packageXMLDocument(gtPackage, pageStructures));
        packageStructureService.insert(packageStructure);
    }

    private List<Translation> createAndSaveTranslations(final Package gtPackage, final List<String> languageCodes)
    {
        List<Translation> createdTranslations = Lists.newArrayList();
        for(Language language : loadApplicableLanguages(languageCodes)) {
            Translation translation = new Translation();
            translation.setId(UUID.randomUUID());
            translation.setVersionNumber(1);
            translation.setReleased(false);
            translation.setPackageId(gtPackage.getId());
            translation.setLanguageId(language.getId());

            createdTranslations.add(translation);

            translationService.insert(translation);
        }

        return createdTranslations;
    }

    private List<Language> loadApplicableLanguages(final List<String> languageCodes)
    {
        if(languageCodes.isEmpty()) {
            return languageService.selectAllLanguages();
        }

        List<Language> languages = Lists.newArrayList();

        for(String languageCode : languageCodes) {
            languages.add(languageService.selectByLanguageCode(new LanguageCode(languageCode)));
        }

        return languages;
    }

    private List<PageStructure> createAndSavePageStructures(final List<Translation> createdTranslations, final Integer numberOfPages) {
        List<PageStructure> pageStructures = Lists.newArrayList();

        Set<String> filenames = Sets.newHashSet();

        for(Translation translation : createdTranslations) {
            for(int i = 0; i < numberOfPages; i++) {
                PageStructure pageStructure = new PageStructure();
                pageStructure.setId(UUID.randomUUID());
                pageStructure.setFilename(i + ".xml");
                pageStructure.setTranslationId(translation.getId());
                pageStructure.setLastUpdated(clock.currentDateTime());
                pageStructure.setXmlContent(emptyPageXMLDocument());

                if(!filenames.contains(i + ".xml")) {
                    pageStructures.add(pageStructure);
                    filenames.add(i + ".xml");
                }

                pageStructureService.insert(pageStructure);
            }

            PageStructure aboutPageStructure = new PageStructure();
            aboutPageStructure.setId(UUID.randomUUID());
            aboutPageStructure.setFilename("about.xml");
            aboutPageStructure.setTranslationId(translation.getId());
            aboutPageStructure.setLastUpdated(clock.currentDateTime());
            aboutPageStructure.setXmlContent(emptyAboutXMLDocument());

            if(!filenames.contains("about.xml")) {
                pageStructures.add(aboutPageStructure);
                filenames.add("about.xml");
            }

            pageStructureService.insert(aboutPageStructure);
        }

        return pageStructures;
    }

    private Document packageXMLDocument(final Package gtPackage,
                                                    final List<PageStructure> pageStructures) {
        String documentXMLString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><document></document>";

        try
        {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document overallDocument = builder.parse(new InputSource(new StringReader(documentXMLString)));

            final Element packageNameElement = overallDocument.createElement("packagename");
            packageNameElement.setAttribute("gtapi-trx-id", " ");
            packageNameElement.setAttribute("translate", "true");
            packageNameElement.setAttribute("thumb", "");
            packageNameElement.setTextContent(gtPackage.getName());

            overallDocument.getDocumentElement().appendChild(packageNameElement);
            overallDocument.getDocumentElement().appendChild(overallDocument.createTextNode("\n"));

            int i = 1;
            for(PageStructure pageStructure : pageStructures) {
                final Element pageElement = overallDocument.createElement(pageStructure.getFilename().contains("about") ? "about" : "page");

                pageElement.setAttribute("filename", pageStructure.getFilename());
                pageElement.setAttribute("thumb", pageStructure.getFilename().replace("xml", "png"));
                pageElement.setAttribute("gtapi-trx-id", " ");
                pageElement.setAttribute("translate", "true");
                pageElement.setTextContent("Page " + i++);
                overallDocument.getDocumentElement().appendChild(pageElement);
                overallDocument.getDocumentElement().appendChild(overallDocument.createTextNode("\n"));
            }

            return overallDocument;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static Document emptyPageXMLDocument() {
        if (EMPTY_PAGE_XML_DOCUMENT != null) {
            return EMPTY_PAGE_XML_DOCUMENT;
        }
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><page></page>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            EMPTY_PAGE_XML_DOCUMENT = builder.parse( new InputSource( new StringReader( xmlString ) ) );
            final Element titleElement = EMPTY_PAGE_XML_DOCUMENT.createElement("title");

            titleElement.setTextContent("Title");
            EMPTY_PAGE_XML_DOCUMENT.getDocumentElement().appendChild(titleElement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY_PAGE_XML_DOCUMENT;
    }

    private static Document emptyAboutXMLDocument() {
        if (EMPTY_ABOUT_XML_DOCUMENT != null) {
            return EMPTY_ABOUT_XML_DOCUMENT;
        }
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><about></about>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            EMPTY_ABOUT_XML_DOCUMENT = builder.parse( new InputSource( new StringReader( xmlString ) ) );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY_ABOUT_XML_DOCUMENT;
    }
}