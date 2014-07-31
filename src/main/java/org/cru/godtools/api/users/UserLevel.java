package org.cru.godtools.api.users;

/**
 * Created by matthewfrederick on 7/31/14.
 */
public enum UserLevel
{
	INVITED("Invited"), APPROVED("Approved"), SUPERUSER("Superuser");

	private String userLevel;

	private UserLevel(String userLevel)
	{
		this.userLevel = userLevel;
	}

	public String toString()
	{
		return userLevel;
	}

	private static UserLevel getEnum(String string)
	{
		for(UserLevel level : values())
		{
			if(level.userLevel.equalsIgnoreCase(string))
				return level;
		}
		throw new IllegalArgumentException();
	}

	public static UserLevel getUserLevel(String string, UserLevel defaultLevel)
	{
		try
		{
			return getEnum(string);
		} catch (IllegalArgumentException e)
		{
			return defaultLevel;
		}
	}
}
