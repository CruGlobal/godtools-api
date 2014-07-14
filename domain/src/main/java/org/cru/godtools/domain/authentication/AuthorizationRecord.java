package org.cru.godtools.domain.authentication;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class AuthorizationRecord
{
    UUID id;
    String username;
    String authToken;
    DateTime grantedTimestamp;
    DateTime revokedTimestamp;
    String deviceId;
	boolean draftAccess;

    public boolean isCurrentlyActive(DateTime currentTime)
    {
        if(!currentTime.isBefore(grantedTimestamp))
        {
            if(revokedTimestamp == null || currentTime.isBefore(revokedTimestamp)) return true;
        }
        return false;
    }
    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public DateTime getGrantedTimestamp()
    {
        return grantedTimestamp;
    }

    public void setGrantedTimestamp(DateTime grantedTimestamp)
    {
        this.grantedTimestamp = grantedTimestamp;
    }

    public DateTime getRevokedTimestamp()
    {
        return revokedTimestamp;
    }

    public void setRevokedTimestamp(DateTime revokedTimestamp)
    {
        this.revokedTimestamp = revokedTimestamp;
    }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

	public boolean hasDraftAccess()
	{
		return draftAccess;
	}

	public void setDraftAccess(boolean draftAccess)
	{
		this.draftAccess = draftAccess;
	}
}
