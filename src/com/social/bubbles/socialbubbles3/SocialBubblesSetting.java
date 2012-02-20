/**
 * 
 */
package com.social.bubbles.socialbubbles3;

import com.social.bubbles.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SocialBubblesSetting extends PreferenceActivity 
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(
                SocialBubblesService.PREFERENCES);
        addPreferencesFromResource(R.xml.social_settings);
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {}

}
