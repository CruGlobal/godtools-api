package org.cru.godtools.api.translations.drafts;

import com.google.common.collect.Sets;
import org.cru.godtools.domain.translations.Translation;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Set;

/**
 * Created by ryancarlson on 9/24/14.
 */
public class DraftUpdateJobScheduler
{
	public static void scheduleOneUpdate(Integer projectId, String locale, Set<String> pageNames, Translation translation) throws SchedulerException
	{
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		JobDetail jobDetail = buildJob(projectId, locale, pageNames, translation);

		Trigger trigger = buildTrigger(translation, 0, 0);

		scheduler.scheduleJob(jobDetail, Sets.newHashSet(trigger), true);

		if(!scheduler.isStarted()) scheduler.start();
	}

	public static void scheduleRecurringUpdate(Integer projectId, String locale, Set<String> pageNames, Translation translation) throws SchedulerException
	{
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		JobDetail jobDetail = buildJob(projectId, locale, pageNames, translation);

		Trigger trigger = buildTrigger(translation, 30, 10);

		scheduler.scheduleJob(jobDetail, Sets.newHashSet(trigger), true);

		if(!scheduler.isStarted()) scheduler.start();
	}

	private static JobDetail buildJob(Integer projectId, String locale, Set<String> pageNames, Translation translation)
	{
		return JobBuilder.newJob(DraftUpdateJob.class)
					.withIdentity(translation.getId().toString())
					.usingJobData(buildJobData(projectId, locale, pageNames, translation))
					.build();
	}

	private static Trigger buildTrigger(Translation translation, int interval, int repeatCount)
	{
		return TriggerBuilder.newTrigger()
					.withIdentity(translation.getId().toString())
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(interval)
							.withRepeatCount(repeatCount))
					.build();
	}

	private static JobDataMap buildJobData(Integer projectId, String locale, Set<String> pageNames, Translation translation)
	{
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(DraftUpdateJob.PROJECT_ID_KEY, projectId);
		jobDataMap.put(DraftUpdateJob.LOCALE_KEY, locale);
		jobDataMap.put(DraftUpdateJob.TRANSLATION_KEY, translation);
		jobDataMap.put(DraftUpdateJob.PAGE_NAME_SET_KEY, pageNames);

		return jobDataMap;
	}


}
