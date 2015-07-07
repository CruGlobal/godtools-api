package org.cru.godtools.domain.authentication;

import com.google.common.base.Optional;
import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/26/14.
 */
@Entity
@Table(name="auth_tokens")
public class AuthorizationRecord
{
    @Column(name="id")
    @Type(type="pg-uuid")
    UUID id;
    @Column(name="username")
    String username;
    @Id
    @Column(name="auth_token")
    String authToken;
    @Column(name="granted_timestamp")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    DateTime grantedTimestamp;
    @Column(name="revoked_timestamp")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    DateTime revokedTimestamp;
    @Column(name="device_id")
    String deviceId;
    @Column(name="draft_access")
    boolean draftAccess;
    @Column(name="admin")
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

    //public String getId() { return id.toString(); }

    public void setId(UUID id)
    {
        this.id = id;
    }

    //public void setId(String id) { this.id = UUID.fromString(id); }

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
