package com.almende.askfast.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class VerifyActivity extends AppCompatActivity {
    private static final String TAG = "VerifyActivity";
    private static final int REQUEST_WELCOME = 3;

    @BindView(R.id.input_code) EditText _codeText;
    @BindView(R.id.btn_next) Button _nextButton;
    @BindView(R.id.link_resend) TextView _resendLink;
    @BindView(R.id.verify_title) TextView _verifyTitle;

    private String _phoone, _name, _email, _password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.bind(this);

        final PagingControl pageControl = (PagingControl) findViewById(R.id.page_control);
        pageControl.updateNumOfPages(4);
        pageControl.setPosition(2);

        Intent intent = getIntent();
        _phoone = intent.getStringExtra("Phone");
        _name = intent.getStringExtra("Name");
        _email = intent.getStringExtra("Email");
        _password = intent.getStringExtra("Password");
        _verifyTitle.setText(String.format(getResources().getString(R.string.verify_title), _phoone));

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        _resendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resend Process
                resend_request();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_WELCOME) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }

    private void resend_request() {
        // Get Country Code
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        String lang = telephonyManager.getSimCountryIso();
        Log.d("Country", lang);
        Locale locale = this.getResources().getConfiguration().locale;
        lang = locale.toString();//.getDisplayName();
        Log.d("Country", lang);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ASKFastAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ASKFastAPI service = retrofit.create(ASKFastAPI.class);

        String hashPass = new MD5Hash(_password).getMD5();

        Call<Result> call = service.Register(_email, lang, _name, hashPass, _phoone, _email, "SMS");
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
    }

    private void verify() {
        Log.d(TAG, "Verify");

        if (!validate()) {
            onVerifyFailed();
            return;
        }

        _nextButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(VerifyActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying...");
        progressDialog.show();

        String code = _codeText.getText().toString();

        // TODO: Implement your own signup logic here.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ASKFastAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ASKFastAPI service = retrofit.create(ASKFastAPI.class);

        Call<Result> call = service.RegisterVerify(code);
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
                        // On complete call either onVerifySuccess or onVerifyFailed
                        // depending on success
                        onVerifySuccess();
                        // onVerifyFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    private void onVerifySuccess() {
        _nextButton.setEnabled(true);
        //setResult(RESULT_OK, null);

        // Start the welcome activity
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        intent.putExtra("Email", _email);
        intent.putExtra("Password", _password);
        startActivityForResult(intent, REQUEST_WELCOME);
        //finish();
    }

    private void onVerifyFailed() {
        Toast.makeText(getBaseContext(), "Verify failed", Toast.LENGTH_LONG).show();

        _nextButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String code = _codeText.getText().toString();

        if (code.isEmpty() || code.length() < 6) {
            _codeText.setError("at least 6 characters");
            valid = false;
        } else {
            _codeText.setError(null);
        }

        return valid;
    }
}
