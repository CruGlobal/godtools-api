package org.cru.godtools.api.meta;

import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.translations.TranslationService;
import org.sql2o.Connection;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class MetaService
{
    Connection sqlConnection;
    LanguageService languageService;
    TranslationService translationService;
    PackageService packageService;

    @Inject
    public MetaService(Connection sqlConnection, LanguageService languageService, TranslationService translationService, PackageService packageService)
    {
        this.sqlConnection = sqlConnection;
        this.languageService = languageService;
        this.translationService = translationService;
        this.packageService = packageService;
    }

    public MetaResults getMetaResults(String languageCode, String packageCode)
    {
//        List<Language> languages = languageService.selectByLanguageCode(new LanguageCode(languageCode));
//        List<Translation> translations = translationService.selectByLanguageId();


        return new MetaResults();
    }
}
