package ca.appvelopers.mcgillmobile.object;

public class AccountItem {
	private String itemName;
	private String detailCode;
	private float charges;
	private float payments;
	private float balance;
	
	public AccountItem(String name, String detail, float charge, float payment, float balance) {
		this.itemName = name;
		this.detailCode = detail;
		this.charges = charge;
		this.payments = payment;
		this.balance = balance;
	}
	
	public String getName() {
		return itemName;
	}
	public String getDetailCode() {
		return detailCode;
	}
	public String getCharges() {
		return "$"+charges;
	}
	public String getPayments() {
		return "$"+payments;
	}
	public String getBalance() {
		return "$"+balance;
	}
}
