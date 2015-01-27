package org.cru.godtools.domain.notifications;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by matthewfrederick on 1/5/15.
 */
public class Notification implements Serializable
{
	UUID id;
	String registrationId;
	DateTime timestamp;
	int presentations;
	int notificationType;
	boolean notificationSent;

	public boolean isReadyForNotification(DateTime dateTime)
	{
		/*
		 * The api will take each saved eligible notification from the db and see it if is
		 * time to send notification. Data for each notification will be saved by the phone
		 */

		// only send notification once
		if (notificationSent) return false;

		switch (notificationType)
		{
			// app not used for 2 weeks
			case 1:
				return dateTime.isAfter(timestamp.plusWeeks(2));

			// after 1 presentation of 4SL/KGP
			case 2:
				return presentations > 0;

			// after 10 presentation of 4SL/KGP
			case 3:
				return presentations > 9;

			// 24 hours after a share
			case 4:
				return dateTime.isAfter(timestamp.plusHours(24));

			// 2 days after downloading app
			case 5:
				return dateTime.isAfter(timestamp.plusDays(2));

			// after 3 uses
			case 6:
				return presentations > 2;

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

	public String getRegistrationId()
	{
		return registrationId;
	}

	public void setRegistrationId(String registrationId)
	{
		this.registrationId = registrationId;
	}

	public DateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public int getPresentations()
	{
		return presentations;
	}

	public void setPresentations(int presentations)
	{
		this.presentations = presentations;
	}

	public int getNotificationType()
	{
		return notificationType;
	}

	public void setNotificationType(int notificationType)
	{
		this.notificationType = notificationType;
	}

	public boolean isNotificationSent()
	{
		return notificationSent;
	}

	public void setNotificationSent(boolean notificationSent)
	{
		this.notificationSent = notificationSent;
	}
}
