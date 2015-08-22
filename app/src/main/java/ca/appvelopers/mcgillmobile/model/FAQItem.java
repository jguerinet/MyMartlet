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

/**
 * An FAQ shown in the Help section
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0.0
 */
public class FAQItem {
    /**
     * The FAQ question
     */
    private String mQuestion;
    /**
     * The FAQ answer
     */
    private String mAnswer;

    /**
     * Default Constructor
     *
     * @param question The FAQ question
     * @param answer   The FAQ answer
     */
    public FAQItem(String question, String answer){
        this.mQuestion = question;
        this.mAnswer = answer;
    }

    /* GETTERS */

    /**
     * @return The FAQ question
     */
    public String getQuestion()
    {
        return this.mQuestion;
    }

    /**
     * @return The FAQ answer
     */
    public String getAnswer()
    {
        return this.mAnswer;
    }

}
