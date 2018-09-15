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

public class job{
    int pid;
    double timeGenerated;
    double timeCompletion;
    double originalTimeGenerated;
    boolean done=false;
    boolean addToQueue=false;
    int priority=1;
    boolean isItNew=true; //only for first queue
    double turnAround;
    double responseTime;
    


    public job(int pid,double timeGenerated,double timeCompletion){
        this.pid=pid;
        this.timeGenerated=timeGenerated;
        this.timeCompletion=timeCompletion;
        this.originalTimeGenerated=timeGenerated;
    }

    public job()
    {
        this.pid = 0;
        this.timeGenerated = 0;
        this.timeCompletion = 0;
    }

    public int getPid(){
        return pid;
    }

    public double getTimeGenerated(){
        return timeGenerated;
    }
    public double getTimeCompletion(){
        return timeCompletion;
    }
}