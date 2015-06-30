package org.cru.godtools.domain.services;

import org.cru.godtools.domain.notifications.*;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
public interface DeviceService
{
	Device selectById(UUID id);

	void insert(Device device);

	void setAutoCommit(boolean autoCommit);

	void rollback();
}
