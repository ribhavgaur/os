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


public class mlfqboost{
    ArrayList<job> jobList;
    double timeSlice1;
    double timeSlice2;
    double timeSlice3;
    TaskSeries Q1Task=new TaskSeries("Queue1");
    TaskSeries Q2Task=new TaskSeries("Queue2");
    TaskSeries Q3Task=new TaskSeries("Queue3");
    TaskSeriesCollection dataset = new TaskSeriesCollection();
    HashMap<Integer,Task> mapQ1=new HashMap<Integer,Task>();
    HashMap<Integer,Task> mapQ2=new HashMap<Integer,Task>();
    HashMap<Integer,Task> mapQ3=new HashMap<Integer,Task>();
    Queue<job> q1 = new LinkedList<>(); 
    Queue<job> q2 = new LinkedList<>(); 
    Queue<job> q3 = new LinkedList<>();
    boolean flagForQ1=false; //should we switch to queue1 

    public void analysis()
    {
        double averageTurnAroundTime=0d;
        double responseTime=0d;
         for(int key : mapQ1.keySet())
        {

            Task currentTask;
            int count;
            Task currentTask1=mapQ1.get(key);
            int count1=currentTask1.getSubtaskCount();
            count1=count1-1;

             Task currentTask2=mapQ2.get(key);
            int count2=currentTask2.getSubtaskCount();
            count2=count2-1;

             Task currentTask3=mapQ3.get(key);
            int count3=currentTask3.getSubtaskCount();
            count3=count3-1;

            //response time -> can be lifted from first queue
            //turnaround -> last element from the queue ka maximum if the queue has non zero count

            responseTime=responseTime+ (((SimpleTimePeriod) currentTask1.getSubtask(0).getDuration()).getStartMillis()/100d)-jobList.get(key-1).originalTimeGenerated;

            double max = -1;

            for(int i = 0;i<3;i++)
            {
                if(i==0)
                    {
                    count = count1;
                    currentTask = currentTask1;
                    }

                else if (i==1){
                    count = count2;
                    currentTask = currentTask2;
                }
                else
                {
                    count = count3;
                    currentTask = currentTask3;
                }
                if(count==-1)
                    continue;
                if((((SimpleTimePeriod) currentTask.getSubtask(count).getDuration()).getEndMillis()/100d) > max)
                {
                    max = (((SimpleTimePeriod) currentTask.getSubtask(count).getDuration()).getEndMillis()/100d);
                }
            //averageTurnAroundTime=averageTurnAroundTime+(((SimpleTimePeriod) currentTask.getSubtask(count).getDuration()).getEndMillis()/100d)-jobList.get(key-1).originalTimeGenerated;
            //responseTime=responseTime+ (((SimpleTimePeriod) currentTask.getSubtask(0).getDuration()).getStartMillis()/100d)-jobList.get(key-1).originalTimeGenerated;
            }
            averageTurnAroundTime = averageTurnAroundTime + max - jobList.get(key-1).originalTimeGenerated;
        }
        System.out.println("Average turnaround time is " + averageTurnAroundTime/jobList.size());
        System.out.println("Average response time is " + responseTime/jobList.size());
        return;
    }

    public mlfqboost(ArrayList<job> jobList,double a,double b, double c)
    {
        this.jobList = jobList;
        this.timeSlice1=a;
        this.timeSlice2=b;
        this.timeSlice3=c;
        for(int i=0; i<jobList.size(); i++){
            mapQ1.put(jobList.get(i).pid,new Task(Integer.toString(jobList.get(i).pid),new SimpleTimePeriod(0,5000)));
            mapQ2.put(jobList.get(i).pid,new Task(Integer.toString(jobList.get(i).pid),new SimpleTimePeriod(0,5000)));
            mapQ3.put(jobList.get(i).pid,new Task(Integer.toString(jobList.get(i).pid),new SimpleTimePeriod(0,5000)));
            
        }
       
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

    public void execute(){

        double currentTime=0d;
        job currentJob;
        double sliceEndTime;
        double finishTime;
        //q1.add(jobList.get(0));
        boolean clash = false;
        boolean clash2 = false;
        job clashingJob;
        double boostTime = 2d;
        int timesBoosted = 1;
        //double firstJobTime = jobList.get(0).timeGenerated;

        while(true){
            if(allDone())
            break;
            //System.out.println("came here");
            //System.out.println("Justentered boost is "+boostTime*timesBoosted);
            for(int i = 0;i<jobList.size();i++)
            {
                job newJob = jobList.get(i);
                if((q1.size()==0) && (q2.size()==0) && (q3.size()==0))
                {
                if(newJob.isItNew)
                {
                    if(currentTime<=newJob.timeGenerated && (newJob.priority==1))
                    {
                        //System.out.println("addedddddddddddedDDDDDDDDD "+newJob.pid);
                    q1.add(newJob);
                    newJob.isItNew = false;
                    currentTime = newJob.timeGenerated;
                    break;
                    }
                }
                }
            }
            while(true)
            {
                if(timesBoosted*boostTime>=currentTime)
                {
                    break;
                }
                else{
                    timesBoosted++;
                }
            }
    //System.out.println("serial booster boost is "+boostTime*timesBoosted);
            //System.out.println("Size of q1 outside q1 loop is " +q1.size());
            //sliceEndTime=currentTime+timeSlice1;
            //addToQueue1(sliceEndTime); // all those which are priority=1 and undone are added to q1 and set isItNew=false and timegenerated <sliceEndTime
            while(q1.size()!=0){
                //System.out.println("first loop");
            sliceEndTime=currentTime+timeSlice1;
            
            addToQueue1(currentTime,sliceEndTime);
            //System.out.println("After addtoQueue1 size is " + q1.size());
            currentJob=q1.remove();
            //System.out.println("running job number " + currentJob.pid + "size of q1 is "+q1.size());
            //System.out.println("currentTime starting  is " + currentTime);
            //System.out.println("sliceEndTime starting  is " + sliceEndTime);
            finishTime=currentTime+currentJob.timeCompletion;
            if(finishTime<sliceEndTime){

                if(finishTime>=timesBoosted*boostTime)
                {
                    mapQ1.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                    currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                    currentTime = timesBoosted*boostTime;
                    timesBoosted++;
                    currentJob.timeGenerated = currentTime;
                    reset();
                    break;
                }
                else {
                currentJob.done=true;
                mapQ1.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(finishTime*100))));
                currentTime=finishTime;
                continue;
                }
            }
            else{
                if(sliceEndTime>=timesBoosted*boostTime)
                {
                    //System.out.println("Now boost current tikme " + currentTime + " and boost time is "+timesBoosted*boostTime);
                    mapQ1.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                    currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                    currentTime = timesBoosted*boostTime;
                    timesBoosted++;
                    currentJob.timeGenerated = currentTime;
                    reset();
                    //System.out.println("After resert q1 size is " + q1.size());
                    break;
                }
                else{
                    currentJob.done=false;
                    currentJob.priority=2;
                    //System.out.println("Priority of job " + currentJob.pid + " is now " + currentJob.priority);
                    q2.add(currentJob);
                    mapQ1.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(sliceEndTime*100))));
                    currentJob.timeCompletion=currentJob.timeCompletion-(sliceEndTime-currentTime);
                    currentTime=sliceEndTime;
                    currentJob.timeGenerated=currentTime;
                    //System.out.println("After sending it down currentTime is " + currentTime);
                    continue;
                }
            }



            }
            flagForQ1=false;
            while(q2.size()!=0)
            {
                //System.out.println("fsecond loop");
                sliceEndTime = currentTime + timeSlice2;
                clashingJob = findClashingJob(currentTime,sliceEndTime);
                
                    
                if(clashingJob==null)
                {
                    clash = false;
                }
                else{
                    clash = true;
                }
                if(clash)
                {
                    sliceEndTime = clashingJob.timeGenerated;
                    //System.out.println("sliceEndTime update starting  is " + sliceEndTime);
                }
                
                currentJob = q2.remove();
                //System.out.println("running job number "+currentJob.pid);
                //System.out.println("currentTime starting  is " + currentTime);
                //System.out.println("sliceEndTime starting  is " + sliceEndTime);

                finishTime = currentTime + currentJob.timeCompletion;
                if(finishTime<=sliceEndTime)
                {
                    if(finishTime>=timesBoosted*boostTime)
                    {
                        mapQ2.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                        currentTime = timesBoosted*boostTime;
                        timesBoosted++;
                        currentJob.timeGenerated = currentTime;
                        reset();
                        break;
                    }
                    else{
                        currentJob.done = true;
                        mapQ2.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(finishTime*100))));
                        currentTime = finishTime;
                        continue;
                    }
                }
                if((finishTime>sliceEndTime) && (!clash))
                {
                    if(sliceEndTime>=timesBoosted*boostTime)
                    {
                        mapQ2.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                        currentTime = timesBoosted*boostTime;
                        timesBoosted++;
                        currentJob.timeGenerated = currentTime;
                        reset();
                        break;
                    }
                    else{
                        currentJob.priority = 3;
                        q3.add(currentJob);
                        mapQ2.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(sliceEndTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion-(sliceEndTime-currentTime);
                        currentTime = sliceEndTime;
                        currentJob.timeGenerated = currentTime;
                        continue;
                    }
                }
                if((finishTime>sliceEndTime) && (clash))
                {
                    if(sliceEndTime>=timesBoosted*boostTime)
                    {
                        mapQ2.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                        currentTime = timesBoosted*boostTime;
                        timesBoosted++;
                        currentJob.timeGenerated = currentTime;
                        reset();
                        break;
                    }
                    else{
                        //System.out.println("currentTime is " + currentTime);
                        //System.out.println("sliceEndTime is " + sliceEndTime);
                        mapQ2.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(sliceEndTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion-(sliceEndTime-currentTime);
                        currentTime = sliceEndTime;
                        currentJob.timeGenerated = currentTime;
                        clashingJob.isItNew=false;
                        q1.add(clashingJob);
                        q2.add(currentJob);
                        flagForQ1 = true;
                        break;
                    }
                }
            }

            if(flagForQ1)
                continue;
            flagForQ1 = false;
            while(q3.size()!=0)
            {
                //System.out.println("thirdTHIRDAAA GYAAAA YAYYYYAYAYYAYA loop");
                sliceEndTime = currentTime + timeSlice3;
                
                    
                clashingJob = findClashingJob(currentTime,sliceEndTime);
                if(clashingJob==null)
                {
                    clash2 = false;
                }
                else{
                    clash2 = true;
                }
                if(clash2)
                {
                    sliceEndTime = clashingJob.timeGenerated;
                }
                
                currentJob = q3.remove();
                 //System.out.println("running job number "+currentJob.pid);
                //System.out.println("currentTime starting  is " + currentTime);
                //System.out.println("sliceEndTime starting  is " + sliceEndTime);
                finishTime = currentTime + currentJob.timeCompletion;
                if(finishTime<=sliceEndTime)
                {
                    if(finishTime>=timesBoosted*boostTime)
                    {
                        mapQ3.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                        currentTime = timesBoosted*boostTime;
                        timesBoosted++;
                        currentJob.timeGenerated = currentTime;
                        reset();
                        break;
                    }
                    else{
                        currentJob.done = true;
                        mapQ3.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(finishTime*100))));
                        currentTime = finishTime;
                        continue;
                    }
                }
                if((finishTime>sliceEndTime) && (!clash2))
                {
                    if(sliceEndTime>=timesBoosted*boostTime)
                    {
                        //System.out.println("not clash boost");
                        mapQ3.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                        currentTime = timesBoosted*boostTime;
                        timesBoosted++;
                        currentJob.timeGenerated = currentTime;
                        reset();
                        break;
                    }
                    else
                    {
                        currentJob.priority = 3;
                        q3.add(currentJob);
                        mapQ3.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(sliceEndTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion-(sliceEndTime-currentTime);
                        currentTime = sliceEndTime;
                        currentJob.timeGenerated = currentTime;
                        continue;
                    }
                }
                if((finishTime>sliceEndTime) && (clash2))
                {
                    if(sliceEndTime>=timesBoosted*boostTime)
                    {
                        //System.out.println("not clash boost");
                        mapQ3.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(timesBoosted*boostTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion - (timesBoosted*boostTime - currentTime);
                        currentTime = timesBoosted*boostTime;
                        timesBoosted++;
                        currentJob.timeGenerated = currentTime;
                        reset();
                        break;
                    }
                    else
                    {
                        mapQ3.get(currentJob.pid).addSubtask(new Task(Integer.toString(currentJob.pid),new SimpleTimePeriod((long) (currentTime*100), (long)(sliceEndTime*100))));
                        currentJob.timeCompletion = currentJob.timeCompletion-(sliceEndTime-currentTime);
                        currentTime = sliceEndTime;
                        currentJob.timeGenerated = currentTime;
                        clashingJob.isItNew=false;
                        q1.add(clashingJob);
                        flagForQ1 = true;
                        q3.add(currentJob);
                        break;
                    }
                }
            }
            if(flagForQ1)
                continue;
            flagForQ1 = false;

        }
         for(int key : mapQ1.keySet())
        {
            Q1Task.add(mapQ1.get(key));
        }
         for(int key : mapQ2.keySet())
        {
            Q2Task.add(mapQ2.get(key));
        }
         for(int key : mapQ3.keySet())
        {
            Q3Task.add(mapQ3.get(key));
        }
        dataset.add(Q1Task);
        dataset.add(Q2Task);
        
        dataset.add(Q3Task);
        analysis();
        return;
    }

        public void addToQueue1(double currentTime,double sliceEndTime){

            // all those which are priority=1 and undone are added to q1 and set isItNew=false and timegenerated <sliceEndTime
            for(int i = 0;i<jobList.size();i++)
            {
                job currentJob = jobList.get(i);
                //System.out.println("looking at job " + currentJob.pid + "," +currentJob.timeGenerated);
                if((currentJob.priority==1) && (currentJob.done == false) && (currentJob.timeGenerated <= sliceEndTime)&&(currentJob.timeGenerated>currentTime))
                {
                    q1.add(currentJob);
                    currentJob.isItNew = false;
                    //System.out.println("AddedDDDERERERERERERERRERERRERERRER the job " + currentJob.pid);
                }
            }
            return;

        }

        public job findClashingJob(double currentTime,double sliceEndTime)
        {
            double minimum=10000d;
            job minimum_job=null;
            for(int i = 0;i<jobList.size();i++)
            {
                job currentJob=jobList.get(i);
                if((currentJob.isItNew)&&(currentJob.timeGenerated<=sliceEndTime)&&(currentJob.timeGenerated>currentTime)){
                    if(currentJob.timeGenerated<=minimum){
                        minimum_job=currentJob;
                        minimum=currentJob.timeGenerated;
                    }
                }
                
            }
            return minimum_job;
        }

        public void reset()
        {
            //will remove all jobs from all queues, set the priority of each job to 1
            //q1.clear();
            int i=0;
            for(i=0; i<jobList.size(); i++)
            {
                //System.out.println("loOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOkking at " + jobList.get(i).pid + "," + jobList.get(i).done + ","+jobList.get(i).isItNew);
                if((!jobList.get(i).isItNew)&&(!jobList.get(i).done))
                    { jobList.get(i).priority=1;
                    q1.add(jobList.get(i));
                    }
            }

            q2.clear();
            q3.clear();
            return;
        }
}