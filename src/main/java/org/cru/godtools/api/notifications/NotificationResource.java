package org.cru.godtools.api.notifications;

import org.cru.godtools.api.authorization.AuthorizationResource;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.notifications.Device;
import org.cru.godtools.domain.notifications.DeviceService;
import org.cru.godtools.domain.notifications.Notification;
import org.cru.godtools.domain.notifications.NotificationService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/notification")
public class NotificationResource
{
	@Inject
	DeviceService deviceService;
	@Inject
	AuthorizationService authorizationService;
	@Inject
	NotificationService notificationService;

	Logger log = Logger.getLogger(AuthorizationResource.class);

	@POST
	@Path("/{registrationId}")
	public Response registerDevice(@PathParam("registrationId")String registrationId,
								   @HeaderParam("deviceId") String deviceIdHeader, @QueryParam("deviceId") String deviceIdParam,
								   @HeaderParam("notificationsOn") String notificationsHeader ,
								   @QueryParam("notificationsOn") String notificationsParam)
	{
		String id = deviceIdHeader == null ? deviceIdParam : deviceIdHeader;
		String notificationsOnString = notificationsHeader == null ? notificationsParam : notificationsHeader;

		log.info("Registering device: " + id + " with registrationId: " + registrationId);

		Boolean notificationsOn = true;
		if ("FALSE".equalsIgnoreCase(notificationsOnString)) notificationsOn = false;

		Device device = new Device();
		device.setId(UUID.randomUUID());
		device.setRegistrationId(registrationId);
		device.setDeviceId(id);
		device.setNotificationOn(notificationsOn);

		// if the device id already exists then just update the device.
		// this would be caused if a person uninstalls the app and re-installs it later.
		// this also allows us to use this endpoint to update a device when the notification state changes.
		if (deviceService.isDeviceRegistered(device.getDeviceId()))
		{
			deviceService.update(device);
		}
		else
		{
			deviceService.insert(device);
		}

		return Response.ok().build();
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateNotification(Notification notification, @HeaderParam(value="Authorization") String authcode)
	{
		log.info("Notification update for registrationId: " + notification.getRegistrationId());

		Notification originalNotification = notificationService.selectNotificationByRegistrationIdAndType(notification);

		/*
		 * Why is an insert and updated needed you may ask. The reason for this is for the items that need to be done several times
		 * for the notification to be sent. This will allow the api to keep track of the number of times a "presentation" has been made.
		 */
		if (originalNotification == null)
		{
			log.info("Creating new Notification");
			notification.setId(UUID.randomUUID());
			// we will not rely on the app to keep track of the number of presentations.
			notification.setPresentations(1);
			notificationService.insertNotification(notification);
		}
		else
		{
			if (!originalNotification.isNotificationSent())
			{
				log.info("Updating previous notification");
				notification.setId(originalNotification.getId());
				notification.setPresentations(originalNotification.getPresentations() + 1);
				notificationService.updateNotification(notification);
			}
		}

		return Response.noContent().build();
	}
}
