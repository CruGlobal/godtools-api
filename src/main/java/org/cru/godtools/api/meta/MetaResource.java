package org.cru.godtools.api.meta;

import org.cru.godtools.api.utilities.ErrorResponse;
import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.api.services.AuthorizationService;
import org.cru.godtools.api.services.LanguageService;
import org.cru.godtools.api.services.PackageService;
import org.cru.godtools.api.services.TranslationService;
import org.cru.godtools.translate.client.TranslationUpload;
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

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/meta")
public class MetaResource
{

	@Inject
	MetaService metaService;
	@Inject
	AuthorizationService authService;

	@Inject
	PackageService packageService;
	@Inject
	TranslationService translationService;
	@Inject
	TranslationUpload translationUpload;
	@Inject
	private LanguageService languageService;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(MetaResource.class);

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getAllMetaInfo(@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
										@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
										@QueryParam("Authorization") String authCodeParam,
										@HeaderParam("Authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info");

		return getLanguageAndPackageMetaInfo(null, null, minimumInterpreterVersionParam, minimumInterpreterVersionHeader, authCodeParam, authCodeHeader);
	}

	@GET
	@Path("/{language}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageMetaInfo(@PathParam("language") String languageCode,
								@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								@QueryParam("Authorization") String authCodeParam,
								@HeaderParam("Authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info for language: " + languageCode);

		return getLanguageAndPackageMetaInfo(languageCode, null, minimumInterpreterVersionParam, minimumInterpreterVersionHeader, authCodeParam, authCodeHeader);
	}

	@GET
	@Path("/{language}/{package}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageAndPackageMetaInfo(@PathParam("language") String languageCode,
								@PathParam("package") String packageCode,
								@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								@QueryParam("Authorization") String authCodeParam,
								@HeaderParam("Authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info for package: " + packageCode + " language: " + languageCode);

		Optional<AuthorizationRecord> authorizationRecordOptional = authService.getAuthorizationRecord(authCodeParam, authCodeHeader);
		AuthorizationRecord.checkAuthorization(authorizationRecordOptional, clock.currentDateTime());

		Integer interpreterVersion = getMinimumInterpreterVersion(minimumInterpreterVersionParam, minimumInterpreterVersionHeader);

		if(interpreterVersion == null)
		{
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("parameter or header \"interpreter\" is required"))
					.build();
		}

		MetaResults metaResults = metaService.getMetaResults(languageCode,
				packageCode,
				authorizationRecordOptional.get().hasDraftAccess(),
				authorizationRecordOptional.get().isAdmin());

		Simply.logObject(metaResults, MetaResource.class);

		return Response.ok(metaResults).build();
	}

	private Integer getMinimumInterpreterVersion(Integer minimumInterpreterVersionParam, Integer minimumInterpreterVersionHeader)
	{
		return minimumInterpreterVersionParam == null ? minimumInterpreterVersionHeader : minimumInterpreterVersionParam;
	}
}
