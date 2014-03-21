package db.migration;

import com.google.common.collect.Lists;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.ImageService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageService;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.migration.MigrationProcess;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class V0_9__setup_page_images implements JdbcMigration
{

    @Override
    public void migrate(Connection connection) throws Exception
    {
        org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();

        PageService pageService = new PageService(sqlConnection);
        ImageService imageService = new ImageService(sqlConnection);

        for(Page page : pageService.selectAllPages())
        {
            List<Element> elementsWithImage = Lists.newArrayList();

            for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(page.getXmlContent(), "page", "backgroundimage"))
            {
                String filename = element.getAttribute("backgroundimage");
                Image image = imageService.selectByFilename(filename);
                element.setAttribute("backgroundimage", image.getImageHash() + ".png");
            }

            for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(page.getXmlContent(), "page", "watermark"))
            {
                String filename = element.getAttribute("watermark");
                Image image = imageService.selectByFilename(filename);
                element.setAttribute("watermark", image.getImageHash() + ".png");
            }

            for(Element element : XmlDocumentSearchUtilities.findElements(page.getXmlContent(), "image"))
            {
                String filename = element.getTextContent();

                System.out.println(filename);
                Image image = imageService.selectByFilename(filename);
                element.setTextContent(image.getImageHash() + ".png");
            }

            pageService.update(page);
        }

    }
}
