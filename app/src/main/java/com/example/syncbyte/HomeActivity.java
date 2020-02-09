package com.example.syncbyte;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Button button;
    boolean doubleBackToExitPressedOnce = false;
    private TextView emptyText;
    private FirebaseFirestore db;
    private CustomPreference customPreference;
    public static final String TAG = HomeActivity.class.getSimpleName();
    private List<ReportModal> dataList;
    private RecyclerView recyclerView;
    private RecyclerList adapter;
    private String loginedUserEmployeeId;
    private static final String DATABASE_ROOT = "syncbyte";
    private static final String DATABASE_TRANSACTION = "transaction";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        customPreference = new CustomPreference(this);
        loginedUserEmployeeId = customPreference.getLoginDetails().get(CustomPreference.KEY_EMPID);;

        db = FirebaseFirestore.getInstance();

        initView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(button.getText() == "Check In")
                    checkIn();
                else
                    checkOut();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),"Long Press to Delete",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                            dataList.remove(position);
                            adapter.notifyItemRemoved(position);

                            TextView check_in_time = (view.findViewById(R.id.checkInTimeFromDatabase));
                            long checkInTime = Long.parseLong(check_in_time.getText().toString());
                            removeItemFromDatabase(checkInTime);
                    }
                })
        );

    }

    private void removeItemFromDatabase(long time) {

        db.collection(DATABASE_ROOT)
                .document(loginedUserEmployeeId)
                .collection(DATABASE_TRANSACTION)
                .whereEqualTo("checkInTime",time)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                                    deleteDocument(queryDocumentSnapshot.getId());
                                }
                            }
                    }
                });

    }

    private void deleteDocument(String documentId){

        db.collection(DATABASE_ROOT)
                .document(loginedUserEmployeeId)
                .collection(DATABASE_TRANSACTION)
                .document(documentId)
                .delete();
    }

    private void initView() {

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        button = findViewById(R.id.transaction_button);
        emptyText = findViewById(R.id.emptyTextError);
        dataList = new ArrayList<>();


        adapter = new RecyclerList(dataList);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);



        if(customPreference.getIsCheckedIn() == 2){
            fetchingButtonText();
        }
        else if(customPreference.getIsCheckedIn() == 1 ) {

            button.setText("Check Out");
            button.setBackgroundColor(getColor(R.color.checkOutColor));

        }
        else if (customPreference.getIsCheckedIn()==0)
        {
            button.setText("Check In");
            button.setBackgroundColor(getColor(R.color.checkInColor));

        }

        updateRecyclerView();

    }


    //OnDouble Back Pressed Functionality
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();



        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(dataList.isEmpty())
            emptyText.setVisibility(View.VISIBLE);
        else
            emptyText.setVisibility(View.GONE);

    }


    public void fetchingButtonText(){

        db.collection(DATABASE_ROOT)
                .document(loginedUserEmployeeId)
                .collection(DATABASE_TRANSACTION)
                .whereEqualTo("checkedIn",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            updateButton(task.getResult().size());

                        } else {
                            Log.d("Home Activity", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void checkIn(){

        Long checkInTime = System.currentTimeMillis()/1000;

        TimeModal modal = new TimeModal(checkInTime,null,true);

        db.collection(DATABASE_ROOT)
                .document(loginedUserEmployeeId)
                .collection(DATABASE_TRANSACTION)
                .add(modal)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        customPreference.setLastCheckRef(documentReference.getId());
                        customPreference.setIsCheckedIn(1);


                        updateButton(1);

                    }
                });
    }

    private void checkOut(){

            final String docRef = customPreference.getLastCheckRef();

            db.collection(DATABASE_ROOT)
                    .document(loginedUserEmployeeId)
                    .collection(DATABASE_TRANSACTION)
                    .whereEqualTo("checkedIn",true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                if(task.getResult().size()==1){

                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        updateCheckOutTime(document.getId());
                                    }

                                    adapter.notifyDataSetChanged();

                                }
                            } else {
                                Log.d("Home Activity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
    }

    private void updateRecyclerView(){

         db.collection(DATABASE_ROOT)
                .document(loginedUserEmployeeId)
                .collection(DATABASE_TRANSACTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots,FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                            emptyText.setVisibility(View.GONE);

                            switch (dc.getType()) {
                                case ADDED:


                                    if(!dc.getDocument().getBoolean("checkedIn")){

                                        long checkInTime = dc.getDocument().getLong("checkInTime");
                                        long checkOutTime = dc.getDocument().getLong("checkOutTime");
                                        notifyRecycler(checkInTime,checkOutTime);
                                    }
                                    break;
                                    case MODIFIED:

                                    if(!dc.getDocument().getBoolean("checkedIn")){

                                        long checkInTime = dc.getDocument().getLong("checkInTime");
                                        long checkOutTime = dc.getDocument().getLong("checkOutTime");
                                        notifyRecycler(checkInTime,checkOutTime);
                                    }
                                    break;
                            }
                        }
                    }
                });


    }

    private void notifyRecycler(long checkInTime,long checkOutTime){

        String checkedInDate = TimeModal.getDate(checkInTime);
        String checkedInTime = TimeModal.getTime(checkInTime);
        String checkedOutDate = TimeModal.getDate(checkOutTime);
        String checkedOutTime = TimeModal.getTime(checkOutTime);
        ReportModal reportModal = new ReportModal(checkedInDate,checkedInTime,checkedOutDate,checkedOutTime,checkInTime);
        dataList.add(reportModal);

        adapter.notifyDataSetChanged();

    }

    // Updating Check Out Time
    private void updateCheckOutTime(String id) {

        Long checkOutTime = System.currentTimeMillis()/1000;

        db.collection(DATABASE_ROOT)
                    .document(loginedUserEmployeeId)
                    .collection(DATABASE_TRANSACTION)
                    .document(id)
                    .update(
                            "checkOutTime" , checkOutTime,
                            "checkedIn",false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            customPreference.setIsCheckedIn(0);
                            customPreference.setLastCheckRef(null);

                            updateButton(0);
                        }
                    });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
               startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                break;
            default:
        }
        return true;
    }

    // i = 1 = CheckedIn , i= 0 = CheckedOut
    private void updateButton(int i){

        if(i == 1){

            button.setText("Check Out");
            button.setBackgroundColor(getColor(R.color.checkOutColor));

        }else{

            button.setText("Check In");
            button.setBackgroundColor(getColor(R.color.checkInColor));
        }
    }

}

