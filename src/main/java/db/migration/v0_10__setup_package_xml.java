package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.EstonianLanguageCode;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.migration.MigrationProcess;
import org.cru.godtools.migration.ThumbsImageDirectory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class v0_10__setup_package_xml implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        PackageService packageService = new PackageService(sqlConnection);
		TranslationService translationService = new TranslationService(sqlConnection);
		PageService pageService = new PageService(sqlConnection);
        VersionService versionService = new VersionService(sqlConnection);
		LanguageService languageService = new LanguageService(sqlConnection);

		for(String packageCode : KnownGodtoolsPackages.packageNames)
        {
			Package gtPackage = packageService.selectByCode(packageCode);
			List<Translation> translationsForPackage = translationService.selectByPackageId(gtPackage.getId());

			for(Translation translation : translationsForPackage)
			{
				Language languageForCurrentTranslation = languageService.selectLanguageById(translation.getLanguageId());

				new EstonianLanguageCode().addHeartbeatSubculture(languageForCurrentTranslation);

				for(Version version : versionService.selectByTranslationId(translation.getId()))
				{
					if(version.getPackageStructure() == null) continue;

					for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(version.getPackageStructure(), "page", "filename"))
					{
						Page referencedPage = pageService.selectByFilenameAndVersionId(pageElement.getAttribute("filename"), version.getId());

						if (referencedPage != null)
						{
							pageElement.setAttribute("filename", referencedPage.getPageHash() + ".xml");
						}
					}

					ThumbsImageDirectory thumbsDirectory = new ThumbsImageDirectory("/data/SnuffyPackages/" + packageCode + "/" + languageForCurrentTranslation.getPath());
					Map<String, Image> thumbsDirectoryImageFilenameMap = thumbsDirectory.getImagesByFilenameMap();

					for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(version.getPackageStructure(),"page", "thumb"))
					{
						Image referencedThumbnail = thumbsDirectoryImageFilenameMap.get(pageElement.getAttribute("thumb"));
						if(referencedThumbnail != null)
						{
							pageElement.setAttribute("thumb", referencedThumbnail.getImageHash() + ".png");
						}
					}
					versionService.update(version);

				}
			}
        }
    }
}
