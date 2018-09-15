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

public class sjf{

    ArrayList<job> jobList;
    TaskSeries information=new TaskSeries("Information");
    TaskSeriesCollection dataset = new TaskSeriesCollection();

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
    public sjf(ArrayList<job> jobList)
    {
        this.jobList = jobList;
       
    }

    public void execute ()
    {
        double currentTime=0;

        while(true)
        {
            job currentJob=findNextJob(jobList,currentTime);
            if(currentJob==null)
                break;
            Task taskForJob=new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod(0,5000));
            
            information.add(taskForJob);   
            double a=Math.max(currentTime,currentJob.timeGenerated);
            taskForJob.addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod( (long) (a*100),(long) ((a+currentJob.timeCompletion)*100))));
            currentJob.turnAround=a+currentJob.timeCompletion-currentJob.originalTimeGenerated;
            currentJob.responseTime=a-currentJob.originalTimeGenerated;

            currentJob.done=true;
            currentTime = a+currentJob.timeCompletion;
            
        }
        dataset.add(information);
        analysis();
    }

    public job findNextJob(ArrayList<job> jobList, double currentTime){
        if(currentTime==0){
            return jobList.get(0);
        }
        double minTime=10000;
        int indexMin=0;
        for(int i=1; i<jobList.size(); i++){
            job thisJob=jobList.get(i);
            if(thisJob.done==false){
                if(thisJob.timeGenerated<=currentTime){
                    double originalMin=minTime;
                    minTime=Math.min(minTime,thisJob.timeCompletion);
                    if(originalMin!=minTime)
                    indexMin=i;
                    
                }
                

            }
            else
            continue;

        }
        if(indexMin!=0)
            return jobList.get(indexMin);

        if(indexMin==0) //either all done then null
        {
            for(int i=0; i<jobList.size(); i++){
                if(jobList.get(i).done)
                continue;
                else 
                return jobList.get(i);
                
            }
            return null;
        }
        return null;
    }
}