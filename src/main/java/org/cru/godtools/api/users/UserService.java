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
				.addParameter("grantedTimestamp", userRecord.getGrantedTimestamp())
				.addParameter("revokedTimestamp", userRecord.getRevokedTimestamp())
				.addParameter("userLevel", userRecord.getUserLevel())
				.executeUpdate();
	}

	public UserRecord getUserRecord(UUID id)
	{
		return sqlConnection.createQuery(UserQueries.findUserRecord)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", id)
				.executeAndFetchFirst(UserRecord.class);
	}

	private class UserQueries
	{
		static final String insert = "INSERT INTO users(id, granted_timestamp, revoked_timestamp, user_level) VALUES(:id, :grantedTimestamp, :revokedTimestamp, :userLevel)";
		static final String findUserRecord = "SELECT * FROM users WHERE id = :id";
	}
}
