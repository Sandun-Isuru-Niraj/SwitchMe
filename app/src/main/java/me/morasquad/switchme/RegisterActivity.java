package me.morasquad.switchme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {


    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        loadingbar = new ProgressDialog(this);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUsertoMainActivity();
        }

    }

    private void SendUsertoMainActivity() {

        Intent main = new Intent(RegisterActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();

    }


    private void CreateNewAccount() {


        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirm_password = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Your Email!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirm_password)){
            Toast.makeText(this, "Please Confirm your Password!", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(confirm_password)){
            Toast.makeText(this, "Your Passwords do not Matched!", Toast.LENGTH_SHORT).show();

        }

        else {
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please wait, while we are creating your new Account!");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                SendUsertoAddDeviceActivity();
                                Toast.makeText(RegisterActivity.this, "You are Authenticated Successfully!", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error Occurred! "+message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });

        }
    }

    private void SendUsertoAddDeviceActivity() {
        Intent add = new Intent(RegisterActivity.this, AddDeviceActivity.class);
        add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(add);
        finish();

    }
}
