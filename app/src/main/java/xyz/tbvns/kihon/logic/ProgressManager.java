package xyz.tbvns.kihon.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.MainThread;
import lombok.Getter;
import xyz.tbvns.kihon.MainActivity;
import xyz.tbvns.kihon.R;
import xyz.tbvns.kihon.activity.ProgressActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProgressManager {
    private static ProgressManager instance;
    private ProgressActivity currentActivity;
    private final Handler mainHandler;

    @Getter
    private int currentProgress = 0;
    @Getter
    private String currentTitle;
    @Getter
    private String currentMessage;
    @Getter
    private String currentTask;
    @Getter
    private int currentItems = 0;
    @Getter
    private int totalItems = 0;
    @Getter
    private boolean isFinished = false;
    @Getter
    private Context context;

    private ProgressManager(Context context) {
        mainHandler = new Handler(Looper.getMainLooper());
        this.context = context.getApplicationContext();
        reset();
    }

    public static synchronized ProgressManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProgressManager(context);
        }
        return instance;
    }

    public void startProgress(Context context, String title, String subtitle) {
        reset();
        currentTitle = title;
        currentMessage = subtitle;
        Intent intent = new Intent(context, ProgressActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @MainThread
    public void registerActivity(ProgressActivity activity) {
        this.currentActivity = activity;
        if (activity != null) {
            activity.reset();
            activity.setProgressBar(currentProgress);
            activity.setTitle(currentTitle);
            activity.setMessage(currentMessage);
            activity.setCurrentTask(currentTask);
            activity.setItemsCount(currentItems, totalItems);
        }
    }

    @MainThread
    public void unregisterActivity() {
        this.currentActivity = null;
    }

    public void updateProgress(int progress) {
        progress = Math.max(0, Math.min(100, progress));
        currentProgress = progress;

        int finalProgress = progress;
        mainHandler.post(() -> {
            if (currentActivity != null) {
                currentActivity.setProgressBar(finalProgress);
            }
        });
    }

    public void updateMessage(String message) {
        if (message == null || message.isEmpty()) return;
        currentMessage = message;
        
        mainHandler.post(() -> {
            if (currentActivity != null) {
                currentActivity.setMessage(message);
            }
        });
    }

    public void setCurrentTask(String task) {
        if (task == null || task.isEmpty()) return;
        currentTask = task;
        
        mainHandler.post(() -> {
            if (currentActivity != null) {
                currentActivity.setCurrentTask(task);
            }
        });
    }

    public void setItemsCount(int current, int total) {
        currentItems = current;
        totalItems = total;
        
        mainHandler.post(() -> {
            if (currentActivity != null) {
                currentActivity.setItemsCount(current, total);
            }
        });
    }

    public void incrementProgress(int delta) {
        updateProgress(currentProgress + delta);
    }

    public void incrementItems() {
        setItemsCount(currentItems + 1, totalItems);
    }

    public void finishProgress() {
        isFinished = true;
        mainHandler.post(() -> {
            if (currentActivity != null) {
                currentActivity.finish();
            }
        });

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("manage", true);
        context.startActivity(intent);
    }
    public synchronized void reset() {
        currentProgress = 0;
        currentTitle = context.getString(R.string.progress_processing);
        currentMessage = context.getString(R.string.progress_initializing);
        currentTask = context.getString(R.string.progress_initializing);
        currentItems = 0;
        totalItems = 0;
        isFinished = false;
    }
}
