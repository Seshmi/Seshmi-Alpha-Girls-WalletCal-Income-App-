package com.example.daytoday;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daytoday.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpenseManage extends AppCompatActivity {

    private FirebaseRecyclerOptions<Data> options;
    private FirebaseRecyclerAdapter<Data, dataRetrive> adapter;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private TextView total;

    private FloatingActionButton fabButton;

    private RelativeLayout income_nav,expense_nav,debt_nav,todolist_nav;

    DatabaseReference reff;
    Data data;

    private String type;
    private float amount;
    private  String note;
    private  String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser == null ){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        String uid = mUser.getUid();

        Data data;

        recyclerView = findViewById(R.id.receive);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        total = findViewById(R.id.totExpense);

        fabButton = findViewById(R.id.fab);

        reff= FirebaseDatabase.getInstance().getReference("Expense").child(uid);
        reff.keepSynced(true);

        //Total expense counter

        reff.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totExpense = 0;

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Data data = dataSnapshot.getValue(Data.class);

                    totExpense +=data.getAmount();
                }
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String fTotExpense = decimalFormat.format(totExpense);

                total.setText(String.valueOf(fTotExpense));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //SIGNOUT BUTTON

        ImageView menu_btn = findViewById(R.id.menu_btn);

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        //back button

        ImageView back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });

        //floting button
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog();
            }
        });

        options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Expense")
                        .child(uid), Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, dataRetrive>(options) {
            @Override
            protected void onBindViewHolder(@NonNull dataRetrive dataRetrive, final int i, @NonNull Data data) {
                dataRetrive.amountR.setText("Rs." + " " + data.getAmount());
                dataRetrive.typeR.setText("Type - " + data.getType());
                dataRetrive.noteR.setText("Note - " + data.getNote());


                dataRetrive.lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        postKey = getRef(i).getKey();
                        amount = data.getAmount();
                        type = data.getType();
                        note = data.getNote();

                        updateData();
                    }
                });

            }

            @NonNull
            @Override
            public dataRetrive onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_data_receive, parent, false);
                return new dataRetrive(view);
            }

        };
        recyclerView.setAdapter(adapter);

        //navigation

        income_nav = findViewById(R.id.income_nav);
        expense_nav = findViewById(R.id.expense_nav);
        debt_nav = findViewById(R.id.debt_nav);
        todolist_nav = findViewById(R.id.todolist_nav);

        income_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExpenseManage.this,IncomeManage2.class) );
            }
        });

        expense_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Expense Manage",Toast.LENGTH_SHORT).show();
            }
        });

        debt_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExpenseManage.this,DebtListActivity.class) );
            }
        });


        todolist_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExpenseManage.this,ListOfListsActivity.class) );
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void customDialog(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(ExpenseManage.this);
        LayoutInflater inflater = LayoutInflater.from(ExpenseManage.this);
        View myView = inflater.inflate(R.layout.add_expense,null);

        final AlertDialog dialog = myDialog.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog.setView(myView);

        final EditText amount_ed = myView.findViewById(R.id.amount_add);
        final EditText type_ed = myView.findViewById(R.id.type_add);
        final EditText note_ed = myView.findViewById(R.id.note_add);
        final Button saveBtn1 = myView.findViewById(R.id.expenseSave);

        saveBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exAmount = amount_ed.getText().toString().trim();
                String exType = type_ed.getText().toString().trim();
                String exNote = note_ed.getText().toString().trim();

                // Input Validation
                if(TextUtils.isEmpty(exAmount)){
                    amount_ed.setError("Add amount");
                    return;
                }
                if(TextUtils.isEmpty(exType)){
                    type_ed.setError("Add Type");
                    return;
                }
                if(TextUtils.isEmpty(exNote)){
                    note_ed.setError("Add note");
                    return;
                }

                float amountEx = Float.parseFloat(exAmount);

                String id = reff.push().getKey();

                Data data = new Data(amountEx,exType,exNote);
                reff.child(id).setValue(data);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateData(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ExpenseManage.this);
        LayoutInflater inflater = LayoutInflater.from(ExpenseManage.this);
        View view = inflater.inflate(R.layout.activity_expense_update,null);

        final AlertDialog dialog = myDialog.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog.setView(view);

        final EditText editAmount = view.findViewById(R.id.amount_update);
        final EditText editType = view.findViewById(R.id.type_update);
        final EditText editNote = view.findViewById(R.id.note_update);

        String str = String.valueOf(amount);
        editAmount.setText(str);
        editType.setText(type);
        editNote.setText(note);

        Button btnUpdate =  view.findViewById(R.id.btnUpdate_expense);
        Button btnDelete = view.findViewById(R.id.btnDelete_expense);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountUp = editAmount.getText().toString().trim();
                String typeUp = editType.getText().toString().trim();
                String noteUp = editNote.getText().toString().trim();

                //Update Validation
                if(TextUtils.isEmpty(amountUp)){
                    editAmount.setError("Enter amount");
                    return;
                }
                if(TextUtils.isEmpty(typeUp)){
                    editType.setError("Enter type!");
                    return;
                }
                if(TextUtils.isEmpty(noteUp)){
                    editNote.setError("Enter Note!");
                    return;
                }

                float floatAmount = Float.parseFloat(amountUp);

                Data data = new Data(floatAmount,typeUp,noteUp);

                reff.child(postKey).setValue(data);

                Toast.makeText(ExpenseManage.this,"Expense Updated",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child(postKey).removeValue();
                Toast.makeText(ExpenseManage.this,"Expense Deleted",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

}