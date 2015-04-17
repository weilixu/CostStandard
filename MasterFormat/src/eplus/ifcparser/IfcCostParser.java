package eplus.ifcparser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ifc4javatoolbox.ifc4.ClassInterface;
import ifc4javatoolbox.ifc4.IfcApplication;
import ifc4javatoolbox.ifc4.IfcAppliedValue;
import ifc4javatoolbox.ifc4.IfcAreaMeasure;
import ifc4javatoolbox.ifc4.IfcArithmeticOperatorEnum;
import ifc4javatoolbox.ifc4.IfcChangeActionEnum;
import ifc4javatoolbox.ifc4.IfcCostItem;
import ifc4javatoolbox.ifc4.IfcCostItemTypeEnum;
import ifc4javatoolbox.ifc4.IfcCostValue;
import ifc4javatoolbox.ifc4.IfcCountMeasure;
import ifc4javatoolbox.ifc4.IfcDate;
import ifc4javatoolbox.ifc4.IfcDimensionalExponents;
import ifc4javatoolbox.ifc4.IfcGloballyUniqueId;
import ifc4javatoolbox.ifc4.IfcIdentifier;
import ifc4javatoolbox.ifc4.IfcLabel;
import ifc4javatoolbox.ifc4.IfcMeasureWithUnit;
import ifc4javatoolbox.ifc4.IfcMonetaryMeasure;
import ifc4javatoolbox.ifc4.IfcMonetaryUnit;
import ifc4javatoolbox.ifc4.IfcNamedUnit;
import ifc4javatoolbox.ifc4.IfcOwnerHistory;
import ifc4javatoolbox.ifc4.IfcPersonAndOrganization;
import ifc4javatoolbox.ifc4.IfcPhysicalQuantity;
import ifc4javatoolbox.ifc4.IfcPhysicalSimpleQuantity;
import ifc4javatoolbox.ifc4.IfcQuantityArea;
import ifc4javatoolbox.ifc4.IfcQuantityCount;
import ifc4javatoolbox.ifc4.IfcSIPrefix;
import ifc4javatoolbox.ifc4.IfcSIUnit;
import ifc4javatoolbox.ifc4.IfcSIUnitName;
import ifc4javatoolbox.ifc4.IfcStateEnum;
import ifc4javatoolbox.ifc4.IfcText;
import ifc4javatoolbox.ifc4.IfcTimeStamp;
import ifc4javatoolbox.ifc4.IfcUnit;
import ifc4javatoolbox.ifc4.IfcUnitEnum;
import ifc4javatoolbox.ifc4.IfcValue;
import ifc4javatoolbox.ifc4.LIST;
import ifc4javatoolbox.ifc4.STRING;
import ifc4javatoolbox.ifcmodel.IfcModel;
import eplus.IdfReader;
import eplus.htmlparser.EnergyPlusHTMLParser;
import eplus.htmlparser.LineItemCostSummary;
import eplus.htmlparser.LineItemCostSummary.LineItem;

public class IfcCostParser {
    private final IdfReader model;
    private final LineItemCostSummary parser;
    private final IfcModel ifcModel = new IfcModel();

    private final String COMPONENT = "ComponentCost:LineItem";

    private final IfcGloballyUniqueId uniqueId;
    private final IfcOwnerHistory history;

    private LIST<IfcPhysicalQuantity> physicalquantityList;
    private LIST<IfcCostValue> costValueList;

    private final IfcCostItem estimateItem;

    public IfcCostParser(IdfReader r, EnergyPlusHTMLParser p)
	    throws IOException {
	model = r;
	parser = p.getCostSummary();
	physicalquantityList = new LIST<IfcPhysicalQuantity>();
	costValueList = new LIST<IfcCostValue>();

	processCostItem();

	STRING id = new STRING();
	id.setDecodedValue(model.getValue("Building", "Name"));
	uniqueId = new IfcGloballyUniqueId(id);
	Integer time = (int) System.currentTimeMillis();
	history = new IfcOwnerHistory(new IfcPersonAndOrganization(),
		new IfcApplication(), new IfcStateEnum(),
		new IfcChangeActionEnum(), new IfcTimeStamp(time),
		new IfcPersonAndOrganization(), new IfcApplication(),
		new IfcTimeStamp());

	estimateItem = new IfcCostItem(uniqueId, history, new IfcLabel(
		"Cost Estimation", true), new IfcText("For Budget Control",
		true), new IfcLabel(), new IfcIdentifier("0", true),
		new IfcCostItemTypeEnum("USERDEFINED"), costValueList,
		physicalquantityList);
	ifcModel.addIfcObject(estimateItem);
	OutputStream outputStream = new FileOutputStream(new File(
		"E:\\test.ifc"));
	ifcModel.writeStepfile(outputStream);
    }

    private void processCostItem() {
	Integer size = parser.getLineItemSize();
	String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	for (int i = 0; i < size; i++) {
	    LineItem item = parser.getLineItem(i);
	    if (item.getUnit().equals("m2")) {
		IfcSIUnit siUnit = new IfcSIUnit(new IfcDimensionalExponents(),
			new IfcUnitEnum("AREAUNIT"), new IfcSIPrefix(),
			new IfcSIUnitName("SQUARE_METRE"));
		ifcModel.addIfcObject(siUnit);
		IfcPhysicalQuantity pq = new IfcQuantityArea(new IfcLabel(
			item.getUnit(), true), new IfcText("", true),
			siUnit, new IfcAreaMeasure(
				Double.parseDouble(item.getQuantity())),
			new IfcLabel());

		// monetary measure and monetary unit
		IfcValue v = new IfcMonetaryMeasure(Double.parseDouble(item
			.getUnitValue()));
		IfcUnit mu = new IfcMonetaryUnit(new IfcLabel("$/m2", true));
		IfcValue vTotal = new IfcMonetaryMeasure(
			Double.parseDouble(item.getTotal()));
		IfcUnit muTotal = new IfcMonetaryUnit(new IfcLabel("$", true));
		IfcMeasureWithUnit total = new IfcMeasureWithUnit(vTotal,
			muTotal);
		IfcMeasureWithUnit measured = new IfcMeasureWithUnit(v, mu);
		ifcModel.addIfcObject((ClassInterface) mu);
		ifcModel.addIfcObject(measured);

		ifcModel.addIfcObject((ClassInterface) muTotal);
		ifcModel.addIfcObject(total);

		// ifc cost value
		LIST<IfcAppliedValue> list = new LIST<IfcAppliedValue>();
		IfcCostValue cv = new IfcCostValue(new IfcLabel(item.getName(),
			true), new IfcText(), total, measured, new IfcDate(
			date, true), new IfcDate(), new IfcLabel(
			"Estimated Cost", true), new IfcLabel("New", true),
			new IfcArithmeticOperatorEnum("MULTIPLY"), list);
		physicalquantityList.add(pq);
		costValueList.add(cv);
		ifcModel.addIfcObject(cv);
		ifcModel.addIfcObject(pq);

	    } else if (item.getUnit().equals("Ea.")) {
		IfcNamedUnit siUnit = new IfcSIUnit(new IfcDimensionalExponents(),
			new IfcUnitEnum("USERDEFINED"), new IfcSIPrefix(),
			new IfcSIUnitName());
		ifcModel.addIfcObject(siUnit);

		IfcPhysicalQuantity pq = new IfcQuantityCount(new IfcLabel(
			item.getUnit(), true), new IfcText("", true),
			siUnit, new IfcCountMeasure(
				Double.parseDouble(item.getQuantity())),
			new IfcLabel());
		IfcValue v = new IfcMonetaryMeasure(Double.parseDouble(item
			.getUnitValue()));
		IfcUnit mu = new IfcMonetaryUnit(new IfcLabel("$/Ea.", true));

		IfcValue vTotal = new IfcMonetaryMeasure(
			Double.parseDouble(item.getTotal()));
		IfcUnit muTotal = new IfcMonetaryUnit(new IfcLabel("$", true));

		IfcMeasureWithUnit total = new IfcMeasureWithUnit(vTotal,
			muTotal);
		IfcMeasureWithUnit measured = new IfcMeasureWithUnit(v, mu);

		ifcModel.addIfcObject((ClassInterface) mu);
		ifcModel.addIfcObject(measured);

		ifcModel.addIfcObject((ClassInterface) muTotal);
		ifcModel.addIfcObject(total);

		LIST<IfcAppliedValue> list = new LIST<IfcAppliedValue>();
		IfcCostValue cv = new IfcCostValue(new IfcLabel(item.getName(),
			true), new IfcText(), total, measured, new IfcDate(
			date, true), new IfcDate(), new IfcLabel(
			"Estimated Cost", true), new IfcLabel("New", true),
			new IfcArithmeticOperatorEnum("MULTIPLY"), list);
		physicalquantityList.add(pq);
		costValueList.add(cv);
		ifcModel.addIfcObject(cv);
		ifcModel.addIfcObject(pq);
	    }
	}
    }

    public void printTest() {
	System.out.println(estimateItem.getStepLine());
	System.out.println(estimateItem.getPredefinedType().getStepLine());
	for (int i = 0; i < estimateItem.getCostValues().size(); i++) {
	    System.out.println(estimateItem.getCostValues().get(i)
		    .getStepLine());
	}
	for (int i = 0; i < estimateItem.getCostQuantities().size(); i++) {
	    System.out.println(estimateItem.getCostQuantities().get(i)
		    .getStepLine());
	}
    }

    public static void main(String[] args) throws IOException {
	IdfReader reader = new IdfReader();
	reader.setFilePath("C:\\Users\\Weili\\Desktop\\New folder\\OptimizedCase\\72.idf");
	reader.readEplusFile();

	EnergyPlusHTMLParser p = new EnergyPlusHTMLParser(
		new File(
			"C:\\Users\\Weili\\Desktop\\New folder\\OptimizedCase\\72Table.html"));

	IfcCostParser parser = new IfcCostParser(reader, p);
	parser.printTest();
    }

}
