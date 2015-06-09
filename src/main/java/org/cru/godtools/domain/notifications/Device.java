package org.cru.godtools.domain.notifications;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
public class Device implements Serializable
{
	UUID id;
	String deviceId;
	String registrationId;
	Boolean notificationOn;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getRegistrationId()
	{
		return registrationId;
	}

	public void setRegistrationId(String registrationId)
	{
		this.registrationId = registrationId;
	}

	public Boolean getNotificationOn()
	{
		return notificationOn;
	}

	public void setNotificationOn(Boolean notificationOn)
	{
		this.notificationOn = notificationOn;
	}
}
