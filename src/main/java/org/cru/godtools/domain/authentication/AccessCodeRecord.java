package org.cru.godtools.domain.authentication;

import org.joda.time.DateTime;

/**
 * Created by matthewfrederick on 7/14/14.
 */
public class AccessCodeRecord
{

    String accessCode;
    DateTime createdTimestamp;
    DateTime revokedTimestamp;
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
