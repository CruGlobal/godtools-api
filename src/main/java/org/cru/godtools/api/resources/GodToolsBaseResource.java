package org.cru.godtools.api.resources;

import org.jboss.logging.*;

import javax.interceptor.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.lang.reflect.*;

/**
 * Created by justinsturm on 8/5/15.
 */
public class GodToolsBaseResource
{
    Logger logger = Logger.getLogger(this.getClass());

    @AroundInvoke
    public Object transactionInterceptor(InvocationContext ctx) throws Exception
    {
        try
        {
            return ctx.getMethod().invoke(this, ctx.getParameters());
        }
        catch(Exception e)
        {
            if(suppressEntityInErrorNotifications())
            {
                logger.info("Error encountered, removing entity to keep sensitive data out of errbit", e);
            }

            Throwable targetThrowable = e;

            if(targetThrowable instanceof InvocationTargetException)
            {
                targetThrowable = ((InvocationTargetException)e).getTargetException();

            }
            if(targetThrowable instanceof javax.ejb.EJBTransactionRolledbackException)
            {
                targetThrowable = e.getCause();
            }

            logger.error(e);
            e.printStackTrace();

            return buildResponseFromThrowable(targetThrowable);
        }
    }

    private Response buildResponseFromThrowable(Throwable throwable)
    {
        if(throwable instanceof WebApplicationException)
        {
            return ((WebApplicationException)throwable).getResponse();
        }
        if(throwable instanceof NotAuthorizedException ||
                throwable instanceof javax.ws.rs.NotAuthorizedException)
        {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        if (throwable instanceof BadRequestException)
        {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new String(throwable.getMessage()))
                    .build();
        }
        if (throwable instanceof NotFoundException)
        {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new String(throwable.getMessage()))
                    .build();
        }
        if (throwable instanceof IllegalStateException)
        {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(new String(throwable.getMessage()))
                    .build();
        }
        if (throwable instanceof RuntimeException)
        {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new String(throwable.getMessage()))
                    .build();
        }

        return Response
                .serverError()
                .build();
    }
    /**
     * Overriding this method in another resource will result in the exception's being caught and
     * subsequently logged as in "INFO" record in the logs.  The end goal is to make sure that endpoints
     * which receive sensitive data in JSON posts are not logged in errbit when exceptions occur.
     */
    protected boolean suppressEntityInErrorNotifications()
    {
        return false;
    }
}
