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

package ca.appvelopers.mcgillmobile.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Save;

/**
 * Displays the EULA
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @version 2.0
 * @since 1.0.0
 */
public class AgreementActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.bind(this);

        boolean required = getIntent().getBooleanExtra(Constants.EULA_REQUIRED, false);
        setUpToolbar(!required);

        //Check if we need to display the buttons
        if(required){
            LinearLayout layout = (LinearLayout)findViewById(R.id.buttons_container);
            layout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.button_agree)
    public void agree(){
        //Save the fact that they accepted
        Save.saveUserAgreement(AgreementActivity.this, true);

        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.button_decline)
    public void decline(){
        //Save the fact that they declined
        Save.saveUserAgreement(AgreementActivity.this, false);

        setResult(RESULT_CANCELED);
        finish();
    }
}