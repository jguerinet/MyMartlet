package ca.mcgill.mymcgill.object;

public class Ebill {
	private String statementDate;
	private String dueDate;
	private String amountDue;
	
	public Ebill(String statementDate,String dueDate,String amountDue)
	{
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
