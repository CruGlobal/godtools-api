package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageService;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.domain.VersionService;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.migration.MigrationProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.Connection;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class v0_10__setup_page_names implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        PageService pageService = new PageService(sqlConnection);
        VersionService versionService = new VersionService(sqlConnection);

        for(Version version : versionService.selectAllVersions())
        {
			if(version.getPackageStructure() == null) continue;

            for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(version.getPackageStructure(), "page", "filename"))
            {
                Page referencedPage = pageService.selectByFilenameAndVersionId(pageElement.getAttribute("filename"), version.getId());
                if(referencedPage != null)
                {
                    pageElement.setAttribute("filename", referencedPage.getPageHash() + ".xml");
                }
            }
            versionService.update(version);
        }
    }
}
