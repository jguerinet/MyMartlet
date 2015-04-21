/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.model;

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
