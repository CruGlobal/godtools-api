package org.cru.godtools.api.utilities;

import org.cru.godtools.api.notifications.NotificationPush;
import org.jboss.logging.Logger;

import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

/**
 * Created by matthewfrederick on 1/5/15.
 *
 * Copied from ERT class created by Ryan Carlson
 */
public class TimerControls
{
	private final int baseIntervalSeconds = 30; // one hour
	private int currentIntervalSeconds = 30;

	private boolean backoffRequired;

	private Logger log = Logger.getLogger(TimerControls.class);

	public void createTimer(TimerService timerService, Class<?> callingClass)
	{
		TimerConfig timerConfig = new TimerConfig(callingClass.getCanonicalName() + " Timer", false);

		ScheduleExpression scheduleExpression = buildScheduleExpression();

		timerService.createCalendarTimer(scheduleExpression, timerConfig);
		log.info(String.format("Created new timer with interval: %s", currentIntervalSeconds));

	}

	private ScheduleExpression buildScheduleExpression()
	{
		if(currentIntervalSeconds <= 60)
		{
			return new ScheduleExpression()
					.second("*/" + currentIntervalSeconds)
					.minute("*")
					.hour("*");
		}
		else
		{
			return new ScheduleExpression()
					.second("*/60")
					.minute("*/" + currentIntervalSeconds / 60)
					.hour("*");
		}
	}

	public void updateTimer(TimerService timerService, Class<?> callingClass)
	{
		if(backoffRequired)
		{
			backoffTimer(timerService, callingClass);
		}
		else
		{
			restoreTimer(timerService, callingClass);
		}
	}

	public void requireBackoff()
	{
		backoffRequired = true;
	}

	private void restoreTimer(TimerService timerService, Class<?> callingClass)
	{
		log.info("Restoring timer to original interval.");
		currentIntervalSeconds = baseIntervalSeconds;
		for(Timer timer : timerService.getTimers())
		{
			if(timer.getInfo().equals(NotificationPush.class.getCanonicalName() + " Timer"))
			{
				timer.cancel();
				createTimer(timerService, callingClass);

			}
		}
	}

	private void backoffTimer(TimerService timerService, Class<?> callingClass)
	{
		log.info("Backing off timer.");
		calculateBackoff();
		for(Timer timer : timerService.getTimers())
		{
			if(timer.getInfo().equals(NotificationPush.class.getCanonicalName() + " Timer"))
			{
				timer.cancel();
				createTimer(timerService, callingClass);
			}
		}
		backoffRequired = false;
	}

	private int calculateBackoff()
	{
		if(currentIntervalSeconds >= 480)
		{
			return currentIntervalSeconds;
		}
		else
		{
			return currentIntervalSeconds *= 2;
		}
	}
}
