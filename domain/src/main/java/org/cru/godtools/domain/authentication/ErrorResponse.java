package org.cru.godtools.domain.authentication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by matthewfrederick on 7/14/14.
 */

@XmlRootElement(name = "error")
public class ErrorResponse
{
    private String errorMessage;

    public ErrorResponse()
    {
    }

    public ErrorResponse(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @XmlElement(name = "error-message")
    public String getErrorMessage()
    {
        return errorMessage;
    }
}
