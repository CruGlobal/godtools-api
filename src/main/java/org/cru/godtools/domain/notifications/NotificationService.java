package org.cru.godtools.domain.notifications;

import org.ccci.util.time.Clock;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by matthewfrederick on 1/5/15.
 */
public class NotificationService
{
	@Inject
	Connection sqlConnection;

	@Inject
		 public NotificationService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	@Inject
	Clock clock;

	public List<Notification> selectAllUnsentNotifications()
	{
		return sqlConnection.createQuery(notificationQueries.selectAllUnsent)
				.setAutoDeriveColumnNames(true)
				.executeAndFetch(Notification.class);
	}

	public Notification selectNotificationByRegistrationIdAndType(Notification notification)
	{
		return sqlConnection.createQuery(notificationQueries.selectNotificationByRegistrationIdAndType)
				.setAutoDeriveColumnNames(true)
				.addParameter("registrationId", notification.getRegistrationId())
				.addParameter("notificationType", notification.getNotificationType())
				.executeAndFetchFirst(Notification.class);
	}

	public void updateNotification(Notification notification)
	{
		sqlConnection.createQuery(notificationQueries.updateNotification)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", notification.getId())
				.addParameter("registrationId", notification.registrationId)
				.addParameter("notificationType", notification.getNotificationType())
				.addParameter("presentations", notification.getPresentations())
				.addParameter("notificationSent", notification.isNotificationSent())
				.addParameter("timestamp", clock.currentDateTime())
				.executeUpdate();
	}

	public void insertNotification(Notification notification)
	{
		sqlConnection.createQuery(notificationQueries.insertNotification)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", notification.getId())
				.addParameter("registrationId", notification.registrationId)
				.addParameter("notificationType", notification.getNotificationType())
				.addParameter("presentations", notification.getPresentations())
				.addParameter("notificationSent", notification.isNotificationSent())
				.addParameter("timestamp", clock.currentDateTime())
				.executeUpdate();
	}

	public void setNotificationAsSent(UUID id)
	{
		sqlConnection.createQuery(notificationQueries.setNotificationAsSent)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", id)
				.executeUpdate();
	}

	public static class notificationQueries
	{
		public final static String selectAllUnsent = "SELECT * FROM notifications WHERE notification_sent = 'f'";
		public final static String insertNotification = "INSERT INTO notifications (id, registration_id, notification_type, presentations, notification_sent, timestamp)" +
				"VALUES (:id, :registrationId, :notificationType, :presentations, :notificationSent, :timestamp)";
		public final static String updateNotification = "UPDATE notifications SET " +
				"registration_id = :registrationId, " +
				"notification_type = :notificationType, " +
				"presentations = :presentations, " +
				"notification_sent = :notificationSent, " +
				"timestamp = :timestamp " +
				"WHERE id = :id";
		public final static String selectNotificationByRegistrationIdAndType = "SELECT * FROM notifications WHERE registration_id = :registrationId AND notification_type = :notificationType";
		public final static String setNotificationAsSent = "UPDATE notifications SET notification_sent = 't' WHERE id = :id";
	}
}
