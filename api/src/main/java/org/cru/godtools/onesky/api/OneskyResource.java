package org.cru.godtools.onesky.api;

import org.cru.godtools.onesky.io.TranslationUpload;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by ryancarlson on 7/14/14.
 */
@Path("/onesky")
public class OneskyResource
{
	/**
	 * Here temporarily for uploading new projects until more robust logic is built out.
	 */
	String DEFAULT_BASE_LOCALE = "en";

	@Inject TranslationUpload translationUpload;

	@POST
	@Path("/project/{projectId}/uploads")
	public Response uploadProjectToOnesky(@PathParam("projectId") Integer projectId)
	{
		translationUpload.doUpload(projectId, DEFAULT_BASE_LOCALE);

		translationUpload.recordInitialUpload(projectId, DEFAULT_BASE_LOCALE);

		return Response.noContent().build();
	}


	@POST
	@Path("/project/{projectId}/uploads/locale/{locale}")
	public Response uploadLocaleToOnesky(@PathParam("projectId") Integer projectId, @PathParam("locale") String locale)
	{
		// note this check might not be necessary... we would just update the translator tool when needed.
		if(translationUpload.checkHasTranslationAlreadyBeenUploaded(projectId, locale))
		{
			// do something here, possible a 400?
		}

		translationUpload.doUpload(projectId, locale);

		translationUpload.recordInitialUpload(projectId, locale);

		return Response.noContent().build();
	}

	@POST
	@Consumes("multipart/form-data")
	@Path("/self")
	public Response selfInspector(MultipartInput form) throws IOException
	{
		for(InputPart inputPart : form.getParts())
		{
			String inputPartString = inputPart.getBodyAsString();
			inputPartString = inputPartString;
		}

		return Response.status(200).build();
	}
}
