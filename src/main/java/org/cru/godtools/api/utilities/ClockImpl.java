package org.cru.godtools.api.utilities;

import org.ccci.util.time.Clock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.enterprise.inject.Produces;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class ClockImpl extends Clock
{
    @Override
    public DateTime currentDateTime()
    {
        return new DateTime(DateTimeZone.UTC);
    }
}
