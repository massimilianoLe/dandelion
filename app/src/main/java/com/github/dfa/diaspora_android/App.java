/*
    This file is part of the Diaspora for Android.

    Diaspora for Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Diaspora for Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora for Android.

    If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.dfa.diaspora_android;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.github.dfa.diaspora_android.data.DiasporaUserProfile;
import com.github.dfa.diaspora_android.service.AvatarImageLoader;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;

public class App extends Application {

    private AppSettings appSettings;
    private AvatarImageLoader avatarImageLoader;
    private CookieManager cookieManager;
    private DiasporaUserProfile diasporaUserProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context c = getApplicationContext();
        appSettings = new AppSettings(c);

        // Init app log
        AppLog.setLoggingEnabled(appSettings.isLoggingEnabled());
        AppLog.setLoggingSpamEnabled(appSettings.isLoggingSpamEnabled());

        // Init pod profile
        avatarImageLoader = new AvatarImageLoader(c);
        diasporaUserProfile = new DiasporaUserProfile(this);


        // Get cookie manager
        cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.createInstance(c);
        }
        cookieManager.setAcceptCookie(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public void resetPodData(@Nullable WebView webView) {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl(DiasporaUrlHelper.URL_BLANK);
            webView.clearFormData();
            webView.clearHistory();
            webView.clearCache(true);
        }

        // Clear avatar image
        new AvatarImageLoader(this).clearAvatarImage();

        // Clear preferences__master
        appSettings.clearPodSettings();

        // Clear cookies
        //noinspection deprecation
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }
    }

    public DiasporaUserProfile getDiasporaUserProfile() {
        return diasporaUserProfile;
    }

    public AppSettings getSettings() {
        return appSettings;
    }

    public AvatarImageLoader getAvatarImageLoader() {
        return avatarImageLoader;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }
}
