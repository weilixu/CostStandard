package eplus.htmlparser;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EnergyPlusHTMLParser {

    private File htmlFile;
    private Document doc;

    private FanSizingSummary fanSummary;
    private PumpSizingSummary pumpSummary;
    private HeatingCoilSummary heatCoilSummary;
    private CoolingCoilSummary coolCoilSummary;
    private CentralPlantSummary plantSummary;
    private InteriorLightingSummary lightSummary;
    private LineItemCostSummary itemCostSummary;
    private EnvelopeSummary envelopeSummary;
    private TransparentEnvelopeSummary transparentEnvelopeSummary;

    public EnergyPlusHTMLParser(File f) {
	htmlFile = f;
	try {
	    doc = Jsoup.parse(htmlFile, "UTF-8");
	    preprocessTable();
	    fanSummary = new FanSizingSummary(doc);
	    pumpSummary = new PumpSizingSummary(doc);
	    heatCoilSummary = new HeatingCoilSummary(doc);
	    coolCoilSummary = new CoolingCoilSummary(doc);
	    plantSummary = new CentralPlantSummary(doc);
	    lightSummary = new InteriorLightingSummary(doc);
	    itemCostSummary = new LineItemCostSummary(doc);
	    envelopeSummary = new EnvelopeSummary(doc);
	    transparentEnvelopeSummary = new TransparentEnvelopeSummary(doc);
	} catch (IOException e) {
	    // do nothing
	}

    }

    private void preprocessTable() {
	String report = null;
	Elements htmlNodes = doc.getAllElements();
	for (int i = 0; i < htmlNodes.size(); i++) {
	    if (htmlNodes.get(i).text().contains("Report:")) {
		report = htmlNodes.get(i + 1).text();
	    }
	    if (htmlNodes.get(i).hasAttr("cellpadding")) {
		String tableName = htmlNodes.get(i - 3).text();
		htmlNodes.get(i).attr("tableID", report + ":" + tableName);
	    }
	}
    }

    /**
     * So far, this method extracts {fanCategory, fanPressure,
     * fanFlowRate,fanPower}, future element can be added to it
     * 
     * @param fanName
     * @return
     */
    public String[] getFanSummary(String fanName) {
	String[] fanProperties = new String[4];
	fanProperties[0] = fanSummary.getFanCategory(fanName);
	fanProperties[1] = fanSummary.getFanPressure(fanName);
	fanProperties[2] = fanSummary.getFanFlowRate(fanName);
	fanProperties[3] = fanSummary.getFanPower(fanName);
	return fanProperties;
    }

    /**
     * So far the method extracts {heatingCoilCapacity, heatingCoilEfficiency,
     * CoilType} future element can be added to this list.
     * 
     * @param heatCoilName
     * @return
     */
    public String[] getHeatCoilSummary(String heatCoilName) {
	String[] heatCoilProperties = new String[3];
	heatCoilProperties[0] = heatCoilSummary
		.getHeatingCoilCapacity(heatCoilName);
	heatCoilProperties[1] = heatCoilSummary
		.getHeatingCoilEfficiency(heatCoilName);
	heatCoilProperties[2] = heatCoilSummary
		.getHeatingCoilType(heatCoilName);
	return heatCoilProperties;
    }

    /**
     * So far the method extracts {coolingcoil capacity, cooling coil sensible
     * load, cooling coil latent load , cooling coil efficiency} Future element
     * can be added to this list;
     * 
     * @param coolCoilName
     * @return
     */
    public String[] getCoolCoilSummary(String coolCoilName) {
	String[] coolCoilProperties = new String[4];
	coolCoilProperties[0] = coolCoilSummary
		.getCoolingCoilTotalCapacity(coolCoilName);
	coolCoilProperties[1] = coolCoilSummary
		.getCoolingCoilSensibleLoad(coolCoilName);
	coolCoilProperties[2] = coolCoilSummary
		.getCoolingCoilLatentLoad(coolCoilName);
	coolCoilProperties[3] = coolCoilSummary
		.getCoolingCoilEfficiency(coolCoilName);

	return coolCoilProperties;
    }

    /**
     * So far the method extracts {equipment capacity, equipment efficiency}
     * future element can be added to this list
     * 
     * @param plantName
     * @return
     */
    public String[] getCentralPlantSummary(String plantName) {
	String[] centralPlant = new String[2];
	centralPlant[0] = plantSummary.getEquipmentCapacity(plantName);
	centralPlant[1] = plantSummary.getEquipmentEfficiency(plantName);
	return centralPlant;
    }

    /**
     * So far the method extracts{pump power, pump head, pump flow rate} future
     * element can be added to this list
     * 
     * @param pumpName
     * @return
     */
    public String[] getPumpSummary(String pumpName) {
	String[] pump = new String[3];
	pump[0] = pumpSummary.getPumpPower(pumpName);
	pump[1] = pumpSummary.getPumpHead(pumpName);
	pump[2] = pumpSummary.getPumpWaterFlow(pumpName);
	return pump;
    }

    /**
     * So far the method only extracts{light total power} future element can be
     * added to this list
     * 
     * @param lightName
     * @return
     */
    public String[] getInteriorLightSummary(String lightName) {
	String[] lights = new String[1];
	lights[0] = lightSummary.getLightPower(lightName);
	return lights;
    }
    
    public LineItemCostSummary getCostSummary(){
	return itemCostSummary;
    }
    
    public Double getConstrucitonArea(String cons){
	return envelopeSummary.getConstructionArea(cons);
    }
    
    /**
     * includes area, uvalue, shgc, vt
     * @param cons
     * @return
     */
    public String[] getTransparentMaterialSummary(String cons){
	String[] material = new String[4];
	material[0] = transparentEnvelopeSummary.getContructionArea(cons);
	material[1] = transparentEnvelopeSummary.getConstructionUValue(cons);
	material[2] = transparentEnvelopeSummary.getConstructionSHGC(cons);
	material[3] = transparentEnvelopeSummary.getConstructionVisibleTransmittance(cons);
	return material;
    }
    
}
