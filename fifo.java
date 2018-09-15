import java.util.*;
import javafx.util.Pair;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import java.text.SimpleDateFormat;
import org.jfree.data.time.SimpleTimePeriod;

public class fifo{

    ArrayList<job> jobList;
    TaskSeries information=new TaskSeries("Information");
    TaskSeriesCollection dataset = new TaskSeriesCollection();

    public fifo(ArrayList<job> jobList)
    {
        this.jobList = jobList;
       
    }

    public void analysis()
    {
        double turnAround=0d;
        double responseTime=0d;
        for(int i=0; i<jobList.size(); i++)
        {
            turnAround=turnAround+jobList.get(i).turnAround;
            responseTime=responseTime+jobList.get(i).responseTime;
        }
        System.out.println("Average turnaround time is " + turnAround/jobList.size());
        System.out.println("Average response time is " + responseTime/jobList.size());
    }
    public void execute ()
    {
        double currentTime = 0;
        int len = jobList.size();
        
        for (int i = 0;i<len;i++)
        {
            job currentJob=jobList.get(i);
            Task taskForJob=new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod(0,5000));
            information.add(taskForJob);
            if(i==0)
                { currentTime=currentJob.timeGenerated;
                taskForJob.addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod( (long)(currentJob.timeGenerated*100),(long) ((currentJob.timeGenerated+currentJob.timeCompletion)*100))));
                currentJob.turnAround=currentJob.timeGenerated+currentJob.timeCompletion-currentJob.originalTimeGenerated;
                currentJob.responseTime=currentTime-currentJob.originalTimeGenerated;
                currentTime= currentJob.timeGenerated+ currentJob.timeCompletion;
                //System.out.println("time scheduled is "+(long)currentJob.timeGenerated*100);
                //System.out.println("time finished " + (long)currentTime*100);
                
                continue;
                }
            else
            {
                if(currentTime<currentJob.timeGenerated){
                     //System.out.println("came less");
                     taskForJob.addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentJob.timeGenerated*100),(long) ((currentJob.timeGenerated+currentJob.timeCompletion)*100))));
                     currentJob.turnAround=currentJob.timeGenerated+currentJob.timeCompletion-currentJob.originalTimeGenerated;
                     currentJob.responseTime=currentJob.timeGenerated-currentJob.originalTimeGenerated;
                     currentTime= currentJob.timeGenerated+ currentJob.timeCompletion;
                     //System.out.println("time scheduled is is "+(long)currentJob.timeGenerated*100);
                     //System.out.println("time ended  is " + (long)currentTime*100);
                     
                     continue;
                }

                else if (currentTime>=currentJob.timeGenerated){
                    //System.out.println("came more");
                    taskForJob.addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100),(long) ((currentTime+ currentJob.timeCompletion)*100))));
                    //System.out.println("time scheduled is "+(long)currentTime*100);
                    currentJob.turnAround=currentTime+ currentJob.timeCompletion-currentJob.originalTimeGenerated;
                    currentJob.responseTime=currentTime-currentJob.originalTimeGenerated;
                    currentTime=currentTime+ currentJob.timeCompletion;
                    //System.out.println(" time ended is " + (long)currentTime*100);
                    
                    continue;
                }
            }
            
         
        }
        dataset.add(information);
        analysis();
    }
}