package org.cru.godtools.api.v2;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("v2/packages/{package}/images")
public class ImageResource
{
	@Inject
	PackageService packageService;

	@Inject
	ReferencedImageService referencedImageService;

	@Inject
	ImageService imageService;

	@Inject
	PackageStructureService packageStructureService;

	@Inject
	AuthorizationService authorizationService;

	@Inject
	Clock clock;

	static final Logger logger = Logger.getLogger(ImageResource.class);

	@GET
	@Produces("application/json")
	public Response listImagesForPackage(@PathParam("package") String packageCode,
										 @HeaderParam("Authorization") String authorization)
	{
		logger.info(String.format("Listing images w/ authorization %s", authorization));

		AuthorizationRecord.checkAdminAccess(authorizationService.getAuthorizationRecord(null, authorization), clock.currentDateTime());

		logger.info(String.format("Listing images, admin validated.  Listing images for %s", packageCode));

		Package gtPackage = packageService.selectByCode(packageCode);

		if(gtPackage == null)
		{
			logger.info("Listing images, package not found");

			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("{ \"message\' : \'Package with code %s was not found.  Cannot list images.\"}", packageCode))
					.build();
		}

		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());

		if(packageStructure == null)
		{
			logger.info("Listing images, package not found");

			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("{ \"message\' : \'Package structure with code %s was not found.  Cannot list images.\"", packageCode))
					.build();
		}

		List<ReferencedImage> referencedImageList = referencedImageService.selectByPackageStructureId(packageStructure.getId(), true);

		logger.info(String.format("Returning %s image references", referencedImageList.size()));

		return Response
				.ok(referencedImageList)
				.build();
	}

	@GET
	@Path("/{imageId}")
	@Produces("application/json")
	public Response getImage(@PathParam("imageId") UUID imageId,
							 @QueryParam("metadata") String metadataOnly)
	{
		logger.info(String.format("Getting image for ID: %s", imageId));

		Image image = imageService.selectById(imageId);

		if(image == null)
		{
			logger.info(String.format("Getting image, not found for %s", imageId));

			return Response.status(Response.Status.NOT_FOUND).build();
		}

		if(Boolean.valueOf(metadataOnly))
		{
			return Response
					.ok(image, MediaType.APPLICATION_JSON)
					.build();
		}
		else
		{
			return Response
					.ok(new ByteArrayInputStream(image.getImageContent()), "image/png")
					.build();
		}
	}

	/**
	 * Parts inspired by tutorial by MK Yong: http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(@PathParam("package") String packageCode,
								@HeaderParam("Authorization") String authorization,
								@MultipartForm Image image)
	{
		logger.info(String.format("Uploading image to package %s w/ authorization %s", packageCode , authorization));

		AuthorizationRecord.checkAdminAccess(authorizationService.getAuthorizationRecord(null, authorization), clock.currentDateTime());

		logger.info(String.format("Uploading image, admin validated."));

		if(image.getImageContent() == null)
		{
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("{ \"message\' : \'Image content was null\"}", packageCode))
					.build();
		}

		if(image.getFilename() == null)
		{
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("{ \"message\' : \'Image filename was null\"}", packageCode))
					.build();
		}

		Package gtPackage = packageService.selectByCode(packageCode);

		if(gtPackage == null)
		{
			logger.info("Uploading images, package not found");

			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("{ \"message\' : \'Package with code %s was not found.  Cannot list images.\"}", packageCode))
					.build();
		}

		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());

		image.setId(UUID.randomUUID());
		//prepend package code and underscores
		image.setFilename(Image.buildFilename(packageCode, image.getFilename()));

		imageService.insert(image);
		insertReferencedImage(packageStructure, image);

		return Response
				.created(URI.create(String.format("/packages/%s/images/%s", packageCode, image.getId())))
				.build();
	}

	private void insertReferencedImage(PackageStructure packageStructure, Image image)
	{
		ReferencedImage referencedImage = new ReferencedImage();

		referencedImage.setImageId(image.getId());
		referencedImage.setPackageStructureId(packageStructure.getId());

		referencedImageService.insert(referencedImage);
	}
}
