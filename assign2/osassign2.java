import java.util.*;
import java.util.Random;
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
import java.io.*;
import org.jfree.chart.ChartUtilities;

public class osassign2{

public static void main(String[] args){
    ArrayList<job> jobList=generateJobs(4,1.5);
    
    return; 

    }

static void printList(ArrayList<job> jobs){
    for(int i=0; i<jobs.size(); i++){
        System.out.println("Job description: "+jobs.get(i).pid + ", " + jobs.get(i).timeGenerated + "," + jobs.get(i).timeCompletion);

    }
    return;
}

static ArrayList<job> generateJobs(int numberOfJobs, double lambda){
    int maxJobLength=2;
    double previousTime=0;
    Random rand=new Random(2);
    int pid=1;
    ArrayList<job> jobList=new ArrayList<job>();
    for(int i=0; i<numberOfJobs; i++){
        double arrivalTime=rand.nextDouble();
        arrivalTime=(-1/lambda)*Math.log(1-arrivalTime);
        arrivalTime=arrivalTime+previousTime;
        previousTime=arrivalTime;
        // if(pid==1)
        // {
        //     jobList.add(new job(pid++,0.262d,1.88d));
        // }
        // if(pid==2)
        // {
        //     jobList.add(new job(pid++,0.438d,1.147d));
        // }
        // if(pid==3)
        // {
        //     jobList.add(new job(pid++,2d,4d));
        // }
        // if(pid==4)
        // {
        //     jobList.add(new job(pid++,4d,1.2d));
        // }
        jobList.add(new job(pid++,arrivalTime,1+rand.nextDouble()*maxJobLength));
     }
     printList(jobList);
     //mlfqboost mlfqalgo=new mlfqboost(jobList,0.5d,0.1d,0.15d);
    //roundrobin roundrobinalgo=new roundrobin(jobList,1.5d);
    //fifo fifoalgo=new fifo(jobList);
     //sjf sjfalgo=new sjf(jobList);
    //roundrobin roundrobinalgo=new roundrobin(jobList,0.4d);
    // sjcf sjcfalgo=new sjcf(jobList);
    mlfqboost mlfqalgo=new mlfqboost(jobList,0.5d,0.1d,0.15d);
    
    
//fifoalgo.execute();
     //sjfalgo.execute();
    //roundrobinalgo.execute();
     //sjcfalgo.execute();
     mlfqalgo.execute();
    
     //ganttchart charts=new ganttchart("Scheduler","FIFO",fifoalgo.dataset);
    //ganttchart charts=new ganttchart("Scheduler","SJF",sjfalgo.dataset);
      //ganttchart charts=new ganttchart("Scheduler","ROUNDROBIN",roundrobinalgo.dataset);
    //ganttchart charts=new ganttchart("Scheduler","SJCF",sjcfalgo.dataset);
      ganttchart charts=new ganttchart("Scheduler","MLFQ",mlfqalgo.dataset);
     charts.pack();
	charts.setVisible(true);
    
     return jobList;
}
}

