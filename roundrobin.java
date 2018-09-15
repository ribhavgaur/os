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
import java.util.Queue;
 
public class roundrobin{

    ArrayList<job> jobList;
    double timeSlice;
    TaskSeries information=new TaskSeries("Information");
    TaskSeriesCollection dataset = new TaskSeriesCollection();
    HashMap<Integer,Task> map=new HashMap<Integer,Task>();
    Queue<job> q = new LinkedList<>(); 

      public void analysis()
    {
        double averageTurnAroundTime=0d;
        double responseTime=0d;
         for(int key : map.keySet())
        {
            Task currentTask=map.get(key);
            int count=currentTask.getSubtaskCount();
            count=count-1;
            averageTurnAroundTime=averageTurnAroundTime+(((SimpleTimePeriod) currentTask.getSubtask(count).getDuration()).getEndMillis()/100d)-jobList.get(key-1).originalTimeGenerated;
            responseTime=responseTime+ (((SimpleTimePeriod) currentTask.getSubtask(0).getDuration()).getStartMillis()/100d)-jobList.get(key-1).originalTimeGenerated;
            
        }
        System.out.println("Average turnaround time is " + averageTurnAroundTime/jobList.size());
        System.out.println("Average response time is " + responseTime/jobList.size());
        return;
    }

    public roundrobin(ArrayList<job> jobList,double a)
    {
        this.jobList = jobList;
        this.timeSlice=a;
        for(int i=0; i<jobList.size(); i++){
            map.put(jobList.get(i).pid,new Task(Integer.toString(jobList.get(i).pid),new SimpleTimePeriod(0,5000)));
            
        }
       
    }

    public void execute ()
    {
        double currentTime=0;
        currentTime=jobList.get(0).timeGenerated;
       
        while(true){
            if(allDone())
            break;

        
        double sliceEndTime=currentTime+timeSlice;
        addToQueue(sliceEndTime,jobList); //true krke add
        if(q.size()==0)
            continue;
        job currentJob=q.remove();
        //System.out.println("Current time is " + currentTime);
        //System.out.println("NOW EXECUTING JOB" + currentJob.pid);
        double finishTime=currentTime+currentJob.timeCompletion;
        //System.out.println("Slice end  time is " + sliceEndTime);
        //System.out.println("finish time is " + finishTime);
        if(finishTime<=sliceEndTime)
        {
                currentJob.done=true;
                map.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(finishTime*100))));
                //System.out.println((long) (currentTime*100) + ",, " + (long)(finishTime*100));
                currentTime=finishTime;
                currentJob.addToQueue=false;
        }
        else if (finishTime>sliceEndTime)
        {
                map.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(sliceEndTime*100))));
                //System.out.println((long) (currentTime*100) + ",,"+(long)(sliceEndTime*100));
                currentJob.timeGenerated=sliceEndTime;
                currentJob.timeCompletion=currentJob.timeCompletion-(sliceEndTime-currentTime);
                currentTime=sliceEndTime;
                currentJob.addToQueue=false;
        }

        }
        for(int key : map.keySet())
        {
            information.add(map.get(key));
        }
        analysis();
        dataset.add(information);

        
    }

    public boolean allDone()
    {
        for(int i=0; i<jobList.size(); i++)
        {
           if(!jobList.get(i).done)
           return false;
        }
        return true;
    }

    public void addToQueue(double sliceEndTime,ArrayList<job>jobList)
    {
        for(int i=0; i<jobList.size(); i++)
        {
            job current=jobList.get(i);
            if(!current.addToQueue && !current.done && current.timeGenerated<=sliceEndTime)
            {
                q.add(current);
                current.addToQueue=true;
            }
            else continue;
        }
        return;
        
    }
    

}