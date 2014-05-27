package ca.mcgill.mymcgill.object;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EbillItem implements Serializable{
    private static final long serialVersionUID = 1L;

	private String statementDate;
	private String dueDate;
	private String amountDue;
	
	private EbillItem(String statementDate, String dueDate, String amountDue)
	{
		this.statementDate = statementDate;
		this.dueDate = dueDate;
		this.amountDue = amountDue;
	}

    public static List<EbillItem> parseEbill(String ebillString){
        List<EbillItem> ebillItems = new ArrayList<EbillItem>();

        //Parse the string to get the relevant info
        Document doc = Jsoup.parse(ebillString);
        Element ebillTable = doc.getElementsByClass("datadisplaytable").first();
        Elements rows = ebillTable.getElementsByTag("tr");

        for (int i = 2; i < rows.size(); i+=2) {
            Element row = rows.get(i);
            Elements cells = row.getElementsByTag("td");
            String statementDate = cells.get(0).text();
            String dueDate = cells.get(3).text();
            String amountDue = cells.get(5).text();
            ebillItems.add(new EbillItem(statementDate, dueDate, amountDue));
        }

        return ebillItems;
    }

	public String getStatementDate() {
		return statementDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public String getAmountDue() {
		return amountDue;
	}

	
}
