package ca.mcgill.mymcgill.test;

import android.test.ActivityInstrumentationTestCase2;
import junit.framework.TestCase;
import ca.mcgill.mymcgill.activity.EbillActivity;
import ca.mcgill.mymcgill.activity.LoginActivity;

public class EbillTest extends ActivityInstrumentationTestCase2<EbillActivity> {
public EbillActivity mActivityClass;	
	public EbillTest(){
		super("ca.mcgill.mymcgill.activity.EbillActivity",EbillActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mActivityClass = this.getActivity();
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		//TODO LOG OUT 
	}
}
