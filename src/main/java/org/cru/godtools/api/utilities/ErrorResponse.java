package org.cru.godtools.api.utilities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ryancarlson on 3/27/14.
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
