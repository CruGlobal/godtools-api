package org.cru.godtools.translate.client;

import com.google.common.collect.ForwardingMap;

import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

/**
 * Used to return a Map, representing a Set of translated strings referenced by unique identifiers
 * along with an HTTP status code.  The Set of translated strings corresponds to a page of GodTools
 * content
 *
 * Created by ryancarlson on 5/6/14.
 */
public abstract class TranslationResults extends ForwardingMap<UUID, String>
{
	protected int statusCode;
	protected Map<UUID, String> translatedStringsMap;

	@Override
	protected Map<UUID, String> delegate()
	{
		return translatedStringsMap;
	}


	public abstract TranslationResults createFromResponse(Response response);

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}
}
