package org.cru.godtools.domain.authentication;

import com.google.common.base.Optional;
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
	boolean admin;

	public static void checkAuthorization(Optional<AuthorizationRecord> authorizationRecordOptional, DateTime currentTime)
	{
		if(!authorizationRecordOptional.isPresent()) throw new UnauthorizedException();
		if(!authorizationRecordOptional.get().isCurrentlyActive(currentTime)) throw new UnauthorizedException();
	}

	public static void checkAccessToDrafts(Optional<AuthorizationRecord> authorizationRecordOptional, DateTime currentTime)
	{
		checkAuthorization(authorizationRecordOptional, currentTime);
		if(!authorizationRecordOptional.get().hasDraftAccess()) throw new UnauthorizedException();
	}

    public boolean hasDraftAccess(DateTime currentTime)
    {
        return hasDraftAccess() && isCurrentlyActive(currentTime);
    }

    public static void checkAdminAccess(Optional<AuthorizationRecord> authorizationRecordOptional, DateTime currentTime)
    {
        checkAuthorization(authorizationRecordOptional, currentTime);
        if(!authorizationRecordOptional.get().isAdmin())  throw new UnauthorizedException();
    }

    private boolean isCurrentlyActive(DateTime currentTime)
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

	public boolean isAdmin()
	{
		return admin;
	}

	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}
}
