package org.cru.godtools.api.services.Sql2oStandard;

import org.cru.godtools.api.services.*;
import org.cru.godtools.domain.notifications.*;
import org.sql2o.*;

import javax.inject.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
public class Sql2oDeviceService implements DeviceService
{
    private Connection sqlConnection;

    @Inject
    public Sql2oDeviceService(Connection sqlConnection)
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

    public void insert(Device device)
    {
        sqlConnection.createQuery(deviceQueries.insert)
                .addParameter("id", device.getId())
                .addParameter("registrationId", device.getRegistrationId())
                .addParameter("deviceId", device.getDeviceId())
                .executeUpdate();
    }

    public static class deviceQueries
    {
        public final static String selectById = "SELECT * FROM devices WHERE id = :id";
        public final static String insert = "INSERT INTO devices(id, registration_id, device_id) " +
                "VALUES (:id, :registrationId, :deviceId)";
    }

    public Connection getSqlConnection()
    {
        return sqlConnection;
    }
}
