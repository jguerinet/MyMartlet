package ca.mcgill.mymcgill.activity.inbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ca.mcgill.mymcgill.R;

public class AttachActivity extends ListActivity {
	
	private File currentDir;
	private FileArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			startActivity(replyIntent);
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
