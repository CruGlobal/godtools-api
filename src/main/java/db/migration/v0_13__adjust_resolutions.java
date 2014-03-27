package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.ImageService;
import org.cru.godtools.migration.MigrationProcess;

import java.sql.Connection;

/**
 * Created by ryancarlson on 3/27/14.
 */
public class v0_13__adjust_resolutions implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
        ImageService imageService = new ImageService(sqlConnection);

        for(Image retinaBigBrotherImage : imageService.selectRetinaFiles())
        {
            Image littleBrotherImage = imageService.selectByFilename(retinaBigBrotherImage.getFilename().replace("@2x", ""));

            if(littleBrotherImage != null)
            {
                littleBrotherImage.setResolution("Medium");
                imageService.update(littleBrotherImage);
            }
            else
            {
                int x=0;
            }
        }

        sqlConnection.commit();
    }
}
