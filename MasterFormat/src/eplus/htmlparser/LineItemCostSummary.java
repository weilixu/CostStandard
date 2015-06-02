package eplus.htmlparser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LineItemCostSummary {
    private final int lineNo = 1;
    private final int itemName = 2;
    private final int quantity = 3;
    private final int units = 4;
    private final int valuePerQty = 5;
    private final int subTotal = 6;

    private final Document doc;
    private final Elements lineItemCostSummaryTable;

    private static final String LINE_ITEM_COST_TABLE_ID = "Component Cost Economics Summary:Cost Line Item Details";
    private static final String TAG = "tableID";

    private List<LineItem> lineItemList;

    public LineItemCostSummary(Document d) {
	doc = d;
	lineItemCostSummaryTable = doc.getElementsByAttributeValue(TAG,
		LINE_ITEM_COST_TABLE_ID);
	lineItemList = new ArrayList<LineItem>();
	processData();
    }

    private void processData() {
	try {
	    Elements costItemList = lineItemCostSummaryTable.get(0)
		    .getElementsByTag("td");
	    for (int i = 0; i < costItemList.size(); i++) {
		if (costItemList.get(i).text().equals("--")
			&& (i + subTotal) < costItemList.size()) {
		    LineItem temp = new LineItem();
		    temp.setLineNumber(costItemList.get(i + lineNo).text());
		    temp.setName(costItemList.get(i + itemName).text());
		    temp.setQuantity(costItemList.get(i + quantity).text());
		    temp.setUnits(costItemList.get(i + units).text());
		    temp.setUnitValue(costItemList.get(i + valuePerQty).text());
		    temp.setTotal(costItemList.get(i + subTotal).text());
		    lineItemList.add(temp);
		}
	    }

	} catch (Exception e) {
	    System.out
		    .println("There is no cost summary in this html file yet");
	}

    }

    public int getLineItemSize() {
	return lineItemList.size();
    }

    public LineItem getLineItem(int index) {
	return lineItemList.get(index);
    }

    public class LineItem {
	private String lineNumber;
	private String name;
	private String quant;
	private String unit;
	private String unitValue;
	private String total;

	public void setLineNumber(String l) {
	    lineNumber = l;
	}

	public void setName(String n) {
	    name = n;
	}

	public void setQuantity(String q) {
	    quant = q;
	}

	public void setUnits(String u) {
	    unit = u;
	}

	public void setUnitValue(String value) {
	    unitValue = value;
	}

	public void setTotal(String total) {
	    this.total = total;
	}

	public String getLineNumber() {
	    return lineNumber;
	}

	public String getName() {
	    return name;
	}

	public String getQuantity() {
	    return quant;
	}

	public String getUnit() {
	    return unit;
	}

	public String getUnitValue() {
	    return unitValue;
	}

	public String getTotal() {
	    return total;
	}
    }

}
