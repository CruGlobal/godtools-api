package org.cru.godtools.api.users;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by matthewfrederick on 7/31/14.
 */
public class UserRecord
{
	UUID id;
	DateTime grantedTimestamp;
	DateTime revokedTimestamp;
	UserLevel userLevel;

	private boolean isCurrentlyActive(DateTime currentTime)
	{
		if(!currentTime.isBefore(grantedTimestamp))
		{
			if(revokedTimestamp == null || currentTime.isBefore(revokedTimestamp))
				return  true;
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

	public UserLevel getUserLevel()
	{
		return userLevel;
	}

	public void setUserLevel(String userLevel)
	{
		this.userLevel = UserLevel.getUserLevel(userLevel, UserLevel.INVITED);
	}



}
