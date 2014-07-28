package org.cru.godtools.RedEggConfig;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ApplicationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by matthewfrederick on 7/24/14.
 */
@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<ApplicationException>
{

    Logger log = Logger.getLogger(this.getClass());

    @Override
    public Response toResponse(ApplicationException applicationException)
    {
        Throwable actualException = unwrapApplicationException(applicationException);

        Response response = Response.serverError().header("Error", actualException.getMessage()).build();

        if(actualException instanceof WebApplicationException)
        {
            response = ((WebApplicationException)actualException).getResponse();
        }

        logThrowable("caught exception ", actualException, response.getStatus());

        return response;
    }

    private void logThrowable(String message, Throwable throwable, int status)
    {
        if(serverError(status))
        {
            log.error(message, throwable);
        } else {
            log.info(message, throwable);
        }
    }

    private boolean serverError(int status)
    {
        return status >= 500;
    }

    private Throwable unwrapApplicationException(ApplicationException applicationException)
    {
        return applicationException.getCause();
    }
}
