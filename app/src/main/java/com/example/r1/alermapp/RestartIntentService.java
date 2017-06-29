package com.example.r1.alermapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RestartIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.r1.alermapp.action.FOO";
      // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.r1.alermapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.r1.alermapp.extra.PARAM2";

    public RestartIntentService() {
        super("RestartIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, RestartIntentService.class);
        intent.setAction(ACTION_FOO);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                handleActionFoo();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
       if (SamplePeriodicService.isServiceRunning()) {
           SamplePeriodicService.stopResidentIfActive(getApplicationContext());
       }
       new SamplePeriodicService().startResident(getApplicationContext());
    }

}
