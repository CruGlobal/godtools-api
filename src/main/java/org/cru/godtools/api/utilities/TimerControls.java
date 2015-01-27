package org.cru.godtools.api.utilities;

import org.jboss.logging.Logger;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

/**
 * Created by matthewfrederick on 1/5/15.
 *
 * Copied from ERT class created by Ryan Carlson
 */
public class TimerControls
{
	private Logger log = Logger.getLogger(TimerControls.class);

	public void createTimer(TimerService timerService, Class<?> callingClass)
	{
		TimerConfig timerConfig = new TimerConfig(callingClass.getCanonicalName() + " Timer", false);

		ScheduleExpression scheduleExpression = buildScheduleExpression();

		timerService.createCalendarTimer(scheduleExpression, timerConfig);
		log.info("Created new timer with firing every hour on the hour from 8:00-22:00");

	}

	private ScheduleExpression buildScheduleExpression()
	{
		return new ScheduleExpression()
				.minute("3")
				.hour("8-22");
	}
}
