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
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/12/14.
 */
public class V0_6__setup_referenced_images implements JdbcMigration
{
	org.sql2o.Connection sqlConnection = MigrationProcess.getSql2oConnection();
	PackageService packageService = new PackageService(sqlConnection);
	PackageStructureService packageStructureService = new PackageStructureService(sqlConnection);
	PageStructureService pageStructureService = new PageStructureService(sqlConnection);
	TranslationService translationService = new TranslationService(sqlConnection);

	ImageService imageService = new ImageService(sqlConnection);
	ReferencedImageService referencedImageService = new ReferencedImageService(sqlConnection);

	@Override
	public void migrate(Connection connection) throws Exception
	{
		for(Package gtPackage : KnownGodtoolsPackages.packages)
		{
			PackageStructure packageStructure = packageStructureService.selectByPackageId(packageService.selectByCode(gtPackage.getCode()).getId());

			for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(packageStructure.getXmlContent(), "page", "thumb"))
			{
				saveImageReference(gtPackage.getCode(), element.getAttribute("thumb"), packageStructure.getId());
			}

			for(Translation translation : translationService.selectByPackageId(packageService.selectByCode(gtPackage.getCode()).getId()))
			{
				for (PageStructure pageStructure : pageStructureService.selectByTranslationId(translation.getId()))
				{
					for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(pageStructure.getXmlContent(), "page", "backgroundimage"))
					{
						saveImageReference(gtPackage.getCode(), element.getAttribute("backgroundimage"), packageStructure.getId());
					}

					for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(pageStructure.getXmlContent(), "page", "watermark"))
					{
						saveImageReference(gtPackage.getCode(), element.getAttribute("watermark"), packageStructure.getId());
					}

					for (Element element : XmlDocumentSearchUtilities.findElements(pageStructure.getXmlContent(), "image"))
					{
						saveImageReference(gtPackage.getCode(), element.getTextContent(), packageStructure.getId());
					}
				}
			}

			// save the icon references for this package.  they are titled "icon.png
			saveImageReference(gtPackage.getCode(), "icon.png", packageStructure.getId());
			saveImageReference(gtPackage.getCode(), "icon@2x.png", packageStructure.getId());
		}
	}

	private void saveImageReference(String packageCode, String filename, UUID packageStructureId)
	{
		String mediumFilename = filename;
		String highFilename = filename.substring(0, filename.length() -4) + "@2x.png";

		insertImage(packageCode, mediumFilename, packageStructureId);
		insertImage(packageCode, highFilename, packageStructureId);
	}

	private void insertImage(String packageCode, String filename, UUID packageStructureId)
	{
		Image image = imageService.selectByFilename(packageCode + "__" + filename);

		if (image == null)
		{
			image = imageService.selectByFilename("shared" + "__" + filename);
		}

		if (image != null)
		{
			ReferencedImage referencedImage = new ReferencedImage();
			referencedImage.setImageId(image.getId());
			referencedImage.setPackageStructureId(packageStructureId);
			referencedImage.setDensity(image.getResolution());

			referencedImageService.insert(referencedImage);
		}
	}
}
