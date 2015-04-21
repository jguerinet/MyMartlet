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
