package com.example.syncbyte;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private EditText empcode,name,number,email,password,dob;
    private Button submit;

    //Getting Current Year, Month,Day
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month  = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    private FirebaseFirestore db;
    private CustomPreference customPreference;
    private KProgressHUD  progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();
        customPreference = new CustomPreference(this);
        progressHUD = new KProgressHUD(this);

        initViews();

        //Open Date Picker
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                checkIfUserExist();
            }
        });

    }

    //Check If User Exist with Entererd Employee Code or not
    private void checkIfUserExist(){

        db.collection("syncbyte")
                .document(empcode.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                            toast("Employee Id Already Exist");
                        else
                            submitData();
                    }
                });

    }

    //On Submit User Button Clicked
    private void submitData() {
        //Submit data to FireStore

        setProgressIndicator("Registering User");

        UserModal userModal = new UserModal(
                name.getText().toString().trim(),
                empcode.getText().toString().trim(),
                email.getText().toString().trim(),
                number.getText().toString().trim(),
                dob.getText().toString().trim(),
                password.getText().toString().trim());

        db.collection("syncbyte")
                .document(userModal.getEmpCode())
                .set(userModal)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            hideProgressIndicator();

                            String empId = empcode.getText().toString().trim();
                            String username =   name.getText().toString().trim();
                            String phone = number.getText().toString().trim();
                            String emailId = email.getText().toString().trim();
                            String pass = password.getText().toString().trim();

                            customPreference.createLoginSession(
                                    empId,
                                    username,
                                    phone,
                                    emailId,
                                    pass
                            );
                            toast("Registered Successfully");
                            finish();

                        }else{

                            toast("Failed: "+task.getException().getMessage());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                hideProgressIndicator();
                toast("Failed: "+e.getMessage());
            }
        });
    }

    //Initialization of the Viewa
    private void initViews() {

        empcode = findViewById(R.id.signup_empcode);
        name = findViewById(R.id.signup_name);
        number = findViewById(R.id.signup_phone);
        email = findViewById(R.id.signup_email);
        dob =  findViewById(R.id.signup_dob);
        password = findViewById(R.id.signup_password);
        submit = findViewById(R.id.signup_button);

    }

    //Date Picker Dialog
    private void openDatePicker(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Date date = new Date(year-1900,month,day);
                        dob.setText(formateDate(date));
                    }
                }, year, month, day);

        datePickerDialog.show();
    }


    //Function to convert Date object to String
    @org.jetbrains.annotations.NotNull
    private String formateDate(Date date){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    //Custom Progress Indicator
    private void setProgressIndicator(String msg){

        progressHUD.create(SignUpActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel(msg)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
    private void hideProgressIndicator(){
        if(progressHUD.isShowing())
            progressHUD.dismiss();
    }

    //Form Validation
    private boolean validate(){

        String username = name.getText().toString();
        String mobile = number.getText().toString();
        String emailId = email.getText().toString();
        String empId = empcode.getText().toString();
        String pass = password.getText().toString();
        String birthDate = dob.getText().toString();

        if(username.isEmpty() || mobile.isEmpty() || emailId.isEmpty() || empId.isEmpty() || pass.isEmpty() || birthDate.isEmpty()){

            toast("All Fields Are Mandatory");
            return false;
        }

        return true;
    }

    //Function to Display Toast Message
    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
