package org.cru.godtools.domain.notifications;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
public class DeviceService
{
	Connection sqlConnection;

	@Inject
	public DeviceService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	public Device selectById(UUID id)
	{
		return sqlConnection.createQuery(deviceQueries.selectById)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", id)
				.executeAndFetchFirst(Device.class);
	}

	public boolean isDeviceRegistered(String deviceId)
	{
		return (sqlConnection.createQuery(deviceQueries.selectByDeviceId)
				.setAutoDeriveColumnNames(true)
				.addParameter("deviceId", deviceId)
				.executeAndFetchFirst(Device.class) != null);
	}

	public void insert(Device device)
	{
		sqlConnection.createQuery(deviceQueries.insert)
				.addParameter("id", device.getId())
				.addParameter("registrationId", device.getRegistrationId())
				.addParameter("deviceId", device.getDeviceId())
				.executeUpdate();
	}

	public void update(Device device)
	{
		sqlConnection.createQuery(deviceQueries.update)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", device.getId())
				.addParameter("registration_id", device.getRegistrationId())
				.executeUpdate();
	}

	public static class deviceQueries
	{
		public final static String selectById = "SELECT * FROM devices WHERE id = :id";
		public final static String selectByDeviceId = "SELECT * FROM devices WHERE device_id = :deviceId";
		public final static String insert = "INSERT INTO devices(id, registration_id, device_id) " +
				"VALUES (:id, :registrationId, :deviceId)";
		public final static String update = "UPDATE devices SET " +
				"id = :id, " +
				"registration_id = :registrationId " +
				"WHERE device_id = :deviceId";
	}
}
