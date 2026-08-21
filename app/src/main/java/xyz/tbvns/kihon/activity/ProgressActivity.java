package xyz.tbvns.kihon.activity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import xyz.tbvns.kihon.R;
import xyz.tbvns.kihon.logic.ProgressManager;


public class ProgressActivity extends AppCompatActivity {

    private ProgressBar progressSpinner;
    private ProgressBar progressBar;
    private TextView progressTitle;
    private TextView progressMessage;
    private TextView progressPercentage;
    private TextView progressCurrentTask;
    private TextView progressItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        progressSpinner = findViewById(R.id.progress_spinner);
        progressBar = findViewById(R.id.progress_bar);
        progressTitle = findViewById(R.id.progress_title);
        progressMessage = findViewById(R.id.progress_message);
        progressPercentage = findViewById(R.id.progress_percentage);
        progressCurrentTask = findViewById(R.id.progress_current_task);
        progressItems = findViewById(R.id.progress_items);

        setFinishOnTouchOutside(false);

        ProgressManager.getInstance(getApplicationContext()).registerActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProgressManager.getInstance(getApplicationContext()).unregisterActivity();
    }

    @Override
    public void onBackPressed() {
        if (!ProgressManager.getInstance(getApplicationContext()).isFinished()) {
            return;
        }
        super.onBackPressed();
    }

    public void setProgressBar(int progress) {
        progress = Math.max(0, Math.min(100, progress));
        progressBar.setProgress(progress);
        progressPercentage.setText(getString(R.string.progress_percentage_format, progress));
    }

    /**
     * Update the title message
     */
    public void setTitle(String title) {
        progressTitle.setText(title);
    }

    public void setMessage(String message) {
        progressMessage.setText(message);
    }

    public void setCurrentTask(String task) {
        progressCurrentTask.setText(task);
    }

    public void setItemsCount(int current, int total) {
        progressItems.setText(getString(R.string.progress_items_format, current, total));
    }

    public void reset() {
        progressBar.setProgress(0);
        progressPercentage.setText(R.string.progress_zero_percent);
        progressTitle.setText(R.string.progress_processing);
        progressMessage.setText(R.string.progress_initializing);
        progressCurrentTask.setText(R.string.progress_initializing);
        progressItems.setText(R.string.progress_zero_items);
    }
}
