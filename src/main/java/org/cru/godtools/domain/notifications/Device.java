package org.cru.godtools.domain.notifications;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
@Entity
@Table(name="devices")
public class Device implements Serializable
{
	@Id
	@Column(name="id")
	@Type(type="pg-uuid")
	UUID id;
	@Column(name="device_id")
	String deviceId;
	@Column(name="registration_id")
	String registrationId;
	//DateTime createdTimestamp;
		//Field not currently stored in DB?

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
}
