package org.cru.godtools.domain.users;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by matthewfrederick on 7/31/14.
 */
public class UserRecord
{
	UUID id;
	String userId;
	String userName;
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

	public String getUserId()
	{
		return userId;
	}

	// RelayId will be parsed from CASReceipt.
	public void setUserId(String userId)
	{
		this.userId = userId;
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

	public String getUserName()
	{
		return userName;
	}

	// This will also be parsed from CASReceipt
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
}
