package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.migration.MigrationProcess;
import org.w3c.dom.Element;

import java.sql.Connection;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class v0_11__setup_thumbnails implements JdbcMigration
{

    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
		ImageService imageService = new ImageService(sqlConnection);
        VersionService versionService = new VersionService(sqlConnection);

        for(Version version : versionService.selectAllVersions())
        {
            for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(version.getPackageStructure(),"page", "thumb"))
            {
//                Image referencedThumbnail = imageService.selectByFilename(pageElement.getAttribute("thumb"));
//                if(referencedThumbnail != null)
//                {
//                    pageElement.setAttribute("thumb", referencedThumbnail.getImageHash() + ".png");
//                }

                versionService.update(version);
            }
        }
	}

}

