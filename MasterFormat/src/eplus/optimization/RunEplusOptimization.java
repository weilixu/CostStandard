package eplus.optimization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import jmetal.util.JMException;
import masterformat.api.DatabaseUtils;
import eplus.IdfReader;
import eplus.htmlparser.EnergyPlusHTMLParser;

public class RunEplusOptimization {
    private static final String EPLUSBAT = "RunEPlus.bat";

    private File folder;
    private File eplusFolder;
    private Integer simulationCount;

    private IdfReader idfData;

    private String energyplus_dir;
    private String weather_dir;

    public RunEplusOptimization(IdfReader reader) {
	idfData = reader;

	String[] config = DatabaseUtils.getEplusConfig();
	energyplus_dir = config[0];
	weather_dir = config[1];
    }

    public void setSimulationTime(Integer simulationTime) {
	simulationCount = simulationTime;
    }

    public void setFolder(File f) {
	folder = f;
    }

    public EnergyPlusHTMLParser runSimulation() throws IOException, JMException {
	// create the Energyplus folder
	eplusFolder = new File(folder.getAbsolutePath() + "\\"
		+ simulationCount.toString());
	eplusFolder.mkdir();

	// create a copy of weather file
	File weatherFile = new File(energyplus_dir + "WeatherData\\"
		+ weather_dir + ".epw");
	copyWeatherFile(weatherFile);

	// create a batch file to run the simulation
	File eplusBatFile = createBatchFile();
	// create the idf file under the created energyplus folder
	idfData.WriteIdf(eplusFolder.getAbsolutePath(),
		simulationCount.toString());
	// make it in a folder
	File energyPlusFile = new File(folder.getAbsolutePath() + "\\"
		+ simulationCount.toString() + "\\"
		+ simulationCount.toString() + ".idf");
	// commandline: eplus batch file directory energyplus file (without
	// postfix) and weather file name (without postfix)
	String[] commandline = {
		eplusBatFile.getAbsolutePath(),
		energyPlusFile.getAbsolutePath().substring(0,
			energyPlusFile.getAbsolutePath().indexOf(".")),
		"weather" };

	// run simulation
	try {
	    Process p = Runtime.getRuntime().exec(commandline, null,
		    eplusFolder);
	    ThreadedInputStream errStr = new ThreadedInputStream(
		    p.getErrorStream());
	    errStr.start();
	    ThreadedInputStream outStr = new ThreadedInputStream(
		    p.getInputStream());
	    outStr.start();
	    p.waitFor();

	    errStr.join();
	    outStr.join();
	    // finished simulation, delete the file
	    eplusBatFile.delete();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	
	EnergyPlusHTMLParser parser = null;
	// get the output from HTML
	File[] fileList = eplusFolder.listFiles();
	for (File f : fileList) {
	    if (f.getAbsolutePath().contains(".html")) {
		parser = new EnergyPlusHTMLParser(f);
	    }
	}
	return parser;
    }

    /*
     * copy weather file into a directory
     */
    private void copyWeatherFile(File weatherFile) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(weatherFile));
	StringBuilder sb = new StringBuilder();
	File file = new File(eplusFolder.getAbsolutePath() + "\\"
		+ "weather.epw");

	try {
	    String line = br.readLine();

	    while (line != null) {
		sb.append(line);
		sb.append(System.lineSeparator());
		line = br.readLine();
	    }
	} finally {
	    FileWriter results = null;
	    try {
		results = new FileWriter(file, true);
		PrintWriter pw = new PrintWriter(results);
		pw.append(sb.toString());
		pw.close();
	    } catch (IOException e) {
		// some warning??
	    }
	    // close the file
	    br.close();
	}
    }

    /*
     * Create a batch file in the directory
     */
    private File createBatchFile() throws IOException {
	String keyWord = "set program_path=";
	String weaWord = "set weather_path=";
	File file = new File(eplusFolder.getAbsolutePath() + "\\" + EPLUSBAT);
	file.createNewFile();

	// reading file and write to the new file

	BufferedReader br = new BufferedReader(new FileReader(EPLUSBAT));
	StringBuilder sb = new StringBuilder();

	try {
	    String line = br.readLine();

	    while (line != null) {
		if (line.contains(keyWord)) {
		    sb.append(keyWord);
		    sb.append(energyplus_dir);
		} else if (line.contains(weaWord)) {
		    sb.append(weaWord);
		    sb.append(eplusFolder.getAbsolutePath() + "\\");
		} else {
		    sb.append(line);
		}
		sb.append(System.lineSeparator());
		line = br.readLine();
	    }
	} finally {
	    FileWriter results = null;
	    try {
		results = new FileWriter(file, true);
		PrintWriter pw = new PrintWriter(results);
		pw.append(sb.toString());
		pw.close();
	    } catch (IOException e) {
		// some warning??
	    }
	    // close the file
	    br.close();
	}
	return file;
    }

    private class ThreadedInputStream extends Thread {
	protected IOException ioExc;
	protected InputStream is;
	protected StringBuffer sb = null;

	public ThreadedInputStream(InputStream inputStream) {
	    is = inputStream;
	    sb = new java.lang.StringBuffer();
	    ioExc = null;
	}

	public void run() {
	    try {
		byte[] by = new byte[1];
		while (this != null) {
		    int ch = is.read(by);
		    if (ch != -1) { // -1 indicates the end of the stream
			sb.append((char) by[0]);
		    } else {
			break;
		    }
		}
		is.close();
	    } catch (IOException e) {
		ioExc = e;
	    }
	}

	public void throwStoredException() throws IOException {
	    if (ioExc != null) {
		throw ioExc;
	    }
	}

	public String getInputStream() {
	    return new String(sb.toString());
	}
    }

}
