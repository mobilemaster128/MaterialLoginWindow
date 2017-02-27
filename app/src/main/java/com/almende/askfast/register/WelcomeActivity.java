package com.almende.askfast.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;


public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "VerifyActivity";
    private String _email, _password;

    @BindView(R.id.btn_next)
    Button _nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        final PagingControl pageControl = (PagingControl) findViewById(R.id.page_control);
        pageControl.updateNumOfPages(4);
        pageControl.setPosition(3);

        Intent intent = getIntent();
        _email = intent.getStringExtra("Email");
        _password = intent.getStringExtra("Password");

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcome();
            }
        });
    }

    private void welcome() {
        Log.d(TAG, "Welcome");

        _nextButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(WelcomeActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Wait...");
        progressDialog.show();

        // TODO: Implement your own signup logic here.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ASKFastAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ASKFastAPI service = retrofit.create(ASKFastAPI.class);

        String hashPass = new MD5Hash(_password).getMD5();

        Call<Result> call = service.Login(hashPass, _email);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                Log.d("Login", "Succeed");//response.body().toString());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Error", "error");
            }
        });
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onWelcomeSuccess();
                        // onWelcomeFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    private void onWelcomeSuccess() {
        _nextButton.setEnabled(true);
        setResult(RESULT_OK, null);

        finish();
    }

    private void onWelcomeFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _nextButton.setEnabled(true);
    }
}
