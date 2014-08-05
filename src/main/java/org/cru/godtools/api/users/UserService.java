package org.cru.godtools.api.users;

import org.ccci.util.time.Clock;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by matthewfrederick on 7/31/14.
 */
public class UserService
{
	Connection sqlConnection;
	Clock clock;

	@Inject
	public UserService(Connection sqlConnection, Clock clock)
	{
		this.sqlConnection = sqlConnection;
		this.clock = clock;
	}

	public void recordNewUser(UserRecord userRecord)
	{
		sqlConnection.createQuery(UserQueries.insert)
				.addParameter("id", userRecord.getId())
				.addParameter("userId", userRecord.getUserId())
				.addParameter("userName", userRecord.getUserName())
				.addParameter("grantedTimestamp", userRecord.getGrantedTimestamp())
				.addParameter("revokedTimestamp", userRecord.getRevokedTimestamp())
				.addParameter("userLevel", userRecord.getUserLevel())
				.executeUpdate();
	}

	public UserRecord getUserRecordByUUID(UUID id)
	{
		return sqlConnection.createQuery(UserQueries.findUserRecordByUUID)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", id)
				.executeAndFetchFirst(UserRecord.class);
	}

	public UserRecord getUserRecordByRelayId(String relayId)
	{
		return sqlConnection.createQuery(UserQueries.findUserRecordByRelayId)
				.setAutoDeriveColumnNames(true)
				.addParameter("userId", relayId)
				.executeAndFetchFirst(UserRecord.class);
	}

	private class UserQueries
	{
		static final String insert = "INSERT INTO users(id, user_id, user_name, granted_timestamp, revoked_timestamp, user_level) VALUES(:id, :userId, :userName, :grantedTimestamp, :revokedTimestamp, :userLevel)";
		static final String findUserRecordByUUID = "SELECT * FROM users WHERE id = :id";
		static final String findUserRecordByRelayId = "SELECT * FROM users WHERE relay_id = :userId";
	}
}
