package com.sinitphone.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	public final static String MAGIC_NUMBER = "555555555$"; 
	
	public static final int REQUEST_SELECT_PHONE_NUMBER = 1;

	private static final String NUMBER_TO_CALL = "number_to_call_key";

	private static final String NUMBER_TEXT = "number_text_key";
	
	public static final String ALWAYS_CALL_PREFIX = "always_call_";
	
	private String numberToCall = null;
	
	private TextView numberTextView;
  
	private Button callButton;
	
	private CheckedTextView alwaysCallCheck;
	
	SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		if( savedInstanceState != null )
		{
			numberToCall = (String) savedInstanceState.getCharSequence( NUMBER_TO_CALL );
		}
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
//		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
//				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		preferences = PreferenceManager.getDefaultSharedPreferences( this );
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		
		numberTextView = (TextView)findViewById( R.id.numberTextView );
		callButton = (Button)findViewById( R.id.callButton );
		alwaysCallCheck = (CheckedTextView)findViewById( R.id.allways_call );
		
		if( savedInstanceState != null )
		{
			numberTextView.setText( savedInstanceState.getCharSequence( NUMBER_TEXT ) );
		}
		
		if( numberToCall != null )
		{
			callButton.setVisibility( View.VISIBLE );
			alwaysCallCheck.setVisibility( View.VISIBLE );
		}
		else
		{
			callButton.setVisibility( View.GONE );
			alwaysCallCheck.setVisibility( View.GONE );
			alwaysCallCheck.setChecked( checkForAlwaysCall( numberToCall ) );
		}
		
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		
		Button button = (Button)findViewById( R.id.contactButton );
		button.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent contactIntent = new Intent( Intent.ACTION_PICK );
				contactIntent.setType( CommonDataKinds.Phone.CONTENT_TYPE );
				if( contactIntent.resolveActivity( getPackageManager() ) != null )
				{
			        startActivityForResult( contactIntent, REQUEST_SELECT_PHONE_NUMBER );
			    }
			}
			
		});
		
		callButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( numberToCall != null )
				{
					Intent intent = new Intent( Intent.ACTION_CALL );
				    intent.setData( Uri.parse( "tel:" + MAGIC_NUMBER.replace( "$", normalizeNumber( numberToCall ) ) ) );
				    if( intent.resolveActivity( getPackageManager() ) != null )
				    {
				        startActivity( intent );
				    }
				}
			}
			
		});
		
		alwaysCallCheck.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( alwaysCallCheck.isChecked() )
				{
					alwaysCallCheck.setChecked( false );
					setAlwaysCallState( numberToCall, false );
				}
				else
				{
					alwaysCallCheck.setChecked( true );
					setAlwaysCallState( numberToCall, true );
				}
			}
			
		});
		
		super.onResume();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
//		switch (number) {
//		case 1:
//			mTitle = getString(R.string.title_section1);
//			break;
//		case 2:
//			mTitle = getString(R.string.title_section2);
//			break;
//		case 3:
//			mTitle = getString(R.string.title_section3);
//			break;
//		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
	        // Get the URI and query the content provider for the phone number
	        Uri contactUri = data.getData();
	        String[] projection = new String[]{CommonDataKinds.Phone.NUMBER,CommonDataKinds.Phone.DISPLAY_NAME};
	        Cursor cursor = getContentResolver().query(contactUri, projection,
	                null, null, null);
	        // If the cursor returned is valid, get the phone number
	        if (cursor != null && cursor.moveToFirst()) {
	            int numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
	            int nameIndex = cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME);
	            String number = cursor.getString(numberIndex);
	            String name = cursor.getString(nameIndex);
	            numberToCall = number;
	            numberTextView.setText( number + " (" + name + ")" );
	            callButton.setVisibility( View.VISIBLE );
	            alwaysCallCheck.setChecked( checkForAlwaysCall( numberToCall ) );
	            alwaysCallCheck.setVisibility( View.VISIBLE );
	        }
	    }
	}	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);
		}		
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putCharSequence( NUMBER_TO_CALL, numberToCall );
		
		outState.putCharSequence( NUMBER_TEXT, numberTextView.getText() );
		super.onSaveInstanceState(outState);
	}

	
	public static String normalizeNumber( String number )
	{
		
		if( number.startsWith( "+" ) )
		{
			return number = "00" + number.substring( 1 );
		}
		else if( number.startsWith( "00" ) )
		{
			return number;
		}
		else if( number.startsWith( "0" ) )
		{
			return "0049" + number.substring( 1 );
		}
		else
		{
			return number;
		}
		
	}
	
	private boolean checkForAlwaysCall( String number )
	{
		return preferences.getBoolean( ALWAYS_CALL_PREFIX + number, false );
	}
	
	private void setAlwaysCallState( String number, boolean state )
	{
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean( ALWAYS_CALL_PREFIX + number, state );
		editor.commit();
	}

}
