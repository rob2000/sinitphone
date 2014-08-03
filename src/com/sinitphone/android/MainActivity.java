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

	public final static String MAGIC_NUMBER_PREFERENCE = "magic_number"; 
	
	public static final int REQUEST_SELECT_PHONE_NUMBER = 1;
	
	SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		preferences = PreferenceManager.getDefaultSharedPreferences( this );
		
	}

	@Override
	protected void onResume() {
		
		TextView magicNumberTextView = (TextView) findViewById( R.id.magic_number );
		magicNumberTextView.setText( preferences.getString( MAGIC_NUMBER_PREFERENCE, "$" ) );
		
		magicNumberTextView.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Editor editor = preferences.edit();
				editor.putString( MAGIC_NUMBER_PREFERENCE, s.toString() );
				editor.commit();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
			
		});
		
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
		
		Button callButton = (Button)findViewById( R.id.callButton );
		callButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView numberToCallTextView = (TextView)findViewById( R.id.numberTextView );
				Intent intent = new Intent( Intent.ACTION_DIAL );
			    intent.setData( Uri.parse( "tel:" + numberToCallTextView.getText() ) );
			    if( intent.resolveActivity( getPackageManager() ) != null )
			    {
			        startActivity( intent );
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
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
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
	        String[] projection = new String[]{CommonDataKinds.Phone.NUMBER};
	        Cursor cursor = getContentResolver().query(contactUri, projection,
	                null, null, null);
	        // If the cursor returned is valid, get the phone number
	        if (cursor != null && cursor.moveToFirst()) {
	            int numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
	            String number = cursor.getString(numberIndex);
	            String magicNumber = preferences.getString( MAGIC_NUMBER_PREFERENCE, "$" );
	            String numberToCall = magicNumber.replace( "$", number );
	            TextView numberTextView = (TextView)findViewById( R.id.numberTextView );
	            numberTextView.setText( numberToCall );
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
			
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
			
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			TextView magicNumberTextView = (TextView) rootView.findViewById( R.id.magic_number );
			magicNumberTextView.setText( preferences.getString( MAGIC_NUMBER_PREFERENCE, "$" ) );
			
			magicNumberTextView.addTextChangedListener( new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					Editor editor = preferences.edit();
					editor.putString( MAGIC_NUMBER_PREFERENCE, s.toString() );
					editor.commit();
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
				}
				
			});
			
//			TextView textView = (TextView) rootView
//					.findViewById(R.id.section_label);
//			textView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
			
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

}
