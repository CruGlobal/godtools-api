package org.cru.godtools.api;

import com.google.common.base.Optional;
import org.cru.godtools.domain.model.AuthorizationRecord;
import org.cru.godtools.domain.services.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.domain.model.Device;
import org.cru.godtools.domain.services.DeviceService;
import org.cru.godtools.domain.model.Notification;
import org.cru.godtools.domain.services.NotificationService;
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

/**
 * Created by matthewfrederick on 12/30/14.
 */
@Path("/notification")
public class NotificationResource
{
	@Inject
	private DeviceService deviceService;
	@Inject
	private AuthorizationService authorizationService;
	@Inject
	private NotificationService notificationService;

	Logger log = Logger.getLogger(AuthorizationResource.class);

	@POST
	@Path("/{registrationId}")
	public Response registerDevice(@PathParam("registrationId")String registrationId,
								   @HeaderParam("deviceId") String deviceIdHeader, @QueryParam("deviceId") String deviceIdParam)
	{
		String id = deviceIdHeader == null ? deviceIdParam : deviceIdHeader;

		log.info("Registering device: " + id + " with registrationId: " + registrationId);

		Device device = new Device();
		device.setId(UUID.randomUUID());
		device.setRegistrationId(registrationId);
		device.setDeviceId(id);

		deviceService.insert(device);

		return Response.ok().build();
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateNotification(Notification notification, @HeaderParam(value="Authorization") String authcode)
	{
		log.info("Notification update for registrationId: " + notification.getRegistrationId());

		Optional<AuthorizationRecord> authorizationRecord = authorizationService.getAuthorizationRecord(null, authcode);
		if (!authorizationRecord.isPresent()) throw new UnauthorizedException();

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

	public void setAutoCommit(boolean autoCommit)
	{
		deviceService.setAutoCommit(autoCommit);
		authorizationService.setAutoCommit(autoCommit);
		notificationService.setAutoCommit(autoCommit);
	}

	public void rollback()
	{
		deviceService.rollback();
		authorizationService.rollback();
		notificationService.rollback();
	}
}
