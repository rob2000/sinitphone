package com.sinitphone.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class NewCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
		if( intent.getAction().equals( Intent.ACTION_NEW_OUTGOING_CALL ) )
		{
			Bundle extraBundle = intent.getExtras();
			if( extraBundle != null && extraBundle.containsKey( Intent.EXTRA_PHONE_NUMBER ) )
			{
				String phoneNumber = extraBundle.getString( Intent.EXTRA_PHONE_NUMBER );
				setResultData( MainActivity.MAGIC_NUMBER.replace( "$", phoneNumber ) );
			}
		}
	}

}
