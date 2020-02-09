package com.example.syncbyte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private EditText phone,email,password;
    private TextView username,empId;
    private CustomPreference customPreference;
    private Button save;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_profile);

        db = FirebaseFirestore.getInstance();
        customPreference = new CustomPreference(this);
        initViews();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = phone.getText().toString().trim();
                String emailId = email.getText().toString().trim();
                String passWord = password.getText().toString().trim();

                updateData(number,emailId,passWord);

            }
        });

    }

    private void updateData(final String number, final String emailId, final String passWord) {

        db.collection("syncbyte")
                .document(customPreference.getLoginDetails().get(CustomPreference.KEY_EMPID))
                .update(
                        "number", number,
                        "email", emailId,
                        "password", passWord
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                customPreference.updateDetails(number,emailId,passWord);
                toast("Profile Updated");
                finish();

            }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("Error: "+e.getMessage());
                    }
                });
    }

    private void initViews() {

        //Get Views
        email = findViewById(R.id.editProfileEmail);
        phone = findViewById(R.id.editProfileNumber);
        username = findViewById(R.id.editProfileName);
        empId = findViewById(R.id.editProfileId);
        password = findViewById(R.id.editProfilePassword);
        save = findViewById(R.id.editProfileSave);

        //Set Views

        HashMap<String,String> loginDetails = customPreference.getLoginDetails();

        email.setText(loginDetails.get(CustomPreference.KEY_EMAIL));
        phone.setText(loginDetails.get(CustomPreference.KEY_NUMBER));
        password.setText(loginDetails.get(CustomPreference.KEY_PASSWORD));
        username.setText(loginDetails.get(CustomPreference.KEY_NAME));
        empId.setText(loginDetails.get(CustomPreference.KEY_EMPID));

    }
    private void toast(String msg){

        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
