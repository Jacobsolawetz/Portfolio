import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
 
public class SimpleJob implements Job {
 
    public void execute(JobExecutionContext jec) throws JobExecutionException {
    	String[] args = {};
    	try {
			Executor.main(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
