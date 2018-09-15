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

public class sjcf{
    ArrayList<job> jobList;
    TaskSeries information=new TaskSeries("Information");
    TaskSeriesCollection dataset = new TaskSeriesCollection();
    HashMap<Integer,Task> map=new HashMap<Integer,Task>();

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

     public sjcf(ArrayList<job> jobList)
    {
        this.jobList = jobList;
        for(int i=0; i<jobList.size(); i++){
            map.put(jobList.get(i).pid,new Task(Integer.toString(jobList.get(i).pid),new SimpleTimePeriod(0,5000)));
            
        }
       
    }

   public void printList(){
    for(int i=0; i<jobList.size(); i++){
        System.out.println("Job description: "+jobList.get(i).pid + ", " + jobList.get(i).done);

    }
   }

    public void execute()
    {
        double currentTime=0d;
        currentTime=jobList.get(0).timeGenerated;
        int clashIndex;
        job currentJob=jobList.get(0);
        double clashTime;


        while(true){
            if(allDone())
            break;
            //System.out.println("came here");
            //printList();
            clashIndex=nextClash(currentJob.timeCompletion+currentTime,currentJob.pid,currentJob.timeGenerated);
            if(clashIndex<0){ ////means this is over
            double finishTime=currentTime+currentJob.timeCompletion;
            map.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(finishTime*100))));
            currentJob.done=true;
            int a=areAnyLeft(finishTime); 
            if(a==-1){ //means none left behind
                currentJob=getNextJob(finishTime); // gets job whose arrival is acfter finish time and undone if null then break loop
                if(currentJob==null)
                    break;
                currentTime=currentJob.timeGenerated;
                continue;
            }
            else
            {
                sendAllJobsAhead(finishTime); //time generated is set at finish tiume for all undone jobs that have come before finish time
                currentJob=getMinJob(finishTime); //===
                currentTime=currentJob.timeGenerated;
                continue;
            }
            
            }
            else{
                clashTime=jobList.get(clashIndex).timeGenerated;
                //System.out.println("clash time is " + clashTime + " and clashIndex is " + clashIndex);
                map.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(clashTime*100))));
                currentJob.timeGenerated=clashTime;
                //currentJob.done=false;
                currentJob.timeCompletion=currentJob.timeCompletion+currentTime-clashTime;
                int a=areAnyLeft(clashTime);
                if(a==-1){
                    //System.out.println("came to anyleft");
                      currentJob=getMinJob(clashTime); // at (...) time give min =====
                      //System.out.print("job chosen is " + currentJob.pid) + " and current time ios " + currentTime);
                      currentTime=currentJob.timeGenerated;
                     //System.out.println("job chosen is " + currentJob.pid + " and current time ios " + currentTime);
                      continue;

                }
                else{
                    //System.out.println("came to else");
                    sendAllJobsAhead(clashTime);
                    currentJob=getMinJob(clashTime);
                    currentTime=currentJob.timeGenerated;
                    //System.out.println("job chosen is " + currentJob.pid + " and current time ios " + currentTime);
                    continue;
                }
                

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

    public int nextClash(double expectedCompletionTime, int pid,double lowerBound)
    {
       int index=-1;
       double min=100000;

        for(int i=0; i<jobList.size(); i++)
        {
            job temp=jobList.get(i);
            if((!temp.done)&&(temp.timeGenerated<=expectedCompletionTime)&&(temp.pid!=pid)&&(temp.timeGenerated>lowerBound))
            {   
                double lastMin=min;
                min=Math.min(min,temp.timeGenerated);
                if(min!=lastMin){
                    index=i;
                    continue;
                }
            }
        }
        return index;
    }

    public int areAnyLeft(double finishTime)
    {
        for(int i=0; i<jobList.size(); i++){
            job temp=jobList.get(i);
            if((temp.timeGenerated<finishTime)&&(!temp.done)){
                return 1;
            }
        }
        return -1;
    }

    public void sendAllJobsAhead(double finishTime)
    {
        for(int i=0; i<jobList.size(); i++)
        {
            job temp=jobList.get(i);
            if((temp.timeGenerated<finishTime)&&(!temp.done))
            {
                temp.timeGenerated=finishTime;
                continue;
            }
        }
    }


    public job getMinJob(double finishTime)
    {
        job minJob=jobList.get(0);
        double min=100000;

        for(int i=0; i<jobList.size(); i++){
             job temp=jobList.get(i);
             if((temp.timeGenerated==finishTime)&&(!temp.done)){
                 double lastMin=min;
                min=Math.min(min,temp.timeCompletion);
                if(lastMin!=min)
                {
                    minJob=temp;
                    continue;
                }

             }
        }
        return minJob;
    }

    public job getNextJob(double nextTime){
        job minJob=jobList.get(0);
        double min=100000;
         for(int i=0; i<jobList.size(); i++){
             job temp=jobList.get(i);
             if((temp.timeGenerated>nextTime)&&(!temp.done)){
                double lastMin=min;
                min=Math.min(min,temp.timeGenerated);
                if(lastMin!=min)
                {
                    minJob=temp;
                    continue;
                }

             }
        }
        return minJob;
    }
}