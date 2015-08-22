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

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * One statement in the user's ebill
 * @author Quang Dao
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class Statement implements Serializable{
    private static final long serialVersionUID = 1L;
	/**
	 * The statement date
	 */
	private LocalDate mDate;
	/**
	 * The due date
	 */
	private LocalDate mDueDate;
	/**
	 * The total amount due or owed
	 */
	private double mAmount;

	/**
	 * Default Constructor
	 *
	 * @param date    The statement date
	 * @param dueDate The due date
	 * @param amount  The amount due or owed
	 */
	public Statement(LocalDate date, LocalDate dueDate, double amount){
		this.mDate = date;
		this.mDueDate = dueDate;
		this.mAmount = amount;
	}

	/* GETTERS */

	/**
	 * @return The statement date
	 */
	public LocalDate getDate() {
		return this.mDate;
	}

	/**
	 * @return The statement due date
	 */
	public LocalDate getDueDate() {
		return this.mDueDate;
	}

	/**
	 * @return The amount owed or due
	 */
	public double getAmount() {
		return this.mAmount;
	}
}
