package coyote.commons.snap;

import coyote.commons.ClasspathUtil;
import coyote.commons.CronEntry;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.job.CronJob;
import coyote.commons.job.ScheduledJob;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to load and configure jobs.
 */
public class JobLoader {

    /**
     * Load a job from the given configuration.
     *
     * @param config the configuration to use
     * @return a configured job
     * @throws ConfigurationException if there are configuration errors
     */
    public static SnapJob loadJob(Config config) throws ConfigurationException {
        SnapJob retval = null;
        String className = null;
        Config jobConfig = null;

        if (config.containsIgnoreCase(ConfigTag.CLASS)) {
            className = config.getString(ConfigTag.CLASS);
            jobConfig = config.getSection(ConfigTag.CONFIGURATION);
            if (jobConfig == null) {
                jobConfig = config;
            }
        } else {
            // Fallback to legacy behavior: first attribute name is the class name
            DataField configField = config.getField(0);
            if (configField != null && StringUtil.isNotEmpty(configField.getName())) {
                className = configField.getName();
                if (configField.isFrame()) {
                    jobConfig = new Config((DataFrame) configField.getObjectValue());
                }
            }
        }

        if (StringUtil.isEmpty(className)) {
            throw new ConfigurationException("Job configuration must specify a class");
        }

        // If the class name is not fully qualified, attempt to resolve it
        if (className.indexOf('.') < 1) {
            List<String> names = ClasspathUtil.resolve(className);
            if (!names.isEmpty()) {
                className = names.get(0);
            } else {
                // Last resort: assume it's in the coyote package (legacy)
                className = "coyote." + className;
            }
        }

        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();

            if (object instanceof SnapJob) {
                retval = (SnapJob) object;
                if (jobConfig != null) {
                    retval.configure(jobConfig);
                }
            } else {
                throw new ConfigurationException("Class is not a SnapJob: " + className);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                 | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ConfigurationException("Could not instantiate job class: " + className + " - " + e.getMessage(), e);
        }

        return retval;
    }


    /**
     * Load multiple jobs from the given configuration.
     *
     * <p>This looks for a "Job" section which can be a single job frame or an
     * array of job frames. Each job is loaded and optionally wrapped in a
     * ScheduledJob if a "Schedule" section is present.</p>
     *
     * @param cfg the configuration to use
     * @return a list of scheduled jobs
     * @throws ConfigurationException if there are configuration errors
     */
    public static List<ScheduledJob> loadJobs(Config cfg) throws ConfigurationException {
        List<ScheduledJob> retval = new ArrayList<>();
        if (cfg.containsIgnoreCase(ConfigTag.JOB)) {
            for (Config jobSection : cfg.getSections(ConfigTag.JOB)) {
                if (jobSection.isArray()) {
                    for (DataField field : jobSection.getFields()) {
                        if (field.isFrame()) {
                            Config jobCfg = new Config((DataFrame) field.getObjectValue());
                            retval.add(createScheduledJob(jobCfg));
                        }
                    }
                } else {
                    retval.add(createScheduledJob(jobSection));
                }
            }
        } else {
            // Try loading it as a single job
            retval.add(createScheduledJob(cfg));
        }
        return retval;
    }


    private static ScheduledJob createScheduledJob(Config config) throws ConfigurationException {
        ScheduledJob retval = null;
        Config jobConfig = config;

        // If the config has a first field that is a frame and it's not "schedule", 
        // "server", etc., it might be the job class name (legacy format)
        if (!config.containsIgnoreCase(ConfigTag.CLASS) && config.getFieldCount() > 0) {
            DataField field = config.getField(0);
            if (field.isFrame() && !ConfigTag.SCHEDULE.equalsIgnoreCase(field.getName())) {
                jobConfig = new Config((DataFrame) field.getObjectValue());
            }
        }

        List<Config> scheduleConfig = jobConfig.getSections(ConfigTag.SCHEDULE);
        if (scheduleConfig.isEmpty()) {
            // also check the top level config in case the job is wrapped but schedule is outside
            scheduleConfig = config.getSections(ConfigTag.SCHEDULE);
        }

        if (scheduleConfig.size() > 0) {
            Config scheduleCfg = scheduleConfig.get(0);
            if (scheduleCfg.containsIgnoreCase(ConfigTag.MILLIS)) {
                long millis = scheduleCfg.getLong(ConfigTag.MILLIS, 1000);
                retval = new ScheduledJob();
                retval.setExecutionInterval(millis);
                retval.setRepeatable(true);
            } else {
                CronJob cronJob = new CronJob();
                cronJob.setCronEntry(parseSchedule(scheduleCfg));
                retval = cronJob;
            }
        } else {
            retval = new ScheduledJob();
            retval.setRepeatable(config.getAsBoolean(ConfigTag.REPEAT, false));
        }

        SnapJob snapJob = loadJob(config);
        if (config.containsIgnoreCase(ConfigTag.NAME)) {
            snapJob.setName(config.getString(ConfigTag.NAME));
        }
        retval.setWork(new SnapJobRunner(snapJob, retval));
        return retval;
    }


    public static CronEntry parseSchedule(Config scheduleCfg) throws ConfigurationException {
        CronEntry cronentry = new CronEntry();
        for (DataField field : scheduleCfg.getFields()) {
            if (ConfigTag.PATTERN.equalsIgnoreCase(field.getName())) {
                try {
                    cronentry = CronEntry.parse(field.getStringValue());
                } catch (ParseException e) {
                    Log.error(String.format("Problems parsing Schedule pattern: %s", e.getMessage()));
                }
            } else if (ConfigTag.MINUTES.equalsIgnoreCase(field.getName())) {
                cronentry.setMinutePattern(field.getStringValue());
            } else if (ConfigTag.HOURS.equalsIgnoreCase(field.getName())) {
                cronentry.setHourPattern(field.getStringValue());
            } else if (ConfigTag.MONTHS.equalsIgnoreCase(field.getName())) {
                cronentry.setMonthPattern(field.getStringValue());
            } else if (ConfigTag.DAYS.equalsIgnoreCase(field.getName())) {
                cronentry.setDayPattern(field.getStringValue());
            } else if (ConfigTag.DAYS_OF_WEEK.equalsIgnoreCase(field.getName())) {
                cronentry.setDayOfWeekPattern(field.getStringValue());
            }
        }
        return cronentry;
    }


    /**
     * A wrapper for SnapJob instances to allow them to be executed by the scheduler.
     */
    private static class SnapJobRunner implements Runnable {
        SnapJob snapJob;
        ScheduledJob scheduledJob;

        public SnapJobRunner(SnapJob snapJob, ScheduledJob scheduledJob) {
            this.snapJob = snapJob;
            this.scheduledJob = scheduledJob;
        }

        @Override
        public void run() {
            try {
                snapJob.start();
            } catch (Throwable e) {
                String jobName = snapJob.getName();
                if (StringUtil.isEmpty(jobName)) {
                    jobName = snapJob.getClass().getSimpleName();
                }

                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                e.printStackTrace(new java.io.PrintWriter(out, true));
                Log.error(String.format("Job '%s' threw an exception and terminated: %s\n%s", jobName, e.getMessage(), out.toString()));
            } finally {
                if (scheduledJob != null) {
                    scheduledJob.setActiveFlag(false);
                }
            }
        }
    }
}
