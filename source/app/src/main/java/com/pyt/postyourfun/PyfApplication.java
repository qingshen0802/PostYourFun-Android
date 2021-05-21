package com.pyt.postyourfun;

import android.support.multidex.MultiDexApplication;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * Created by r8tin on 2/5/16.
 */
public class PyfApplication extends MultiDexApplication {

    private Permission[] permissions = new Permission[]{Permission.USER_PHOTOS, Permission.EMAIL, Permission.PUBLISH_ACTION};

    @Override
    public void onCreate() {
        super.onCreate();
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.app_name))
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }
}