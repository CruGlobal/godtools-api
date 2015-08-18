package org.cru.godtools.api.v2;

import org.ccci.util.time.Clock;
import org.cru.godtools.api.meta.MetaResults;
import org.cru.godtools.api.meta.MetaService;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;

@Path("v2/meta")
public class MetaResource
{
	@Inject
	AuthorizationService authService;

	@Inject
	MetaService metaService;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(this.getClass());

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getAllMetaInfo(@QueryParam("Authorization") String authCodeParam,
								   @HeaderParam("Authorization") String authCodeHeader,
								   @HeaderParam("Accept") String requestedContentType) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info");

		boolean hasDraftAccess = authService.hasDraftAccess(authCodeParam, authCodeHeader);
		boolean hasAdminAccess = authService.hasAdminAccess(authCodeParam, authCodeHeader);
		MediaType mediaType = resolveMediaType(requestedContentType);

		if(hasAdminAccess || hasDraftAccess)
		{
			// draft meta file is built from the database
			MetaResults metaResults = metaService.getAllMetaResults(hasDraftAccess, hasAdminAccess);

			return Response
					.ok(metaResults)
					.type(mediaType)
					.build();
		}
		else
		{
			return Response
					.status(Response.Status.MOVED_PERMANENTLY)
					.header("Location", AmazonS3GodToolsConfig.getMetaRedirectUrl(mediaType))
					.build();
		}
	}

	@GET
	@Path("/{language}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageMetaInfo(@PathParam("language") String languageCode,
										@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
										@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
										@QueryParam("Authorization") String authCodeParam,
										@HeaderParam("Authorization") String authCodeHeader,
										@HeaderParam("Accept") String requestedContentType) throws MalformedURLException
	{
		log.info("Getting all meta info for language: " + languageCode);

		boolean hasDraftAccess = authService.hasDraftAccess(authCodeParam, authCodeHeader);
		boolean hasAdminAccess = authService.hasAdminAccess(authCodeParam, authCodeHeader);
		MediaType mediaType = resolveMediaType(requestedContentType);

		if(hasDraftAccess || hasAdminAccess)
		{
			MetaResults metaResults = metaService.getLanguageMetaResults(languageCode,
					hasDraftAccess,
					hasAdminAccess);

			return Response
					.ok(metaResults)
					.type(mediaType)
					.build();
		}
		else
		{
			return Response
					.status(Response.Status.MOVED_PERMANENTLY)
					.header("Location", AmazonS3GodToolsConfig.getMetaRedirectUrl(mediaType))
					.build();
		}
	}

	@GET
	@Path("/{language}/{package}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageAndPackageMetaInfo(@PathParam("language") String languageCode,
												  @PathParam("package") String packageCode,
												  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
												  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
												  @QueryParam("Authorization") String authCodeParam,
												  @HeaderParam("Authorization") String authCodeHeader,
												  @HeaderParam("Accept") String requestedContentType) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info for package: " + packageCode + " language: " + languageCode);

		boolean hasDraftAccess = authService.hasDraftAccess(authCodeParam, authCodeHeader);
		boolean hasAdminAccess = authService.hasAdminAccess(authCodeParam, authCodeHeader);
		MediaType mediaType = resolveMediaType(requestedContentType);

		if(hasDraftAccess || hasAdminAccess)
		{
			MetaResults metaResults = metaService.getPackageMetaResults(languageCode,
					packageCode,
					hasDraftAccess,
					hasAdminAccess);

			return Response
					.ok(metaResults)
					.type(mediaType)
					.build();
		}
		else
		{
			return Response
					.status(Response.Status.MOVED_PERMANENTLY)
					.header("Location", AmazonS3GodToolsConfig.getMetaRedirectUrl(mediaType))
					.build();
		}
	}

	private MediaType resolveMediaType(String requestedContentType)
	{
		try
		{
			return MediaType.valueOf(requestedContentType);
		}
		catch(RuntimeException e)
		{
			throw new BadRequestException(
					String.format("Unrecognized Media Type %s. " +
									"(hint: check \"Accepts\" passes a valid type like \"application/json\" or \"application/xml\")",
							requestedContentType));
		}
	}
}
