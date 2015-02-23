package eplus.ifcparser;

import java.io.IOException;

import ifc4javatoolbox.ifc4.IfcApplication;
import ifc4javatoolbox.ifc4.IfcChangeActionEnum;
import ifc4javatoolbox.ifc4.IfcGloballyUniqueId;
import ifc4javatoolbox.ifc4.IfcOwnerHistory;
import ifc4javatoolbox.ifc4.IfcPersonAndOrganization;
import ifc4javatoolbox.ifc4.IfcStateEnum;
import ifc4javatoolbox.ifc4.IfcTimeStamp;
import ifc4javatoolbox.ifc4.STRING;
import eplus.IdfReader;

public class IfcCostParser {
    private final IdfReader model;
    
    private final String COMPONENT = "ComponentCost:LineItem";

    private final IfcGloballyUniqueId uniqueId;
    private final IfcOwnerHistory history;

    public IfcCostParser(IdfReader r) {
	model = r;

	STRING id = new STRING();
	id.setDecodedValue(model.getValue("Building", "Name"));
	uniqueId = new IfcGloballyUniqueId(id);
	Integer time = (int) System.currentTimeMillis();
	history = new IfcOwnerHistory(new IfcPersonAndOrganization(),
		new IfcApplication(), new IfcStateEnum(),
		new IfcChangeActionEnum(), new IfcTimeStamp(time),
		new IfcPersonAndOrganization(), new IfcApplication(),
		new IfcTimeStamp());
    }

    public void printTest() {
	String u = uniqueId.toString();
	String step = uniqueId.getStepLine();
	System.out.println(step + " " + u);
	String stephis = history.getStepLine();
	String h = history.toString();
	System.out.println(stephis + " " + h);

    }

    public static void main(String[] args) throws IOException {
	IdfReader reader = new IdfReader();
	reader.setFilePath("C:\\Users\\Weili\\Desktop\\New folder\\CostTester.idf");
	reader.readEplusFile();

	IfcCostParser parser = new IfcCostParser(reader);
	parser.printTest();
    }

}
