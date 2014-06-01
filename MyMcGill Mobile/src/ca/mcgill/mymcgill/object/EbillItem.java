package ca.mcgill.mymcgill.object;

import java.io.Serializable;

public class EbillItem implements Serializable{
    private static final long serialVersionUID = 1L;

	private String statementDate;
	private String dueDate;
	private String amountDue;
	
	public EbillItem(String statementDate, String dueDate, String amountDue){
		this.statementDate = statementDate;
		this.dueDate = dueDate;
		this.amountDue = amountDue;
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
