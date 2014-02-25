package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ca.mcgill.mymcgill.R;

public class BaseActivity extends Activity{

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public String[] layers;
    private ActionBarDrawerToggle drawerToggle;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        assert (getActionBar() != null);

        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Set up the drawer toggle
        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, R.drawable.ic_drawer, 0, 0){
            public void onDrawerClosed(View view){
                getActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView){
                getActionBar().setTitle(R.string.menu);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        layers = new String[4];
        layers[0] = "test1";
        layers[1] = "test2";
        layers[2] = "test3";
        layers[3] = "test4";

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,
                layers));

//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//                map.drawerClickEvent(pos);
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
