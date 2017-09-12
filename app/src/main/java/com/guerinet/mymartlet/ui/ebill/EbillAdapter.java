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

package com.guerinet.mymartlet.ui.ebill;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Statement;
import com.guerinet.suitcase.date.DateFormat;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter used for the ebill page
 * @author Julien Guerinet
 * @since 1.0.0
 */
class EbillAdapter extends RecyclerView.Adapter<EbillAdapter.StatementHolder> {
    /**
     * List of {@link Statement}s
     */
    private final List<Statement> mStatements;

    /**
     * Default Constructor
     */
    EbillAdapter() {
        mStatements = new ArrayList<>();
        update();
    }

    /**
     * Updates the list of {@link Statement}s shown
     */
    void update() {
        // Clear existing statements
        mStatements.clear();

        // Add the other statements asynchronously
        SQLite
                .select()
                .from(Statement.class)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        return;
                    }
                    mStatements.addAll(tResult);
                    notifyDataSetChanged();
                })
                .execute();
    }

    @Override
    public StatementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new StatementHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_statement, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(StatementHolder statementHolder, int i) {
        statementHolder.bindStatement(mStatements.get(i));
    }

    @Override
    public int getItemCount() {
        return mStatements.size();
    }

    class StatementHolder extends RecyclerView.ViewHolder {
        /**
         * Statement date
         */
        @BindView(R.id.statement_date)
        protected TextView mDate;
        /**
         * Statement due date
         */
        @BindView(R.id.statement_due_date)
        protected TextView mDueDate;
        /**
         * Statement amount
         */
        @BindView(R.id.statement_amount)
        protected TextView mAmount;

        public StatementHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindStatement(Statement statement) {
            Context context = itemView.getContext();

            mDate.setText(context.getString(R.string.ebill_statement_date,
                    DateFormat.getLongDateString(statement.getDate())));
            mDueDate.setText(context.getString(R.string.ebill_due_date,
                    DateFormat.getLongDateString(statement.getDueDate())));

            double amount = statement.getAmount();
            mAmount.setText(String.format("$%s", String.valueOf(amount)));

            //Change the color to green or red depending on if the user owes money or not
            int colorId = amount < 0 ?  R.color.green : R.color.red;
            mAmount.setTextColor(ContextCompat.getColor(context, colorId));
        }
    }
}
