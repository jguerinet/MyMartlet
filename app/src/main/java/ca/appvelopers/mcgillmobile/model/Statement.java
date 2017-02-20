/*
 * Copyright 2014-2017 Julien Guerinet
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

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.threeten.bp.LocalDate;

import java.io.Serializable;

import ca.appvelopers.mcgillmobile.util.dbflow.databases.StatementsDB;

/**
 * One statement in the user's ebill
 * @author Quang Dao
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = StatementsDB.class, allFields = true)
public class Statement extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Randomly generated Id for this statement, used as a primary key
     */
    @PrimaryKey(autoincrement = true)
    int id;
	/**
	 * Statement date
	 */
	LocalDate date;
	/**
	 * Due date
	 */
	LocalDate dueDate;
	/**
	 * Total amount due or owed
	 */
	double amount;

    /**
     * DB Constructor
     */
    Statement() {}

	/**
	 * Default Constructor
	 *
	 * @param date    Statement date
	 * @param dueDate Due date
	 * @param amount  Amount due or owed
	 */
	public Statement(LocalDate date, LocalDate dueDate, double amount) {
		this.date = date;
		this.dueDate = dueDate;
		this.amount = amount;
	}

	/* GETTERS */

	/**
	 * @return Statement date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @return Statement due date
	 */
	public LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * @return Amount owed or due
	 */
	public double getAmount() {
		return amount;
	}
}
