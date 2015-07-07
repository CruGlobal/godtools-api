package org.cru.godtools.domain.authentication;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by matthewfrederick on 7/14/14.
 */
@Entity
@Table(name="access_codes")
public class AccessCodeRecord
{
    @Id
    @Column(name="access_code")
    String accessCode;
    @Column(name="created_timestamp")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime createdTimestamp;
    @Column(name="revoked_timestamp")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime revokedTimestamp;
    @Column(name="admin")
	boolean admin;

    public boolean isCurrentlyActive(DateTime currentTime)
    {
        if(!currentTime.isBefore(createdTimestamp))
        {
            if(revokedTimestamp == null || currentTime.isBefore(revokedTimestamp)) return true;
        }
        return false;
    }

    public DateTime getCreatedTimestamp()
    {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp)
    {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getRevokedTimestamp()
    {
        return revokedTimestamp;
    }

    public void setRevokedTimestamp(DateTime revokedTimestamp)
    {
        this.revokedTimestamp = revokedTimestamp;
    }

    public String getCode()
    {
        return accessCode;
    }

    public void setCode(String code)
    {
        this.accessCode = code;
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
