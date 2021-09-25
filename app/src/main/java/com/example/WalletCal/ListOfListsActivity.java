package com.example.daytoday;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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

import com.example.daytoday.Model.List;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListOfListsActivity extends AppCompatActivity {

    private DatabaseReference lDatabase;
    private FirebaseAuth mAuth;

    private FirebaseRecyclerOptions<List> options;
    private FirebaseRecyclerAdapter<List, ListOfListsActivity.MyViewHolder> adapter;

    private RecyclerView recyclerView;

    private String type;
    private  String note;
    private  String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_lists);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser == null ){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        String uid = mUser.getUid();

        lDatabase = FirebaseDatabase.getInstance().getReference("Shopping List").child(uid);
        lDatabase.keepSynced(true);

        FloatingActionButton fab_btn = findViewById(R.id.fabLists);
        ImageView menu_btn = findViewById(R.id.menu_btn);
        ImageView back_btn = findViewById(R.id.back_btn);
        TextView total = findViewById(R.id.exAllTotAmount);
        recyclerView = findViewById(R.id.recyclerLists);

        RelativeLayout income = findViewById(R.id.income_nav);
        RelativeLayout expense = findViewById(R.id.expense_nav);
        RelativeLayout debt = findViewById(R.id.debt_nav);
        RelativeLayout list = findViewById(R.id.shoppinglist_nav);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //layoutManager.setStackFromEnd(true);
        //layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fab_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                customDialog();
            }
        });

        lDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                float totAmount = 0;

                for(DataSnapshot snap:snapshot.getChildren()){
                    List list = snap.getValue(List.class);
                    totAmount += list.getAmount();
                }

                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                String fTotAmount = decimalFormat.format(totAmount);

                total.setText(String.valueOf(fTotAmount));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<List>()
                .setQuery(lDatabase,List.class).build();

        adapter = new FirebaseRecyclerAdapter<List, ListOfListsActivity.MyViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull ListOfListsActivity.MyViewHolder holder, final int position, @NonNull final List model) {

                holder.setType(model.getType());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());

                holder.del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postKey = getRef(position).getKey();
                        lDatabase.child(postKey).removeValue();
                        Toast.makeText(ListOfListsActivity.this,"List Deleted",Toast.LENGTH_LONG).show();
                    }
                });

                holder.ed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        postKey = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();

                        updateList();

                    }
                });

                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        postKey = getRef(position).getKey();
                        type = model.getType();

                        Intent list = new Intent(getApplicationContext(),ListActivity.class);
                        list.putExtra("key",postKey);
                        list.putExtra("listName",type);

                        Toast.makeText(ListOfListsActivity.this,"Retrieving List..",Toast.LENGTH_SHORT).show();

                        startActivity(list);

                    }
                });

            }

            @NonNull
            @Override
            public ListOfListsActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list,parent,false);
                return new ListOfListsActivity.MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),IncomeManage2.class));
            }
        });

        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExpenseManage.class));
            }
        });

        debt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DebtListActivity.class));
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ListOfListsActivity.class));
            }
        });

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        back_btn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        }));

    }

    private void customDialog(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ListOfListsActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ListOfListsActivity.this);
        View myView = inflater.inflate(R.layout.input_list,null);

        final AlertDialog dialog = myDialog.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog.setView(myView);

        final EditText type = myView.findViewById(R.id.edit_list_name);
        final EditText note = myView.findViewById(R.id.edit_list_desc);
        final Button save = myView.findViewById(R.id.btnAddListSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mType = type.getText().toString().trim();
                float mAmount = 0;
                String mNote = note.getText().toString().trim();

                if(TextUtils.isEmpty(mType)){
                    type.setError("Enter list name");
                    return;
                }

                if(TextUtils.isEmpty(mNote)){
                    note.setError("Enter description");
                    return;
                }

                String date = DateFormat.getDateInstance().format(new Date());

                String id = lDatabase.push().getKey();

                List list = new List(mType,mAmount,mNote,date,id);

                setListDb(id,list);



                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setGravity(Gravity.LEFT);
    }

    public boolean setListDb(String id,List list){
        try {
            lDatabase.child(id).setValue(list);
            Toast.makeText(ListOfListsActivity.this,"Item Added",Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e){
            Toast.makeText(ListOfListsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        View myView;
        ImageView del,ed;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
            del = myView.findViewById(R.id.delList);
            ed = myView.findViewById(R.id.edlList);
        }

        public void setType(String type){
            TextView mType = myView.findViewById(R.id.list_type);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote = myView.findViewById(R.id.list_desc);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = myView.findViewById(R.id.list_date);
            mDate.setText(date);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public  void setAmount(float amount){

            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String nAmount = decimalFormat.format(amount);

            TextView mAmount = myView.findViewById(R.id.list_amount);
            mAmount.setText(String.valueOf(nAmount));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateList(){

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ListOfListsActivity.this);

        LayoutInflater inflater = LayoutInflater.from(ListOfListsActivity.this);

        View mView = inflater.inflate(R.layout.update_list,null);

        final AlertDialog dialog = myDialog.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog.setView(mView);

        final EditText edtType = mView.findViewById(R.id.edit_list_name_upd);
        final EditText edtNote = mView.findViewById(R.id.edit_list_desc_upd);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        Button btnUpdate = mView.findViewById(R.id.btnSaveListUpd);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Enter Item Name");
                    return;
                }

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Enter Item Quantity");
                    return;
                }


                //String date = DateFormat.getDateInstance().format(new Date());

                Map<String, Object> updates = new HashMap<String,Object>();
                updates.put("type",type);
                updates.put("note",note);

                lDatabase.child(postKey).updateChildren(updates);

                Toast.makeText(ListOfListsActivity.this,"List Updated",Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}