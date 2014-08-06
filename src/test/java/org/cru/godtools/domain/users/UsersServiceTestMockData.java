package org.cru.godtools.domain.users;

import org.testng.Assert;

/**
 * Created by matthewfrederick on 8/6/14.
 */
public class UsersServiceTestMockData
{
	public static void persistUser(UserService userService)
	{
		UserRecord userRecord = new UserRecord();
		userRecord.setId(UsersServiceTest.TEST_ID);
		userRecord.setUserId(UsersServiceTest.TEST_USER_ID);
		userRecord.setUserName(UsersServiceTest.TEST_USER_NAME);
		userRecord.setUserLevel(UserLevel.APPROVED.toString());

		userService.recordNewUser(userRecord);
	}

	public static void validateUserRecord(UserRecord userRecord)
	{
		Assert.assertNotNull(userRecord);
		Assert.assertEquals(userRecord.getId(), UsersServiceTest.TEST_ID);
		Assert.assertEquals(userRecord.getUserId(), UsersServiceTest.TEST_USER_ID);
		Assert.assertEquals(userRecord.getUserName(), UsersServiceTest.TEST_USER_NAME);
	}
}
