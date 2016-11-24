package nga.ngamedia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A signin screen that offers signin via email/password.
 */
public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity.java";

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View     mProgressView;
    private View     mSigninFormView;
    private Button   mSignInButton;
    private TextView mSignupView;
    private TextView mForgetView;

    // Firebase auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Set up the Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Set up the signin form.
        mEmailView      = (EditText) findViewById(R.id.email);
        mPasswordView   = (EditText) findViewById(R.id.password);
        mSigninFormView = findViewById(R.id.signin_form);
        mSignInButton   = (Button) findViewById(R.id.email_sign_in_button);
        mForgetView     = (TextView) findViewById(R.id.forget_text);
        mSignupView     = (TextView)findViewById(R.id.signup_text);
        mProgressView   = findViewById(R.id.signin_progress);

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignin();
            }
        });
        /*mForgetView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Start SignupActivity
                Intent intent = new Intent(getApplicationContext(), ForgetActivity.class);
                startActivity(intent);
            }
        });*/

        mSignupView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Start SignupActivity
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the signin form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual signin attempt is made.
     */
    private void attemptSignin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the signin attempt.
        String email    = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt signin and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user signin attempt.
            showProgress(true);
            userSignin(email, password);
        }
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

            mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSigninFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void userSignin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(SigninActivity.this,
                                    "Sign in failed, please try again...",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SigninActivity.this,
                                    "Welcome to your movie world!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        }
                });

    }

}

