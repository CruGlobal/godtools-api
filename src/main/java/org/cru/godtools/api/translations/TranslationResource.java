package org.cru.godtools.api.translations;

import org.ccci.util.time.Clock;
import org.cru.godtools.api.packages.GodToolsPageStructureRetrieval;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.translations.Translation;
import org.jboss.logging.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;

/**
 * Contains RESTful endpoints for delivering GodTools "translation" resources.
 *  - "translation" include translation XML files, but not images
 *
 * For more information: https://github.com/CruGlobal/godtools-api/wiki/The-Translations-Endpoint
 *
 * Created by ryancarlson on 4/8/14.
 */
@Path("/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;
	@Inject @Default
	GodToolsTranslationRetrieval translationRetrieval;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	PageStructureService pageStructureService;
	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(TranslationResource.class);

	@GET
	@Path("/{language}")
	@Produces({"application/zip", "application/xml"})
	public Response getTranslations(@PathParam("language") String languageCode,
									@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									@QueryParam("compressed") String compressed,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all translations for language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return translationRetrieval
				.setLanguageCode(languageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.loadTranslations()
				.buildResponse();
	}

	@GET
	@Path("/{language}/{package}")
	@Produces({"application/zip", "application/xml"})
	public Response getTranslation(@PathParam("language") String languageCode,
								   @PathParam("package") String packageCode,
								   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								   @QueryParam("compressed") String compressed,
								   @QueryParam("version") BigDecimal versionNumber,
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return translationRetrieval
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setVersionNumber(versionNumber == null ? GodToolsVersion.LATEST_VERSION : new GodToolsVersion(versionNumber))
				.loadTranslations()
				.buildResponse();
	}

	@POST
	@Path("/{language}/{package}")
	public Response createTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam) throws URISyntaxException
	{
		log.info("Creating new translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		Translation translation = godToolsTranslationService.setupNewTranslation(new LanguageCode(languageCode), packageCode);

		Simply.logObject(translation, TranslationResource.class);

		// FIXME: this isn't quite right yet... should have major.minor version number
		return Response.created(new URI("/" + languageCode + "/" + packageCode + "?version=" + translation.getVersionNumber())).build();
	}

	@PUT
	@Path("/{language}/{package}")
	public Response updateTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam,
									  @QueryParam("publish") String publish)
	{
		log.info("Updating translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		if(Boolean.parseBoolean(publish))
		{
			log.info("Publishing translation");
			godToolsTranslationService.publishDraftTranslation(new LanguageCode(languageCode), packageCode);
			log.info("Done publishing!");
		}
		return Response.noContent().build();
	}

	@GET
	@Path("/{language}/{package}/page")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllPageStructures(@PathParam("language") String languageCode,
	                                     @PathParam("package") String packageCode,
	                                     @QueryParam("version") BigDecimal versionNumber,
										 @HeaderParam("Authorization") String authTokenHeader,
										 @QueryParam("Authorization") String authTokenParam) throws URISyntaxException, IOException
	{
		log.info("Requesting all page structures for language: " + languageCode + " and package: " + packageCode);
		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		Set<GodToolsTranslation> translations = translationRetrieval.setLanguageCode(languageCode).setPackageCode(packageCode)
			.setVersionNumber(GodToolsVersion.LATEST_VERSION).loadTranslations().godToolsTranslations;

		GodToolsPageStructureRetrieval pageStructureRetrieval = new GodToolsPageStructureRetrieval();

		pageStructureRetrieval.getPageStructures(translations);
		return pageStructureRetrieval.buildXMLResponse();
	}

	@PUT
	@Path("/{language}/{package}/page/{page}")
	public Response updatePageStructure(@PathParam("language") String languageCode,
	                                    @PathParam("package") String packageCode,
	                                    @PathParam("page") UUID pageStructureId,
	                                    @HeaderParam("Authorization") String authTokenHeader,
	                                    @QueryParam("Authorization") String authTokenParam) throws URISyntaxException
	{
		log.info("Updating page structure for package: " + packageCode + " language: " + languageCode + " and page structure ID: " + pageStructureId.toString());

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		PageStructure pageStructure = pageStructureService.selectByid(pageStructureId);

		//	I assume there will need to be some logic here to replace the new xml with the old xml.
		pageStructureService.update(pageStructure);

		return Response.noContent().build();
	}

	@GET
	@Path("/{language}/{package}/page/{page}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPageStructure(@PathParam("language") String languageCode,
	                                 @PathParam("package") String packageCode,
	                                 @PathParam("page") UUID pageStructureId,
	                                 @HeaderParam("Authorization") String authTokenHeader,
	                                 @QueryParam("Authorization") String authTokenParam)
	{
		log.info("Getting page structure for package: " + packageCode + " language: " + languageCode + " and page structure ID: " + pageStructureId.toString());

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenHeader, authTokenParam), clock.currentDateTime());

		PageStructure pageStructure = pageStructureService.selectByid(pageStructureId);

		log.info("Returned page structure for package: " + packageCode + " language: " + languageCode + " and page structure ID: " + pageStructure
		+ "%n" + pageStructure.getXmlContent());

		return Response.ok(pageStructure.getXmlContent()).build();
	}
}
