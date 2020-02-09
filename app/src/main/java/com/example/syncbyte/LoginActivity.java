package com.example.syncbyte;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private EditText empcode,password;
    private Button login;
    private FirebaseFirestore db;
    private ImageView fingerPrintImage;
    private TextView signUpText;
    public static final String TAG = LoginActivity.class.getSimpleName();
    CustomPreference customPreference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        customPreference = new CustomPreference(LoginActivity.this);

        initViews();

        executor = ContextCompat.getMainExecutor(this);

        //Fingerprint auth implementation
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                if(errorCode == BiometricPrompt.ERROR_CANCELED){
                    biometricPrompt.cancelAuthentication();
                }
                toast("Authentication Error: "+errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                if(customPreference.getLoginDetails().get(CustomPreference.KEY_EMPID)!= null){

                    navigateToNextScreen();

                }else{

                    toast("First Time User? Try Login With Password",Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                toast("Application did not recognize the placed finger print. Please try again!");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setSubtitle("Login into your account")
                .setDescription("Touch your finger on the finger print sensor to authorise your account.")
                .setNegativeButtonText("Login With Password")
                .build();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate())
                    loginUser();
            }
        });

        fingerPrintImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                biometricPrompt.authenticate(promptInfo);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

    }

    //Move to Next Screen Function
    private void navigateToNextScreen(){

        Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Login user
    private void loginUser() {

        db.collection("syncbyte")
                .document(empcode.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if(document.exists()){

                                String  inputPassword = password.getText().toString();
                                String  correctPassword = document.getString("password");

                                if (correctPassword.equals(inputPassword)){

                                    String empCode = document.getString("empCode");
                                    String name = document.getString("username");
                                    String email = document.getString("email");
                                    String phone = document.getString("number");

                                    customPreference.createLoginSession(
                                            empCode,
                                            name,
                                            phone,
                                            email,
                                            correctPassword
                                    );

                                    navigateToNextScreen();

                                }else{

                                    toast("Incorrect Password",Toast.LENGTH_LONG);
                                    return;
                                }
                            }else{
                                toast("Employee Id does not exist",Toast.LENGTH_LONG);
                            }
                        }else{

                            toast("Failed: "+ task.getException().getMessage(),Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    //Initialization of Views
    private void initViews() {
        empcode  = findViewById(R.id.empcode);
        password = findViewById(R.id.signup_password);
        login = findViewById(R.id.login_button);
        fingerPrintImage = findViewById(R.id.fingerPrintImage);
        signUpText = findViewById(R.id.signup_text);

    }

    //Form Validations
    private boolean validate(){

        if(empcode.getText().toString().isEmpty()){

            toast("Please Enter Employee Id");
            return false;
        }
        else if(password.getText().toString().isEmpty()){

            toast("Please Enter Password");
            return false;
        }

        return true;
    }

    //Toast for Short Duration
    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    //Toast for Long Duration
    private void toast(String msg, int duration){
        Toast.makeText(this,msg,duration).show();
    }
}
