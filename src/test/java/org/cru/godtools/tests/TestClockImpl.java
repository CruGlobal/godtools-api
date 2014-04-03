package org.cru.godtools.tests;

import org.ccci.util.time.Clock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class TestClockImpl extends Clock
{
	@Override
	public DateTime currentDateTime()
	{
		return new DateTime(DateTimeZone.UTC).withDate(2014, 1, 1).withMillisOfDay(0);
	}
}
