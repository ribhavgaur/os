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
import org.jfree.chart.axis.DateAxis;
import java.text.SimpleDateFormat;
import org.jfree.chart.plot.CategoryPlot;

public class ganttchart extends JFrame
{

    public ganttchart (String applicationTitle, String chartTitle, IntervalCategoryDataset dataset)
    {
        super(applicationTitle);

        JFreeChart chart = ChartFactory.createGanttChart(chartTitle, "Processes", "Time", dataset,
		 true, true, true);


        CategoryPlot plot = chart.getCategoryPlot();
        DateAxis axis = (DateAxis) plot.getRangeAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("SSSS"));
        axis.setMaximumDate(new Date(2500));
         ChartPanel chartPanel = new ChartPanel(chart);

	
	chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));
    setContentPane(chartPanel);
    }
}