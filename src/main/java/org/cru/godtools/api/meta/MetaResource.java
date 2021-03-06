package org.cru.godtools.api.meta;

import com.amazonaws.services.s3.model.S3Object;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.GodToolsS3Client;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Deprecated
@Path("/meta")
public class MetaResource
{
	@Inject
	AuthorizationService authService;

	@Inject
	MetaService metaService;

	@Inject
	Clock clock;

	@Inject
	GodToolsS3Client godToolsS3Client;

	private Logger log = Logger.getLogger(getClass());

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getAllMetaInfo(@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
										@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
										@QueryParam("Authorization") String authCodeParam,
										@HeaderParam("Authorization") String authCodeHeader,
								   		@HeaderParam("Accept") MediaType requestedContentType) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info");

		boolean retrieveDrafts = authService.hasDraftAccess(authCodeParam, authCodeHeader);

		if(retrieveDrafts)
		{
			// draft meta file is built from the database
			MetaResults metaResults = metaService.getAllMetaResults(retrieveDrafts, false);

			return Response
					.ok(metaResults)
					.type(requestedContentType == null || requestedContentType.isWildcardType() ?
							MediaType.APPLICATION_XML_TYPE : requestedContentType)
					.build();
		}
		else
		{
			// published meta file is retrieved from S3
			S3Object metaFile = godToolsS3Client.getMetaFile(requestedContentType);

			return Response
					.ok(metaFile.getObjectContent())
					.type(requestedContentType)
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
								@HeaderParam("Accept") MediaType requestedContentType)
	{
		log.info("Getting all meta info for language: " + languageCode);

		boolean retrieveDrafts = authService.hasDraftAccess(authCodeParam, authCodeHeader);

		if(retrieveDrafts)
		{
			MetaResults metaResults = metaService.getLanguageMetaResults(languageCode, retrieveDrafts, false);

			return Response
					.ok(metaResults)
					.type(requestedContentType == null || requestedContentType.isWildcardType() ?
							MediaType.APPLICATION_XML_TYPE : requestedContentType)
					.build();
		}
		else
		{
			// published meta file is retrieved from S3
			S3Object metaFile = godToolsS3Client.getMetaFile(requestedContentType);

			return Response
					.ok(metaFile.getObjectContent())
					.type(requestedContentType)
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
								@HeaderParam("Accept") MediaType requestedContentType) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info for package: " + packageCode + " language: " + languageCode);

		boolean retrieveDrafts = authService.hasDraftAccess(authCodeParam, authCodeHeader);

		if(retrieveDrafts)
		{
			MetaResults metaResults = metaService.getPackageMetaResults(languageCode, packageCode, retrieveDrafts, false);

			return Response
					.ok(metaResults)
					.type(requestedContentType == null || requestedContentType.isWildcardType() ?
							MediaType.APPLICATION_XML_TYPE : requestedContentType)
					.build();
		}
		else
		{
			// published meta file is retrieved from S3
			S3Object metaFile = godToolsS3Client.getMetaFile(requestedContentType);

			return Response
					.ok(metaFile.getObjectContent())
					.type(requestedContentType)
					.build();
		}
	}
}
