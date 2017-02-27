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
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PhoneActivity extends AppCompatActivity {
    private static final String TAG = "VerifyActivity";
    private static final int REQUEST_VERIFY = 2;

    private String _name, _email, _password;

    @BindView(R.id.input_phone)
    EditText _phoneText;
    @BindView(R.id.btn_next)
    Button _nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.bind(this);

        final PagingControl pageControl = (PagingControl) findViewById(R.id.page_control);
        pageControl.updateNumOfPages(4);
        pageControl.setPosition(1);

        Intent intent = getIntent();
        _name = intent.getStringExtra("Name");
        _email = intent.getStringExtra("Email");
        _password = intent.getStringExtra("Password");

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VERIFY) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }

    private void verify() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onPhoneFailed();
            return;
        }

        _nextButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(PhoneActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Wait...");
        progressDialog.show();

        String phone = _phoneText.getText().toString();

        // TODO: Implement your own signup logic here.
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

        Call<Result> call = service.Register(_email, lang, _name, hashPass, phone, _email, "SMS");
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
                        // On complete call either onPhoneSuccess or onPhoneFailed
                        // depending on success
                        onPhoneSuccess();
                        // onPhoneFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }


    private void onPhoneSuccess() {
        _nextButton.setEnabled(true);
        //setResult(RESULT_OK, null);

        // Start the Verify activity
        Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
        String phone = _phoneText.getText().toString();
        intent.putExtra("Phone", phone);
        intent.putExtra("Name", _name);
        intent.putExtra("Email", _email);
        intent.putExtra("Password", _password);
        startActivityForResult(intent, REQUEST_VERIFY);
        //finish();
    }

    private void onPhoneFailed() {
        Toast.makeText(getBaseContext(), "valid Phone number", Toast.LENGTH_LONG).show();

        _nextButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String phone = _phoneText.getText().toString();

        if (phone.isEmpty() || phone.length() < 10 || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            _phoneText.setError("at least 10 characters");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        return valid;
    }
}
