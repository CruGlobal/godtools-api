package org.cru.godtools.api.meta;

import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.api.utilities.ErrorResponse;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.TranslationUpload;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
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

	private Logger log = Logger.getLogger(MetaResource.class);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllMetaInfo(@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
										@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
										@QueryParam("authorization") String authCodeParam,
										@HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
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
                                @QueryParam("authorization") String authCodeParam,
                                @HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
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
								@QueryParam("authorization") String authCodeParam,
								@HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info for package: " + packageCode + " language: " + languageCode);

		authService.checkAuthorization(authCodeParam, authCodeHeader);
		Integer interpreterVersion = getMinimumInterpreterVersion(minimumInterpreterVersionParam, minimumInterpreterVersionHeader);

		if(interpreterVersion == null)
		{
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("parameter or header \"interpreter\" is required"))
					.build();
		}

		MetaResults metaResults = metaService.getMetaResults(languageCode,
				packageCode,
				interpreterVersion,
				authService.canAccessOrCreateDrafts(authCodeParam, authCodeHeader));

		Simply.logObject(metaResults, MetaResource.class);

		return Response.ok(metaResults).build();
	}

	private Integer getMinimumInterpreterVersion(Integer minimumInterpreterVersionParam, Integer minimumInterpreterVersionHeader)
    {
        return minimumInterpreterVersionParam == null ? minimumInterpreterVersionHeader : minimumInterpreterVersionParam;
    }
}
