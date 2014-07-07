package org.cru.godtools.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

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
		for(Package gtPackage : packages)
		{
			PackageStructure packageStructure = packageStructureService.selectByPackageId(packageService.selectByCode(gtPackage.getCode()).getId());
			List<PageStructure> pageStructureList = pageStructureService.selectByPackageStructureId(packageStructure.getId());

			for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(packageStructure.getXmlContent(), "page", "thumb"))
			{
				saveImage(gtPackage.getCode(), element.getAttribute("thumb"), packageStructure.getId());
			}

			for(PageStructure pageStructure : pageStructureList)
			{
				for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(pageStructure.getXmlContent(), "page", "backgroundimage"))
				{
					saveImage(gtPackage.getCode(), element.getAttribute("backgroundimage"), packageStructure.getId());
				}

				for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(pageStructure.getXmlContent(), "page", "watermark"))
				{
					saveImage(gtPackage.getCode(), element.getAttribute("watermark"), packageStructure.getId());
				}

				for(Element element : XmlDocumentSearchUtilities.findElements(pageStructure.getXmlContent(), "image"))
				{
					saveImage(gtPackage.getCode(), element.getTextContent(), packageStructure.getId());
				}
			}
		}
	}

	private void saveImage(String packageCode, String filename, UUID packageStructureId)
	{
		Image image = imageService.selectByPackageNameAndFilename(packageCode, filename);

		if(image == null)
		{
			image = imageService.selectByPackageNameAndFilename("shared", filename);
		}
		if(image != null)
		{
			ReferencedImage referencedImage = new ReferencedImage();
			referencedImage.setImageId(image.getId());
			referencedImage.setPackageStructureId(packageStructureId);

			referencedImageService.insert(referencedImage);
		}

	}
}
