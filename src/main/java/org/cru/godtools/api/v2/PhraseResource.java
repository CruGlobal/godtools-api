package org.cru.godtools.api.v2;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.v2.functions.DraftTranslation;
import org.cru.godtools.api.v2.functions.PublishedTranslation;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationUpload;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/v2/packages/{package}/pages/{pageName}/phrases")
public class PhraseResource
{
	@Inject
	AuthorizationService authorizationService;

	@Inject
	Clock clock;

	@Inject
	PublishedTranslation publishedTranslation;

	@Inject
	DraftTranslation draftTranslation;

	@Inject
	PackageService packageService;

	@Inject
	LanguageService languageService;

	@Inject
	TranslationService translationService;

	@Inject
	TranslationElementService translationElementService;

	@Inject
	TranslationUpload translationUpload;

	/**
	 * English is the base language for God Tools.  Phrases should always be manipulated relative to English
	 */
	private static final String BASE_LANGUAGE_CODE = "en";

	private static final Logger logger = Logger.getLogger(PhraseResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listPhrasesForPage(@HeaderParam("Authorization") String authorization,
									   @PathParam("package") String packageCode,
									   @PathParam("pageName") String pageName)
	{
		logger.info(String.format("Listing phrases w/ authorization %s", authorization));

		AuthorizationRecord.checkAdminAccess(authorizationService.getAuthorizationRecord(null, authorization), clock.currentDateTime());

		logger.info(String.format("Listing phrases, admin validated.  Listing phrases for %s-%s", packageCode, pageName));

		Optional<GodToolsTranslation> translationOptional = getGodToolsTranslationOptional(packageCode);

		Optional<PageStructure> pageOptional = translationOptional.get().getPage(pageName);

		if(!pageOptional.isPresent())
		{
			logger.warn(String.format("Listing phrases, Page %s not found English translation for %s", pageName, packageCode));

			return Response.status(Response.Status.NOT_FOUND).build();
		}

		List<TranslationElement> translationElements = translationElementService.selectByTranslationIdPageStructureId(translationOptional.get().getTranslation().getId(),
				pageOptional.get().getId());

		return Response
				.ok(translationElements)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPhraseToPackage(@HeaderParam("Authorization") String authorization,
									   @PathParam("package") String packageCode,
									   @PathParam("pageName") String pageName,
									   TranslationElement translationElement)
	{
		logger.info(String.format("Adding phrase w/ authorization %s", authorization));

		AuthorizationRecord.checkAdminAccess(authorizationService.getAuthorizationRecord(null, authorization), clock.currentDateTime());

		logger.info(String.format("Adding phrase, admin validated.  Adding phrase for %s-%s", packageCode, pageName));

		Optional<GodToolsTranslation> translationOptional = getGodToolsTranslationOptional(packageCode);

		Optional<PageStructure> pageOptional = translationOptional.get().getPage(pageName);

		if(!pageOptional.isPresent())
		{
			logger.warn(String.format("Adding phrases, Page %s not found English translation for %s", pageName, packageCode));

			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Package gtPackage = packageService.selectByCode(packageCode);  // can't be null by this point

		translationElement.setFieldsForNewPhrase(pageOptional.get());

		// TODO: display reordering - minor, perhaps

		for(Language language : languageService.selectAllLanguages())
		{
			Translation translation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
					gtPackage.getId(),
					GodToolsVersion.LATEST_VERSION);

			// no translation in this language
			if(translation == null) continue;

			// i probably don't need to copy here, but it makes me feel better to do it.  in the JPA world it would be
			// necessary
			TranslationElement translationElementForOtherLanguage = TranslationElement.copyOf(translationElement);

			translationElementForOtherLanguage.setTranslationId(translation.getId());

			translationElementService.insert(translationElementForOtherLanguage);
		}

		translationUpload.doUpload(gtPackage.getTranslationProjectId(),
				BASE_LANGUAGE_CODE,
				pageName);

		return Response
				.status(Response.Status.CREATED)
				.build();
	}

	@DELETE
	@Path("/{id}")
	public Response removePhraseFromPackage(@HeaderParam("Authorization") String authorization,
											@PathParam("package") String packageCode,
											@PathParam("pageName") String pageName,
											@PathParam("id") UUID phraseId)
	{
		logger.info(String.format("Removing phrase w/ authorization %s", authorization));

		AuthorizationRecord.checkAdminAccess(authorizationService.getAuthorizationRecord(null, authorization), clock.currentDateTime());

		logger.info(String.format("Removing phrase, admin validated.  Adding phrase for %s-%s", packageCode, pageName));

		Optional<GodToolsTranslation> translationOptional = draftTranslation.retrieve(BASE_LANGUAGE_CODE, packageCode, false);

		if(!translationOptional.isPresent())
		{
			translationOptional = publishedTranslation.retrieve(BASE_LANGUAGE_CODE, packageCode, false);

			if(!translationOptional.isPresent())
			{
				logger.warn("Something weird... no published English translation for: " + packageCode);

				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}

		Optional<PageStructure> pageOptional = translationOptional.get().getPage(pageName);

		if(!pageOptional.isPresent())
		{
			logger.warn(String.format("Removing phrases, Page %s not found English translation for %s", pageName, packageCode));

			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Package gtPackage = packageService.selectByCode(packageCode);  // can't be null by this point

		translationElementService.delete(phraseId);

		translationUpload.doUpload(gtPackage.getTranslationProjectId(),
				BASE_LANGUAGE_CODE,
				pageName,
				true);

		return Response
				.noContent()
				.build();
	}

	private Optional<GodToolsTranslation> getGodToolsTranslationOptional(String packageCode)
	{
		Optional<GodToolsTranslation> translationOptional = draftTranslation.retrieve(BASE_LANGUAGE_CODE, packageCode, false);

		if(!translationOptional.isPresent())
		{
			translationOptional = publishedTranslation.retrieve(BASE_LANGUAGE_CODE, packageCode, false);

			if(!translationOptional.isPresent())
			{
				logger.warn("Something weird... no published English translation for: " + packageCode);

				throw new NotFoundException();
			}
		}
		return translationOptional;
	}
}
