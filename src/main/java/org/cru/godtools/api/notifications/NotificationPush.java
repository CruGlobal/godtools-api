package org.cru.godtools.api.notifications;

import org.ccci.util.time.Clock;
import org.cru.godtools.api.utilities.TimerControls;
import org.cru.godtools.domain.database.SqlConnectionProducer;
import org.cru.godtools.domain.notifications.Notification;
import org.cru.godtools.domain.notifications.NotificationService;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.jboss.logging.Logger;
import org.sql2o.Connection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by matthewfrederick on 1/5/15.
 */

@Singleton
@Startup
public class NotificationPush
{
	@Inject
	NotificationService notificationService;
	@Inject
	Connection sqlConnection;
	@Inject
	GodToolsProperties properties;
	@Inject
	Clock clock;
	@Inject
	TimerControls timerControls;

	@Resource
	TimerService timerService;

	Logger log = Logger.getLogger(NotificationPush.class);

	@PostConstruct
	public void initialize()
	{
		timerControls.createTimer(timerService, NotificationPush.class);
	}

	@Timeout
	public void execute()
	{
		log.info("Starting NotificationPush");

		if (properties == null)
		{
			properties = new GodToolsPropertiesFactory().get();
		}

		if (sqlConnection == null)
		{
			sqlConnection = new SqlConnectionProducer().getSqlConnection();
		}

		if (notificationService == null)
		{
			notificationService = new NotificationService();
		}

		try
		{
			List<Notification> notifications = notificationService.selectAllUnsentNotifications();

			for (Notification notification : notifications)
			{
				if (notification.isReadyForNotification(clock.currentDateTime()))
				{
					log.info("Creating message for notification type:" + notification.getNotificationType());

					String msg = getMessageForType(notification.getNotificationType());
					Message message = new Message.Builder().addData("msg", msg).addData("type", Integer.toString(notification.getNotificationType())).build();

					String apiKey = properties.getNonNullProperty("googleApiKey");

					Sender sender = new Sender(apiKey);

					try
					{
						Result result = sender.send(message, notification.getRegistrationId(), 2);
						log.info(result.getMessageId());

						notification.setNotificationSent(true);
						notificationService.updateNotification(notification);

						log.info("Notification: " + notification.getId() + "is sent: " + notification.isNotificationSent());
					}
					catch (Exception e)
					{
						log.error("Could not send notification with ID: " + notification.getId());
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		catch (Exception e)
		{
			log.error("Error trying to send notifications in NotificationPush");
			log.error(e.getMessage(), e);
		}
		finally
		{
			timerControls.updateTimer(timerService, NotificationPush.class);
		}
	}

	private String getMessageForType(int notificationType)
	{
		switch (notificationType)
		{
			// app not used for 2 weeks
			case Constants.NOT_USED_2_WEEKS:
				return "God is always at work around us. Who could you share your faith with today? Pray for those people right now.";

			// after 1 presentation of 4SL/KGP
			case Constants.AFTER_1_PRESENTATION:
				return "You took a faith step to share about Jesus. Way to go! Tell us what God is doing.";

			// after 10 presentation of 4SL/KGP
			case Constants.AFTER_10_PRESENTATIONS:
				return "Great job trusting God to share your faith! Tell us how we can pray for you.";

			// 24 hours after a share
			case Constants.DAY_AFTER_SHARE:
				return "Did you share your faith yesterday? Contact them and see what they are thinking.";

			// 2 days after downloading app
			case 5:
				return "Excited you want to share your faith through God Tools. Take some time today to review the materials, so that you're prepared when God provides a 'divine appointment' for you to share your faith!";

			// after 3 uses
			case Constants.AFTER_3_USES:
				return "Is God Tools helping you? Tell us how to make it better.";

		}

		log.warn("Message not found for notification type: " + notificationType);
		return null;
	}
}
