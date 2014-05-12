package db.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.images.domain.ReferencedImage;
import org.cru.godtools.api.images.domain.ReferencedImageService;
import org.cru.godtools.api.packages.domain.PackageService;
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.PackageStructureService;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.packages.domain.PageStructureService;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.migration.MigrationProcess;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.util.List;

import static org.cru.godtools.migration.KnownGodtoolsPackages.packages;

/**
 * Created by ryancarlson on 5/12/14.
 */
public class V0_6__setup_referenced_images implements JdbcMigration
{
	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	PackageService packageService = new PackageService(sqlConnection);
	PackageStructureService packageStructureService = new PackageStructureService(sqlConnection);
	PageStructureService pageStructureService = new PageStructureService(sqlConnection);

	ImageService imageService = new ImageService(sqlConnection);
	ReferencedImageService referencedImageService = new ReferencedImageService(sqlConnection);

	@Override
	public void migrate(Connection connection) throws Exception
	{
		for(org.cru.godtools.api.packages.domain.Package gtPackage : packages)
		{
			PackageStructure packageStructure = packageStructureService.selectByPackageId(packageService.selectByCode(gtPackage.getCode()).getId());
			List<PageStructure> pageStructureList = pageStructureService.selectByPackageStructureId(packageStructure.getId());

			for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(packageStructure.getXmlContent(), "page", "thumb"))
			{
				Image image = imageService.selectByPackageNameAndFilename(gtPackage.getCode(), element.getAttribute("thumb"));
				saveImage(packageStructure, image);
			}

			for(PageStructure pageStructure : pageStructureList)
			{
				for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(pageStructure.getXmlContent(), "page", "backgroundimage"))
				{
					Image image = imageService.selectByPackageNameAndFilename(gtPackage.getCode(), element.getAttribute("backgroundimage"));

					saveImage(packageStructure, image);
				}

				for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(pageStructure.getXmlContent(), "page", "watermark"))
				{
					Image image = imageService.selectByPackageNameAndFilename(gtPackage.getCode(), element.getAttribute("watermark"));

					saveImage(packageStructure, image);
				}

				for(Element element : XmlDocumentSearchUtilities.findElements(pageStructure.getXmlContent(), "image"))
				{
					Image image = imageService.selectByPackageNameAndFilename(gtPackage.getCode(), element.getTextContent());

					saveImage(packageStructure, image);
				}
			}
		}

	}

	private void saveImage(PackageStructure packageStructure, Image image)
	{
		if(image != null)
		{
			ReferencedImage referencedImage = new ReferencedImage();
			referencedImage.setImageId(image.getId());
			referencedImage.setPackageStructureId(packageStructure.getId());

			referencedImageService.insert(referencedImage);
		}
	}
}
