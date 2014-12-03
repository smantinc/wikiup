package org.wikiup.modules.quartz;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.text.ParseException;
import java.util.Iterator;

public class QuartzScheduleHandler implements ResourceHandler {
    public void handle(Resource resource) {
        Document doc = Documents.loadFromResource(resource);
        for(Document node : doc.getChildren("schedule")) {
            Document triggerNode = node.getChild("trigger");
            Document jobNode = node.getChild("job");

            Class<?> jobClass = Interfaces.getClass(Documents.getAttributeValue(jobNode, "job-class", null));
            String name = Documents.getId(jobNode, null);
            String group = Documents.getDocumentValue(jobNode, "group");

            JobDetail jobDetail = new JobDetail(name, group, jobClass);
            CronTrigger trigger = new CronTrigger(Documents.getDocumentValue(triggerNode, "name", null), Documents.getDocumentValue(triggerNode, "group", null));
            Scheduler scheduler = ScheduleManager.getInstance().getScheduler();
            Iterator<Document> properties = jobNode.getChildren("property").iterator();
            JobDataMap map = new JobDataMap();
            if(properties.hasNext()) {
                ContextUtil.setProperties(jobNode, new MapContext<Object>(map), Null.getInstance());
                jobDetail.setJobDataMap(map);
            }
            try {
                trigger.setCronExpression(Documents.getDocumentValue(triggerNode, "cron-expression", null));
                scheduler.scheduleJob(jobDetail, trigger);
            } catch(ParseException e) {
            } catch(SchedulerException e) {
            }
        }
    }

    public void finish() {
        try {
            ScheduleManager.getInstance().getScheduler().start();
        } catch(SchedulerException e) {
        }
    }
}
