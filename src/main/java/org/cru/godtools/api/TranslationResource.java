package org.cru.godtools.api;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.*;
import org.cru.godtools.api.translations.model.PageFile;
import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.model.AuthorizationRecord;
import org.cru.godtools.domain.services.AuthorizationService;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.model.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.services.LanguageService;
import org.cru.godtools.domain.model.Package;
import org.cru.godtools.domain.services.PackageService;
import org.cru.godtools.domain.model.PageStructure;
import org.cru.godtools.domain.services.PageStructureService;
import org.cru.godtools.domain.model.TranslationElement;
import org.cru.godtools.domain.services.TranslationElementService;
import org.cru.godtools.domain.model.Translation;
import org.cru.godtools.domain.services.TranslationService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
	@Inject
	GodToolsTranslationRetrieval translationRetrieval;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	PageStructureService pageStructureService;
	@Inject
	LanguageService languageService;
	@Inject
	PackageService packageService;
	@Inject
	TranslationService translationService;
	@Inject
	TranslationElementService translationElementService;
	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(TranslationResource.class);
	public static boolean BYPASS_ASYNC_UPDATE = false;

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

		// FIXME: this isn't quite right yet... major version number should not be hard coded, but for now the API doesn't support updating it
		return Response
				.created(new URI("/" + languageCode + "/" + packageCode + "?version=1." + translation.getVersionNumber()))
				.build();
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
	@Path("/{language}/{package}/config")
	@Produces("application/json")
	public Response getJsonConfig(@PathParam("language") String languageCode,
							  @PathParam("package") String packageCode,
							  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							  @QueryParam("compressed") String compressed,
							  @HeaderParam("Authorization") String authTokenHeader,
							  @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting config.xml for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return Response
				.ok(godToolsTranslationService.getConfig(packageCode, new LanguageCode(languageCode), GodToolsVersion.LATEST_PUBLISHED_VERSION))
				.build();
	}

	@GET
	@Path("/{language}/{package}/pages/{pageId}")
	@Produces("application/json")
	public Response getJsonPage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode,
							   @PathParam("pageId") UUID pageId,
							   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							   @HeaderParam("Authorization") String authTokenHeader,
							   @QueryParam("Authorization") String authTokenParam) throws IOException, ParserConfigurationException
	{
		log.info("Requesting draft page update for package: " + packageCode + " and language: " + languageCode + " and page ID: " + pageId);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		Optional<Response> optionalBadRequestResponse = verifyRequestedPageBelongsToPageAndLanguage(packageCode, languageCode, pageId);

		if(optionalBadRequestResponse.isPresent())
		{
			return optionalBadRequestResponse.get();
		}

		PageStructure pageStructure = pageStructureService.selectById(pageId);

		if(pageStructure == null)
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}

		List<TranslationElement> translationElements =
				translationElementService.selectByTranslationIdPageStructureId(
						translationService.selectById(pageStructure.getTranslation() != null ? pageStructure.getTranslation().getId() : null).getId(),
						pageId);

		PageFile pageFile = PageFile.fromTranslationElements(translationElements);

		return Response
				.ok(pageFile)
				.build();
	}

	private Optional<Response> verifyRequestedPageBelongsToPageAndLanguage(String packageCode, String languageCode, UUID pageId)
	{
		Package packageFromCode = packageService.selectByCode(packageCode);
		if(packageFromCode == null)
		{
			return Optional.fromNullable(
					Response.status(Response.Status.NOT_FOUND)
							.entity(String.format("Requested package %s was not found", packageCode))
							.build());
		}

		Language languageFromCode = languageService.selectByLanguageCode(new LanguageCode(languageCode));
		if(languageFromCode == null)
		{
			return Optional.fromNullable(
					Response.status(Response.Status.NOT_FOUND)
							.entity(String.format("Requested language %s was not found", languageCode))
							.build());
		}

		PageStructure pageStructure = pageStructureService.selectById(pageId);
		if(pageStructure == null)
		{
			return Optional.fromNullable(
					Response.status(Response.Status.NOT_FOUND)
							.entity(String.format("Requested page %s was not found", pageId.toString()))
							.build());
		}

		Translation translation = translationService.selectById(pageStructure.getTranslation() != null ? pageStructure.getTranslation().getId() : null);
		Package packageDerivedFromPage = packageService.selectById(translation.getPackage() != null ? translation.getPackage().getId() : null);
		Language languageDerivedFromPage = languageService.selectLanguageById(translation.getLanguage() != null ? translation.getLanguage().getId() : null);

		if(!packageFromCode.getId().equals(packageDerivedFromPage.getId()))
		{
			return Optional.fromNullable(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("Requested page %s does not belong to package %s", pageId.toString(), packageCode))
					.build());

		}

		if(!languageFromCode.getId().equals(languageDerivedFromPage.getId()))
		{
			return Optional.fromNullable(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("Requested page %s does not belong to language %s", pageId.toString(), languageCode))
					.build());
		}

		return Optional.absent();
	}

	public void setAutoCommit(boolean autoCommit)
	{
		authService.setAutoCommit(autoCommit);
		godToolsTranslationService.setAutoCommit(autoCommit);
		pageStructureService.setAutoCommit(autoCommit);
		languageService.setAutoCommit(autoCommit);
		packageService.setAutoCommit(autoCommit);
		translationService.setAutoCommit(autoCommit);
		translationElementService.setAutoCommit(autoCommit);
	}

	public void rollback()
	{
		authService.rollback();
		godToolsTranslationService.rollback();
		pageStructureService.rollback();
		languageService.rollback();
		packageService.rollback();
		translationService.rollback();
		translationElementService.rollback();
	}
}
