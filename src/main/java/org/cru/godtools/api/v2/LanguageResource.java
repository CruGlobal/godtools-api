package org.cru.godtools.api.v2;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.v2.functions.DraftTranslation;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/v2/languages")
public class LanguageResource
{

	@Inject
	AuthorizationService authorizationService;
	@Inject
	LanguageService languageService;
	@Inject
	PackageService packageService;

	@Inject
	DraftTranslation draftTranslation;

	@Inject
	Clock clock;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addLanguage(Language language, @HeaderParam("Authorization") String authorization)
	{
		Optional<AuthorizationRecord> authorizationRecord = authorizationService.getAuthorizationRecord(authorization, null);

		AuthorizationRecord.checkAdminAccess(authorizationRecord, clock.currentDateTime());

		if(languageService.languageExists(language))
		{
			return Response
					.status(400)
					.build();
		}

		language.setId(UUID.randomUUID());

		languageService.insert(language);

		for(Package gtPackage : packageService.selectAllPackages())
		{
			draftTranslation.create(language.getCode(), gtPackage.getCode());
		}

		authorizationService.updateAdminRecordExpiration(authorizationRecord.get(), 4);

		return Response
				.status(201)
				.build();
	}
}
