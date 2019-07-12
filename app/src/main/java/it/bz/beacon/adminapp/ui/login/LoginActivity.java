package it.bz.beacon.adminapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.api.AuthControllerApi;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationRequest;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationToken;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    protected TextInputEditText editUsername;

    @BindView(R.id.password)
    protected TextInputEditText editPassword;

    @BindView(R.id.usernameContainer)
    protected TextInputLayout containerUsername;

    @BindView(R.id.passwordContainer)
    protected TextInputLayout containerPassword;

    @BindView(R.id.login_progress)
    protected LinearLayout progress;

    @BindView(R.id.login_form)
    protected View loginForm;

    private Storage storage;
    private AuthControllerApi authControllerApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = AdminApplication.getStorage();
        authControllerApi = AdminApplication.getAuthApi();

        if (!TextUtils.isEmpty(storage.getLoginUserToken())) {
            openMain();
        }

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

        AdminApplication.hideKeyboard(this);
        editUsername.setError(null);
        editPassword.setError(null);

        String username = (editUsername.getText() != null) ? editUsername.getText().toString() : "";
        String password = (editPassword.getText() != null) ? editPassword.getText().toString() : "";

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || (!isPasswordValid(password))) {
            editPassword.setError(getString(R.string.error_invalid_password));
            focusView = editPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            editUsername.setError(getString(R.string.error_field_required));
            focusView = editUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            doLogin(username, password);
        }
    }

    private void doLogin(final String username, final String password) {
        try {
            AuthenticationRequest request = new AuthenticationRequest();
            request.setUsername(username);
            request.setPassword(password);
            showProgress(true);

            authControllerApi.signinUsingPOSTAsync(request, new ApiCallback<AuthenticationToken>() {

                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showProgress(false);
                            if ((statusCode == 403) || (statusCode == 401)) {
                                Snackbar.make(containerUsername, getString(R.string.error_incorrect_login), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            else {
                                Snackbar.make(containerUsername, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                                        .setAction(getString(R.string.retry), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                attemptLogin();
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                }

                @Override
                public void onSuccess(AuthenticationToken result, int statusCode, Map<String, List<String>> responseHeaders) {
                    storage.setUser(username, password, result.getToken());
                    AdminApplication.setBearerToken(result.getToken());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showProgress(false);
                            openMain();
                        }
                    });
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        }
        catch (Exception e) {
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void openMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
