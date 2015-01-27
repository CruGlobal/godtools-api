package org.cru.godtools.api.notifications;

import java.io.IOException;

/**
 * Created by matthewfrederick on 12/31/14.
 *
 * Copied from Google's Push Notification example by permission under http://www.apache.org/licenses/LICENSE-2.0
 */
public class InvalidRequestException extends IOException
{
	private final int status;
	private final String description;

	public InvalidRequestException(int status)
	{
		this(status, null);
	}

	public InvalidRequestException(int status, String description)
	{
		super(getMessage(status, description));
		this.status = status;
		this.description = description;
	}

	private static String getMessage(int status, String description)
	{
		StringBuilder base = new StringBuilder("HTTP Status Code: ").append(status);
		if (description != null)
		{
			base.append("(").append(description).append(")");
		}
		return base.toString();
	}

	/**
	 * Gets the HTTP Status Code.
	 */
	public int getHttpStatusCode()
	{
		return status;
	}

	/**
	 * Gets the error description.
	 */
	public String getDescription()
	{
		return description;
	}
}
