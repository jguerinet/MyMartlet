package ca.mcgill.mymcgill.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import junit.framework.TestCase;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.LoginActivity;

public class EbillTest extends ActivityInstrumentationTestCase2<EbillActivity> {
public EbillActivity mActivityClass;
public ListView listView;

	@SuppressWarnings("deprecation")
	public EbillTest(){
		super("ca.mcgill.mymcgill.activity.ebill.EbillActivity",EbillActivity.class);
	}
	
	public void testListView(){
		assertNotNull(listView);
	}
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mActivityClass = this.getActivity();
		
		// This was changed (JDA)
		listView = (ListView) mActivityClass.findViewById(android.R.id.list);
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		//TODO LOG OUT 
	}
}
