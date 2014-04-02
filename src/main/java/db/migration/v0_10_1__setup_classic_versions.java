package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.Translation;
import org.cru.godtools.api.translations.TranslationService;
import org.cru.godtools.migration.MigrationProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/31/14.
 */
public class v0_10_1__setup_classic_versions implements JdbcMigration
{


    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();

        LanguageService languageService = new LanguageService(sqlConnection);
        TranslationService translationService = new TranslationService(sqlConnection);
        VersionService versionService = new VersionService(sqlConnection);
		ImageService imageService = new ImageService(sqlConnection, new ImagePageRelationshipService(sqlConnection));
        PageService pageService = new PageService(sqlConnection);
        ImagePageRelationshipService imagePageRelationshipService = new ImagePageRelationshipService(sqlConnection);
        PackageService packageService = new PackageService(sqlConnection);

        for(Language language : languageService.selectAllLanguages())
        {
            LanguageCode languageCode = LanguageCode.fromLanguage(language);
            if(languageCode.toString().contains("classic"))
            {
                for(Translation translation : translationService.selectByLanguageId(language.getId()))
                {
                    Version version = versionService.selectLatestVersionForTranslation(translation.getId());
                    Package gtPackage = packageService.selectById(version.getPackageId());


                    Document packageXml = version.getPackageStructure();
                    Page fakePage = createFakePage(version, languageCode);
                    pageService.insert(fakePage);

                    for(Element page : XmlDocumentSearchUtilities.findElements(packageXml, "page"))
                    {
                        String filename = page.getAttribute("filename");
                        Image image = imageService.selectByFilename(gtPackage.getCode() + "_" + languageCode.toString() + "_" + filename);
                        page.setAttribute("filename", image.getImageHash() + ".png");
                        imagePageRelationshipService.insert(new ImagePageRelationship(fakePage, image));
                    }

                    versionService.update(version);
                }
            }
        }

        sqlConnection.commit();
    }

    private Page createFakePage(Version version, LanguageCode languageCode)
    {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setDescription("Placeholder page for " + languageCode.toString());
        page.setOrdinal(0);
        page.setVersionId(version.getId());

        return page;
    }

}
