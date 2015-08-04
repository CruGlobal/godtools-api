package org.cru.godtools.domain.services;

import org.cru.godtools.domain.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by matthewfrederick on 1/5/15.
 */
public interface NotificationService
{
	List<Notification> selectAllUnsentNotifications();

	Notification selectNotificationByRegistrationIdAndType(Notification notification);

	void updateNotification(Notification notification);

	void insertNotification(Notification notification);

	void setNotificationAsSent(UUID id);

	void setAutoCommit(boolean autoCommit);

	void rollback();

}
