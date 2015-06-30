package org.cru.godtools.domain.services;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.notifications.*;
import org.sql2o.Connection;

import javax.inject.Inject;
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
