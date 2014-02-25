package ca.mcgill.mymcgill.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import ca.mcgill.mymcgill.activity.EbillActivity;


public class EbillTest extends ActivityInstrumentationTestCase2<EbillActivity> {
public EbillActivity mActivityClass;
public ListView listView;

	@SuppressWarnings("deprecation")
	public EbillTest(){
		super("ca.mcgill.mymcgill.activity.EbillActivity",EbillActivity.class);
	}
	
	public void testListView(){
		assertNotNull(listView);
	}
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mActivityClass = this.getActivity();
		
		listView = (ListView) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.ebill_listview);
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		//TODO LOG OUT 
	}
}
