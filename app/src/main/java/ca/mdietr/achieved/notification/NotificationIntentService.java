package ca.mdietr.achieved.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

import ca.mdietr.achieved.MainActivity;
import ca.mdietr.achieved.R;
import ca.mdietr.achieved.database.DatabaseAccessObject;
import ca.mdietr.achieved.model.Goal;

/**
 * Created by Matt on 2016-01-27.
 */
public class NotificationIntentService extends IntentService {

    private static final int SET_GOAL_NOTIFICATION_ID = 0;
    private static final int REMINDER_NOTIFICATION_ID = 1;
    private static final String ACTION_SET_GOAL = "ACTION_SET_GOAL";
    private static final String ACTION_REMINDER = "ACTION_REMINDER";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getName());
    }

    public static Intent createIntentReminderNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_REMINDER);
        return intent;
    }

    public static Intent createIntentSetGoalNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_SET_GOAL);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_REMINDER)) {
            triggerReminderNotification();
        } else if (action.equals(ACTION_SET_GOAL)) {
            triggerSetGoalNotification();
        }

    }

    private void triggerReminderNotification() {
        String goalText = "Achieve your Goals!";
        String rewardText = "You can do it!";
        Goal goal = getTodaysGoal();
        if (goal != null) {
            goalText = goal.getText();
            if (goal.getReward() != null && !goal.getReward().equals(""))
                rewardText = "Reward: " + goal.getReward();
        }

        Intent mainIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, REMINDER_NOTIFICATION_ID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_action_confirm, "Achieve", pIntent);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Notification notification = builder
                .setContentIntent(pIntent)
                .setContentTitle(goalText)
                .setContentText(rewardText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(getResources().getColor(R.color.primary))
                .setSound(sound)
                .setAutoCancel(true)
                .addAction(action)
                .build();

        final NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(REMINDER_NOTIFICATION_ID, notification);
    }

    private void triggerSetGoalNotification() {
        Intent mainIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, SET_GOAL_NOTIFICATION_ID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_action_new, "Set Goal", pIntent);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Notification notification = builder
                .setContentIntent(pIntent)
                .setContentTitle("Set Today's Goal")
                .setContentText("What will you achieve today?")
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(getResources().getColor(R.color.primary))
                .setSound(sound)
                .setAutoCancel(true)
                .addAction(action)
                .build();

        final NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(SET_GOAL_NOTIFICATION_ID, notification);

        // Set the next notification alarm (if necessary), taking the time from shared preferences
        SharedPreferences spref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_name), MODE_PRIVATE);

        // If the reminder is not enabled in shared preferences, then don't set it
        if (!spref.getBoolean(getString(R.string.shared_preferences_set_goal_enabled),false))
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, spref.getInt(getString(R.string.shared_preferences_set_goal_hour), 0));
        calendar.set(Calendar.MINUTE, spref.getInt(getString(R.string.shared_preferences_set_goal_minute), 0));
        calendar.set(Calendar.SECOND, 0);

        Intent serviceIntent = NotificationIntentService.createIntentSetGoalNotification(getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private Goal getTodaysGoal() {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        return db.getGoal(today);

    }
}
