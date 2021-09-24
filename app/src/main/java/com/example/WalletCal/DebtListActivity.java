package com.example.daytoday;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.example.daytoday.Model.Debt;
import com.example.daytoday.Model.Item;
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

import java.util.Calendar;
import java.util.Date;

public class DebtListActivity extends AppCompatActivity {

    private FirebaseRecyclerOptions<Debt> options;

    private FloatingActionButton fabbtn;
    private FirebaseRecyclerAdapter<Debt, DebtViewHolder> adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private RelativeLayout income_nav,expense_nav,debt_nav,todolist_nav;
    private ImageView menu_btn,back_btn;
    private TextView totdebts;
    private String name;
    private String ddate;
    private float damount;
    private  String description;
    private  String postKey_;


    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debtlist);



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser dUser = mAuth.getCurrentUser();

        if (dUser == null ){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        String did = dUser.getUid();


/*create database path*/
        db = FirebaseDatabase.getInstance().getReference("Debt").child(did);
        db.keepSynced(true);

        //calculate the total debts

        totdebts = findViewById(R.id.totDebtAmount);

        db.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totdebt=0;
                for(DataSnapshot snap:snapshot.getChildren()){

                    Debt debt = snap.getValue(Debt.class);
                    totdebt += debt.getAmount();
                }

                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String fTotDebt = decimalFormat.format(totdebt);

                totdebts.setText(String.valueOf(fTotDebt));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fabbtn = findViewById(R.id.fabbtn);

        recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    debtDialog();
            }
        });

        options = new FirebaseRecyclerOptions.Builder<Debt>().setQuery(FirebaseDatabase.getInstance().getReference("Debt").child(did),Debt.class).build();
        adapter = new FirebaseRecyclerAdapter<Debt, DebtViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final DebtViewHolder holder, int position, @NonNull final Debt debt) {

                holder.name.setText("Name : "+debt.getName());
                holder.datedue.setText("Due Date : "+debt.getDuedate());
                holder.amount.setText("Amount : "+ " "+ debt.getAmount());
                holder.dis.setText("Description : "+debt.getDiscription());

               holder.debtlay.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {

                        postKey_ = getRef(position).getKey();
                        name = debt.getName();
                        ddate = debt.getDuedate();
                        damount = debt.getAmount();
                        description = debt.getDiscription();

                        updateDebt();
                    }
                });
            }

            @NonNull
            @Override
            public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.view_layout,parent,false);
                return new DebtViewHolder(view);

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);




        income_nav = findViewById(R.id.income_nav);
        expense_nav = findViewById(R.id.expense_nav);
        debt_nav = findViewById(R.id.debt_nav);
        todolist_nav = findViewById(R.id.todolist_nav);
        menu_btn = findViewById(R.id.menu_btn);
        back_btn = findViewById(R.id.back_btn);


        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
        expense_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DebtListActivity.this,ExpenseManage.class) );
            }
        });
        todolist_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DebtListActivity.this,ListOfListsActivity.class) );
            }
        });
        income_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //startActivity(new Intent(DebtListActivity.this,ListOfListsActivity.class) );
            }
        });
        debt_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DebtListActivity.this,"Already in here",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Add new debts
    private void debtDialog(){

        AlertDialog.Builder myDialog_ = new AlertDialog.Builder(DebtListActivity.this);
        LayoutInflater inflater_= LayoutInflater.from(DebtListActivity.this);
        View MyView = inflater_.inflate(R.layout.activity_adddebt,null);

       final AlertDialog dialog_= myDialog_.create();

        if (dialog_.getWindow() !=null)
            dialog_.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog_.setView(MyView);

        final EditText name = MyView.findViewById(R.id.cus_name);
        final EditText ddate = MyView.findViewById(R.id.debt_duedate);
        final EditText damount = MyView.findViewById(R.id.debt_amount);
        final EditText description = MyView.findViewById(R.id.debt_desc);
        final Button btn = MyView.findViewById(R.id.btn);
        ImageView cal = MyView.findViewById(R.id.adddebt_calendar);


        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final  int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DebtListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        String date = i2+"/"+i1+"/"+i;
                        ddate.setText(date);
                    }
                },year,month,day);
                 datePickerDialog.show();

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = name.getText().toString().trim();
                String Ddate = ddate.getText().toString().trim();
                String dAmount = damount.getText().toString().trim();
                String Desc = description.getText().toString().trim();


          //Validation
                if(TextUtils.isEmpty(Name)){
                    name.setError("Customer Name");
                    return;
                }
                if(TextUtils.isEmpty(Ddate)){
                    ddate.setError("Select date");
                    return;
                }
                if(TextUtils.isEmpty(dAmount)){
                    damount.setError("Enter amount");
                    return;
                }
                float amountfloat = Float.parseFloat(dAmount);

                String id = db.push().getKey();

                Debt debt = new Debt(Name,Ddate,amountfloat,Desc);
                db.child(id).setValue(debt);

                Toast.makeText(DebtListActivity.this, "Insert Successfully!", Toast.LENGTH_SHORT).show();
                dialog_.dismiss();

            }
        });
        dialog_.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public void updateDebt(){

        DecimalFormat decimalFormat_ = new DecimalFormat("#.00");
        AlertDialog.Builder myDialog_ = new AlertDialog.Builder(DebtListActivity.this);
        LayoutInflater inflater_ = LayoutInflater.from(DebtListActivity.this);
        View MyView = inflater_.inflate(R.layout.activity_update,null);

        final AlertDialog dialog_ = myDialog_.create();

        if (dialog_.getWindow() != null)
            dialog_.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog_.setView(MyView);

        final EditText upName = MyView.findViewById(R.id.debt_name);
        final EditText updDate = MyView.findViewById(R.id._datedue);
        final EditText upAmount = MyView.findViewById(R.id.amount_);
        final EditText upDesc = MyView.findViewById(R.id.des);
        ImageView upcal = MyView.findViewById(R.id.updebt_calendar);

        Calendar calendar = Calendar.getInstance();

        final int year1 = calendar.get(Calendar.YEAR);
        final  int month1 = calendar.get(Calendar.MONTH);
        final int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        upcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DebtListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i1, int i2, int i3) {

                        String date_ = i3+"/"+i2+"/"+i1;
                        updDate.setText(date_);
                    }
                },year1,month1,day1);
                datePickerDialog.show();

            }
        });

        //set the text previous added data
        upName.setText(name);
        upName.setSelection(name.length());

        updDate.setText(ddate);
        updDate.setSelection(ddate.length());

        String debtAmount = decimalFormat_.format(damount);
        upAmount.setText(debtAmount);
        upAmount.setSelection(debtAmount.length());

        upDesc.setText(description);
        upDesc.setSelection(description.length());

        //delete and update listner

        ImageView imgup = MyView.findViewById(R.id.imgUpdate);
        ImageView imgdl = MyView.findViewById(R.id.imgDelete);

        imgup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = upName.getText().toString().trim();
                ddate = updDate.getText().toString().trim();
                String dAmount = upAmount.getText().toString().trim();
                description = upDesc.getText().toString().trim();

                //checking

                if(TextUtils.isEmpty(name)){
                    upName.setError("Enter name");
                    return;
                }

                if(TextUtils.isEmpty(ddate)){
                    updDate.setError("Enter date");
                    return;
                }
                if(TextUtils.isEmpty(dAmount)){
                    upAmount.setError("Enter amount");
                    return;
                }
                float floatAmount = Float.parseFloat(dAmount);



                Debt debt = new Debt(name,ddate,floatAmount,description);
                db.child(postKey_).setValue(debt);
                Toast.makeText(DebtListActivity.this,"Update Successfully",Toast.LENGTH_SHORT).show();

                dialog_.dismiss();

            }
        });
        imgdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.child(postKey_).removeValue();
                Toast.makeText(DebtListActivity.this,"Delete Successfully",Toast.LENGTH_SHORT).show();
                dialog_.dismiss();
            }
        });
        dialog_.show();

    }
}