package it.bz.beacon.adminapp.ui.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    protected EditText editUsername;

    @BindView(R.id.password)
    protected EditText editPassword;

    @BindView(R.id.login_progress)
    protected LinearLayout progress;

    @BindView(R.id.login_form)
    protected View loginForm;

    private Storage mStorage;
//    private AuthApi mAuthApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.email_sign_in_button)
    public void signIn(View view) {
        attemptLogin();
    }

    private void attemptLogin() {

        editUsername.setError(null);
        editPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            editPassword.setError(getString(R.string.error_invalid_password));
            focusView = editPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            editUsername.setError(getString(R.string.error_field_required));
            focusView = editUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            doLogin(username, password);
        }
    }

    private class FakeLoginTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            openMain();
        }
    }

    private void doLogin(final String username, final String password) {
        try {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            // TODO: do real login here
            FakeLoginTask task = new FakeLoginTask();
            task.execute();

//            Login login = new Login();
//            login.setUsername(username);
//            login.setPassword(password);
//            mAuthApi.routingV1AuthLoginPostAsync(login, new ApiCallback<FeUser>() {
//                @Override
//                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            showProgress(false);
//                            Toast.makeText(LoginActivity.this, getString(R.string.error_incorrect_login), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onSuccess(final FeUser result, int statusCode, Map<String, List<String>> responseHeaders) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//
////                                mStorage.setUser(result, username, password, isAdmin, isTechnician);
//                                openMain();
//                                showProgress(false);
//                        }
//
//                    });
//                }
//
//                @Override
//                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {}
//
//                @Override
//                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {}
//            });
        } catch (Exception e) {
            Log.d("Beacon", e.getMessage());
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
//        loginForm.animate().setDuration(shortAnimTime).alpha(
//                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
//        });

        progress.setVisibility(show ? View.VISIBLE : View.GONE);
//        progress.animate().setDuration(shortAnimTime).alpha(
//                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                progress.setVisibility(show ? View.VISIBLE : View.GONE);
//            }
//        });
    }

    protected void openMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}

