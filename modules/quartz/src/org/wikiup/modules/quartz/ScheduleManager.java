package org.wikiup.modules.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Releasable;

public class ScheduleManager extends WikiupDynamicSingleton<ScheduleManager> implements Releasable {
    private Scheduler scheduler;

    static public ScheduleManager getInstance() {
        return getInstance(ScheduleManager.class);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void release() {
        try {
            scheduler.shutdown();
        } catch(SchedulerException e) {
        }
    }

//  public void cloneFrom(ScheduleManager instance)
//  {
//    this.scheduler = instance.scheduler;
//  }

    public void firstBuilt() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch(SchedulerException e) {
        }
    }
}