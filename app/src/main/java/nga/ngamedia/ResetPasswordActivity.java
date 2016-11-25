package nga.ngamedia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText     eEmail;
    private Button       btnRest;
    private ProgressBar  progressBar;
    private View         resetView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Set UI
        eEmail      = (EditText) findViewById(R.id.reset_email);
        btnRest     = (Button) findViewById(R.id.reset_button);
        progressBar = (ProgressBar) findViewById(R.id.reset_progress);
        resetView   = findViewById(R.id.reset_form);

        // Set firebase auth
        mAuth = FirebaseAuth.getInstance();

        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = eEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    eEmail.setError(getString(R.string.error_field_required));
                    eEmail.requestFocus();
                    return;
                }
                showProgress(true);
                mAuth.sendPasswordResetEmail(email)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 Toast.makeText(ResetPasswordActivity.this,
                                                "We've sent you an email to reset your password",
                                                Toast.LENGTH_LONG)
                                      .show();
                                 finish();
                             } else {
                                 Toast.makeText(ResetPasswordActivity.this,
                                                "Failed to send reset email, please try again later.",
                                                Toast.LENGTH_LONG)
                                      .show();
                                 finish();
                             }
                             showProgress(true);
                         }
                     });
            }
        });
    }

    /**
     * Shows the progress UI and hides the signin form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            resetView.setVisibility(show ? View.GONE : View.VISIBLE);
            resetView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            resetView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
