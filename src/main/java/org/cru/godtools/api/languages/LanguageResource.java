package org.cru.godtools.api.languages;

import com.google.common.base.Optional;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.NewTranslationCreation;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.services.*;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by ryancarlson on 10/13/14.
 */
@Path("/languages")
public class LanguageResource
{

	@Inject
	AuthorizationService authorizationService;
	@Inject
	LanguageService languageService;
	@Inject
	PackageService packageService;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	NewTranslationCreation translationCreation;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addLanguage(Language language, @HeaderParam("Authorization") String authorization)
	{
		Optional<AuthorizationRecord> authorizationRecord = authorizationService.getAuthorizationRecord(authorization, null);

		if(!authorizationRecord.isPresent() || !authorizationRecord.get().isAdmin()) return Response.status(401).build();

		if(languageService.languageExists(language)) return Response.status(400).build();

		language.setId(UUID.randomUUID());

		languageService.insert(language);

		for(org.cru.godtools.domain.packages.Package gtPackage : packageService.selectAllPackages())
		{
			godToolsTranslationService.setupNewTranslation(LanguageCode.fromLanguage(language),
					gtPackage.getCode());
		}
		return Response.status(201).build();
	}
}
