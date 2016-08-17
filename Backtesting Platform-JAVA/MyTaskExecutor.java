import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.CronScheduleBuilder.*;  
 
public class MyTaskExecutor {
    
	
	public static void main( String[] args ) throws SchedulerException
    {
    	String log4jConfPath = "/Users/14price/Documents/workspace/AutomatedExecution/log4j.properties";
    	PropertyConfigurator.configure(log4jConfPath);
    	
    	JobDetail job = JobBuilder
                .newJob(SimpleJob.class)
                .withIdentity("SimpleJob")
                .build();
         
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("SimpleJob")
                .withSchedule(cronSchedule("0 25 14 ? * MON-FRI"))
                .build();
         
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job,trigger);
    }
}