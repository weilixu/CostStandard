package masterformat.gui;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class PlotHistogram {
	private static int numBins;
	private static String title;
	private ChartPanel chart;
	private double[] data;

	public PlotHistogram(String t, double[] data) {
		this.data = data;
		numBins = getNumBins();
		title = t;
	}

	private int getNumBins() {
		// Use Freedman-Diaconis' rule which is based on IQR to
		// calculate number of bins for histogram plot
		DescriptiveStatistics ds = new DescriptiveStatistics(data);
		double n = ds.getN();
		double iqr = ds.getPercentile(75) - ds.getPercentile(25);
		double min = ds.getMin();
		double max = ds.getMax();
		double h = 2*iqr/Math.cbrt(n);
		int numBins = (int) ((max-min)/h);
		
		if(numBins<=0){
		    numBins = 1;
		}
		
		return numBins;
	}

	private IntervalXYDataset createDataset() {
		HistogramDataset histogramdataset = new HistogramDataset();
		histogramdataset.addSeries("simulation results", data, numBins);
		return histogramdataset;
	}

	private JFreeChart createChart(IntervalXYDataset intervalxydataset) {
		JFreeChart jfreechart = ChartFactory.createHistogram(title,
				 "Cost per Square Meter ($/m2)", "Frequency",intervalxydataset,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setForegroundAlpha(0.65F);
		XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot
				.getRenderer();
		xybarrenderer.setDrawBarOutline(false);
		return jfreechart;
	}

	public ChartPanel createPanel() {
		JFreeChart jfreechart = createChart(createDataset());
		return new ChartPanel(jfreechart);
	}
}

