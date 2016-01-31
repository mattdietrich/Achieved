package ca.mdietr.achieved.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_REMINDER = "ACTION_REMINDER";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getName());
    }

    public static Intent createIntentReminderNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_REMINDER);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_REMINDER)) {
            triggerReminderNotification();
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
        PendingIntent pIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        manager.notify(NOTIFICATION_ID, notification);
    }


    private Goal getTodaysGoal() {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        return db.getGoal(today);

    }
}
