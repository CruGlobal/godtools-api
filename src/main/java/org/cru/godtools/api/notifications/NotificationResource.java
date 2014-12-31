package org.cru.godtools.api.notifications;

import org.cru.godtools.api.authorization.AuthorizationResource;
import org.cru.godtools.domain.notifications.Device;
import org.cru.godtools.domain.notifications.DeviceService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
@Path("/notification")
public class NotificationResource
{
	@Inject
	DeviceService deviceService;

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

		Message message = new Message.Builder().addData("test", "test").build();

		return Response.ok().build();
	}
}
