package org.cru.godtools.api.notifications;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.cru.godtools.api.authorization.AuthorizationResource;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.domain.notifications.Device;
import org.cru.godtools.domain.notifications.DeviceService;
import org.cru.godtools.domain.notifications.Notification;
import org.cru.godtools.domain.notifications.NotificationService;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
@Path("/notification")
public class NotificationResource
{
	@Inject
	DeviceService deviceService;
	@Inject
	AuthorizationService authorizationService;
	@Inject
	NotificationService notificationService;

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

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

	@POST
	@Path("/multicast")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendMassNotification(String message, @HeaderParam(value="Authorization") String authcode) throws IOException
	{
		log.info("Send mass notification, authcode: " + authcode);

		// get authorization record for authcode
		Optional<AuthorizationRecord> authorizationRecord = authorizationService.getAuthorizationRecord(null, authcode);

		// user must be admin to send multicast
		if (!authorizationRecord.isPresent() || !authorizationRecord.get().isAdmin()) throw new UnauthorizedException();

		// make sure the message is not empty
		if (Strings.isNullOrEmpty(message)) throw new BadRequestException("Message is empty");

		// get all of the registration ids
		List<String> regIds = notificationService.getAllRegistrationIds();

		// create the message to be sent
		Message messageToSend = new Message.Builder().addData("msg", message).build();

		String apiKey = properties.getNonNullProperty("googleApiKey");

		Sender sender = new Sender(apiKey);

		MulticastResult result = sender.send(messageToSend, regIds, 2);
		log.info(result.getMulticastId());

		return Response.ok(result).build();
	}
}
