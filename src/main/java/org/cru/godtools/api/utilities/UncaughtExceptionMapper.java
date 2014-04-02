package org.cru.godtools.api.utilities;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ApplicationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<ApplicationException>
{

    Logger log = Logger.getLogger(this.getClass());

    @Override
    public Response toResponse(ApplicationException applicationException)
    {
        Throwable actualException = unwrapApplicationException(applicationException);

		if(actualException.getClass().isAssignableFrom(ResourceNotFoundException.class))
		{
			ResourceNotFoundException notFoundException = (ResourceNotFoundException) actualException;
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new ErrorResponse(notFoundException.getMissingType() + " was not found."))
					.build();
		}
        if(actualException instanceof WebApplicationException)
        {
            return ((WebApplicationException)actualException).getResponse();
        }

        log.error("5** exception caught", actualException);

        return Response.serverError().header("Error" , actualException.getMessage()).build();
    }

    private Throwable unwrapApplicationException(ApplicationException applicationException)
    {
        return applicationException.getCause();
    }
}
