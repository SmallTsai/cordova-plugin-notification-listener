package com.lou.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.TextView;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NotificationListener extends CordovaPlugin {

    public static final String NOTIFY_CHANNEL = "com.lou.cordova.plugin.APP_BROADCST_RECEIVER" + System.currentTimeMillis();
    private static final String LOG_TAG = "NotificationListener";

    private static final String ACTION_HAS_PERMISSION = "hasPermission";
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_IS_RUNNING = "isRunning";
    private static final String ACTION_TOGGLE = "toggle";
    private static final String ACTION_ADD_LISTENER = "addListener";

    private CallbackContext listenerCallback;
    private BroadcastReceiver nReceiver;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case ACTION_HAS_PERMISSION:
                isNotificationListerEnabled(callbackContext);
                return true;
            case ACTION_REQUEST_PERMISSION:
                requestPermissions();
                return true;
            case ACTION_IS_RUNNING:
                isServiceRunning(callbackContext);
                return true;
            case ACTION_TOGGLE:
                toggleService(callbackContext);
                return true;
            case ACTION_ADD_LISTENER:
                addListener(callbackContext);
                return true;
            default:
                return false;
        }
    }

    private void addProperty(JSONObject obj, String key, Object value) {
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException e) {
            LOG.d(LOG_TAG, "JSONException on put key: " + key + " value: " + value);
        }
    }

    private void isNotificationListerEnabled(CallbackContext callbackContext) {
        boolean enabled = isNotificationListerEnabled();
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, "hasPermission", enabled);
        callbackContext.success(returnObj);
    }

    private boolean isNotificationListerEnabled() {
        Context context = cordova.getActivity().getApplicationContext();
        String packageName = context.getPackageName();
        String listeners = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return listeners != null && listeners.contains(packageName);
    }

    private void requestPermissions() {
        showConfirmPermissionDialog();
    }

    private void showConfirmPermissionDialog() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    builder = new Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                } else {
                    builder = new Builder(cordova.getActivity());
                }
                builder.setTitle("Notification Access");
                builder.setMessage("Please enable notification access");
                builder.setCancelable(true);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openSettings();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                AlertDialog dialog = builder.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    textView.setTextDirection(View.TEXT_DIRECTION_LOCALE);
                }
            }
        };
        cordova.getActivity().runOnUiThread(runnable);
    }

    private void openSettings() {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            Context context = cordova.getActivity().getApplicationContext();
            context.startActivity(intent);
        } catch (Exception e) {
            LOG.d(LOG_TAG, e.getMessage());
        }
    }

    private void isServiceRunning(CallbackContext callbackContext) {
        boolean isRunning = isServiceRunning();
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, "isRunning", isRunning);
        callbackContext.success(returnObj);
    }

    private boolean isServiceRunning() {
        int myPid = android.os.Process.myPid();
        Context context = cordova.getActivity().getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.pid == myPid && AppNotificationService.class.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void toggleService(CallbackContext callbackContext) {
        toggleService();
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        callbackContext.sendPluginResult(result);
    }

    private void toggleService() {
        Context context = cordova.getActivity().getApplicationContext();
        PackageManager packageManager = cordova.getActivity().getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(context, AppNotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(new ComponentName(context, AppNotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void addListener(CallbackContext callbackContext) {
        if (listenerCallback != null) {
            callbackContext.error("Listener already exist.");
            return;
        }
        listenerCallback = callbackContext;
        nReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                parseNotification(intent);
            }
        };
        registerReceiver();
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void registerReceiver() {
        Context context = cordova.getActivity().getApplicationContext();
        context.registerReceiver(nReceiver, new IntentFilter(NOTIFY_CHANNEL));
    }

    private void unregisterReceiver() {
        Context context = cordova.getActivity().getApplicationContext();
        context.unregisterReceiver(nReceiver);
    }

    private void parseNotification(Intent intent) {
        StatusBarNotification sbn = intent.getParcelableExtra("sbn");
        Bundle extras = sbn.getNotification().extras;
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, "title", extras.getString("android.title"));
        addProperty(returnObj, "package", sbn.getPackageName());
        addProperty(returnObj, "postTime", sbn.getPostTime());
        addProperty(returnObj, "text", extras.getString("android.text"));
        addProperty(returnObj, "textLines", extras.getString("android.textLines"));
        PluginResult result = new PluginResult(PluginResult.Status.OK, returnObj);
        result.setKeepCallback(true);
        listenerCallback.sendPluginResult(result);
    }

    @Override
    public void onDestroy() {
        if (listenerCallback != null)
            listenerCallback = null;
        if (nReceiver != null) {
            unregisterReceiver();
            nReceiver = null;
        }
        super.onDestroy();
    }

}
