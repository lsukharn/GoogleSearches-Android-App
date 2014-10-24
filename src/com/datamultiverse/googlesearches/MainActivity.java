package com.datamultiverse.googlesearches;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends ListActivity {
	
	private static final String FILE = "file_searches";

	private EditText googleSearch;
	private EditText googleTag;
	private ArrayList<String> tags;
	private SharedPreferences savedSearches;
	private ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//superclass must be called to execute its code in addition to
		//code I wrote in life cycle method onCreate
		super.onCreate(savedInstanceState);
		//inflate GUI
		setContentView(R.layout.activity_main);
		//get references to widgets
		googleSearch = (EditText)findViewById(R.id.googleSearchEditText);
		googleTag = (EditText)findViewById(R.id.tagEditText);
		ImageButton saveButton = (ImageButton)findViewById(R.id.imageButton1);
		saveButton.setOnClickListener(saveOnClick);
		//initialize SharedPreferences object with FILE data
		savedSearches = getSharedPreferences(FILE, MODE_PRIVATE);
		//initialize ArrayList<String> of tags
		tags = new ArrayList<String>(savedSearches.getAll().keySet());
		//then sort it using a Collections object
		Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
		//initialize ArrayAdapter<String> (context: this, presentation:
		//R.layout.item_layout, data to display of type List<Staring>: tags)
		adapter = new ArrayAdapter<String>(this, R.layout.item_layout, tags);
		//call ListActivity method setListAdapter to bind the ListView to
		//ArrayAdapter
		setListAdapter(adapter);
		//declare listeners that handle events of 'short' click and
		//long click
		getListView().setOnItemClickListener(onSearchItemClick);
		getListView().setOnItemLongClickListener(onLongClick);
		
	}
	//implement OnClickListener interface 
	public OnClickListener saveOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (googleSearch.getText().length() > 0 && 
					googleTag.getText().length() > 0){
				//call method, which adds a tagged search into list
				//of searches
			addGoogleSearch(googleSearch.getText().toString(), 
			googleTag.getText().toString());
			//then clear both entry views
			googleSearch.getText().clear(); 
			googleTag.getText().clear();
			//and programmatically hide keyboard
			((InputMethodManager)getSystemService(
			Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					googleTag.getWindowToken(), 0);
			}
			else{ //complain that the search is not filled right
			// to access 'this' in anonymous inner class 'this' 
			//should be fully specified with outer class's name
				AlertDialog.Builder complain = 
						new AlertDialog.Builder(MainActivity.this);
			complain.setMessage(getString(R.string.notEnoughData));
			complain.setPositiveButton(R.string.ok, null);
			complain.create().show();
			}
		}
	};
	//use SharedPreferences.Editor to save query and its tag 
	//and then sort it using Collections.sort
	private void addGoogleSearch(String query, String tag){
		SharedPreferences.Editor editor = savedSearches.edit();
		editor.putString(tag, query);
		editor.apply();
		if(!tags.contains(tag)){
			tags.add(tag);
			Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
			adapter.notifyDataSetChanged();
		}
	}
	OnItemClickListener onSearchItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			String tag = ((TextView)view).getText().toString(); //the view 
			//TextView is implemented in item_layout.xml -> it's its base element,
			//hence we use ((TextView)view).getText()
			String urlGoogleMobile =savedSearches.getString(tag, "");
			//start web_search intent 
			//create an object of Intent passing Intent.ACTION_WEB_SEARCH
			//to its constructor
			Intent webIntent = new Intent(Intent.ACTION_WEB_SEARCH);
			//by using putExtra method specify dynamically created 
			//url - urlGoogleMobile
			webIntent.putExtra(SearchManager.QUERY, urlGoogleMobile);
			if (webIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(webIntent);
			
			}
		}
	};
	//program events of 'Share', 'Edit' and 'Delete'
	OnItemLongClickListener onLongClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int arg2, long arg3) {
			final String tag = ((TextView)view).getText().toString();
			AlertDialog.Builder dialogBuilder = new
					AlertDialog.Builder(MainActivity.this);
			dialogBuilder.setTitle(getString(R.string.shareEditDelete, 
					tag));
			dialogBuilder.setItems(R.array.arrayOfStrings, 
					new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which){
					case 0:
						shareSearch(tag);
						break;
					case 1:
						googleTag.setText(tag);
						googleSearch.setText(savedSearches.getString(tag, ""));
						break;
					case 2:
						deleteSearch(tag);
						break;
					}
					
				}
				
			});
			dialogBuilder.setNegativeButton(getString(R.string.cancel), 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
			dialogBuilder.create().show();
			return true;
		}
	};
private void shareSearch(String tag){
	//get the serach out of SharedPreferences object 
	//and then instantiate shareIntenet Intent object
	//add extra parameters with Intent methods
	String theSearch = savedSearches.getString(tag, "");
	Intent shareIntent = new Intent();
	shareIntent.setAction(Intent.ACTION_SEND);
	shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
	shareIntent.putExtra(Intent.EXTRA_TEXT, theSearch);
	shareIntent.setType("text/plain");
	startActivity(Intent.createChooser(shareIntent, "Intent Name"));
}

private void deleteSearch(final String tag){
	//Create alert dialog alertDelete to display delete dialog
	AlertDialog.Builder alertDelete = 
			new AlertDialog.Builder(this);
	//set it's message to R.string.alertMessage: "Are you sure you want to delete this...
	alertDelete.setMessage(getString(R.string.alertMessage, tag));
	//set positive (OK) button and assign a listener (OnClickListener) for it
	alertDelete.setPositiveButton(R.string.ok, 
			new DialogInterface.OnClickListener() {
	//handle onClick event of alertDelete dialog - i.e. delete a 
	//tag from ArrayLsit of tags and delete key-value pair from
	//svaedSearches SharedPreferences object using SharedPreferences.Editor 
	//object 
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
				tags.remove(tag);
			
				SharedPreferences.Editor editorDelete = 
						savedSearches.edit();
				editorDelete.remove(tag);
				editorDelete.apply();
				adapter.notifyDataSetChanged();
			
		}
	});
	//create a negative button, which closes the dialog 
	alertDelete.setNegativeButton(getString(R.string.cancel), 
			new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
		
	});
	//create alertDelete and show it on the screen in MainActivity
	alertDelete.create().show();
}
}
