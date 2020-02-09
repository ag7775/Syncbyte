package com.example.syncbyte;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout editProfile,generateReport;
    private TextView username,empId;
    private Button logout;
    private CustomPreference customPreference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        customPreference = new CustomPreference(this);
        db  = FirebaseFirestore.getInstance();


        initViews();

        //onClick Listener for Layout Generate Report
        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReportContent();
            }
        });

        //Logout Button OnClick Listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPreference.logoutUser();
            }
        });

        //EditProfile Layout Listener
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,EditProfileActivity.class));
            }
        });
    }

    //Initialization of Views
    private void initViews() {

        editProfile = findViewById(R.id.setting_editProfile);
        generateReport = findViewById(R.id.setting_report);
        logout = findViewById(R.id.logout);
        empId = findViewById(R.id.setting_id);
        username = findViewById(R.id.setting_name);

        //SetViews
        HashMap<String,String> loginDetails = customPreference.getLoginDetails();
        username.setText(loginDetails.get(CustomPreference.KEY_NAME));
        empId.setText(loginDetails.get(CustomPreference.KEY_EMPID));

    }


    //Get the List of data which is checkedIn and checkedOut for Current User
    private void getReportContent(){


        final List<ReportModal> reportList = new ArrayList<ReportModal>();

        db.collection("syncbyte")
                .document(customPreference.getLoginDetails().get(CustomPreference.KEY_EMPID))
                .collection("transaction")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot documentSnapshot  : queryDocumentSnapshots){

                                if(!documentSnapshot.getBoolean("checkedIn")){


                                    long checkInTime = documentSnapshot.getLong("checkInTime");
                                    long checkOutTime = documentSnapshot.getLong("checkOutTime");

                                    String checkedInDate = TimeModal.getDate(checkInTime);
                                    String checkedInTime = TimeModal.getTime(checkInTime);
                                    String checkedOutDate = TimeModal.getDate(checkOutTime);
                                    String checkedOutTime = TimeModal.getTime(checkOutTime);

                                    ReportModal reportModal = new ReportModal(checkedInDate,checkedInTime,checkedOutDate,checkedOutTime);

                                    reportList.add(reportModal);

                                }
                            }
                            saveExcelFile(customPreference.getLoginDetails().get(CustomPreference.KEY_EMPID),reportList);
                        }
                        else{

                            Toast.makeText(getApplicationContext(),"No Records Found",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Generate the report from that Date
    private void saveExcelFile(String fileName,List<ReportModal> reportModalList){

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(getApplicationContext(), "Storage not available or read only",Toast.LENGTH_SHORT).show();
            return;
        }

        if(reportModalList.isEmpty()){
            Toast.makeText(getApplicationContext(),"Empty List",Toast.LENGTH_SHORT).show();
            return;
        }


        boolean success = false;

        //New Workbook

        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet(customPreference.getLoginDetails().get(CustomPreference.KEY_EMPID));

        /* Generate column headings */
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("CheckInDate");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("CheckInTime");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("CheckOutDate");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("CheckOutTime");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));

        int val = 0;
        int k = 1;
        for(int i=0;i<reportModalList.size();i++){

            row = sheet1.createRow(k);

            for(int j=0;j<4;j++){

                c = row.createCell(j);

                switch (j){

                    case 0 : c.setCellValue(reportModalList.get(i).getCheckInDate());
                        break;

                    case 1 : c.setCellValue(reportModalList.get(i).getCheckInTime());
                        break;

                    case 2 : c.setCellValue(reportModalList.get(i).getCheckOutDate());
                        break;

                    case 3 : c.setCellValue(reportModalList.get(i).getCheckOutTime());
                        break;

                    default: c.setCellValue("null");
                        break;
                }

                c.setCellStyle(cs);

            }
            sheet1.setColumnWidth(i, (15 * 500));
            k++;
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/EXCEL/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(getApplicationContext().getExternalFilesDir(null), fileName+".xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Toast.makeText(getApplicationContext(), "File Saved: " + file, Toast.LENGTH_LONG).show();
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
