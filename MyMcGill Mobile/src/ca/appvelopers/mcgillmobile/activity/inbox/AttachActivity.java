package ca.appvelopers.mcgillmobile.activity.inbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseListActivity;

public class AttachActivity extends BaseListActivity {
	
	private File currentDir;
	private FileArrayAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_attach);
		
		currentDir =  new File(Environment.getExternalStorageDirectory().toString());
		fill(currentDir);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory"))
		{
			currentDir = new File(o.getPath());
			fill(currentDir);
		}
		else
		{
			//Toast.makeText(this,currentDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
			Intent replyIntent = new Intent(this,ReplyActivity.class);
			replyIntent.putExtra("file", currentDir.getAbsolutePath() + "/" + o.getName());
			setResult(RESULT_OK, replyIntent);
			finish();	
		}
	}

	private void fill(File f)
	{
		File[] dirs = f.listFiles();
		this.setTitle(f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		adapter = new FileArrayAdapter(AttachActivity.this, R.layout.activity_attach , R.id.TextView01, dir);
		this.setListAdapter(adapter);
		
		try{
			for(File ff: dirs)
			{
				if (ff.isDirectory())
					dir.add(new Option(ff.getName(),"Folder", ff.getAbsolutePath()));
				else 
				{
					fls.add(new Option(ff.getName(),"File Size: " + ff.length(),ff.getAbsolutePath()));
				}
			}
		}
		catch (Exception e)
		{
			
		}
		dir.addAll(fls);
		if(!f.getName().equalsIgnoreCase("sdcard")) dir.add(0,new Option("..","Parent Directory",f.getParent()));

	}
}
