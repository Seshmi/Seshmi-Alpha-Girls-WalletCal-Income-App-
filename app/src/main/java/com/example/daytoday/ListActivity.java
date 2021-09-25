package com.example.daytoday;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private FirebaseRecyclerOptions<Item> options;
    private FirebaseRecyclerAdapter<Item,MyViewHolder> adapter;

    private TextView total;

    private String type;
    private float amount;
    private  String note;
    private  String postKey;
    private String listKey;
    private String listName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle extras = getIntent().getExtras();
        listKey = extras.getString("key");
        listName = extras.getString("listName");

        TextView listNameShow = findViewById(R.id.shopping_list_name);

        listNameShow.setText(listName);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser == null ){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        String uid = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference("Shopping List").child(uid).child(listKey).child("Items");
        mDatabase.keepSynced(true);

        total = findViewById(R.id.exTotAmount);

        FloatingActionButton fab_btn = findViewById(R.id.fab);
        ImageView menu_btn = findViewById(R.id.menu_btn);
        ImageView back_btn = findViewById(R.id.back_btn);

        RelativeLayout income = findViewById(R.id.income_nav);
        RelativeLayout expense = findViewById(R.id.expense_nav);
        RelativeLayout debt = findViewById(R.id.debt_nav);
        RelativeLayout list = findViewById(R.id.shoppinglist_nav);

        RecyclerView recyclerView = findViewById(R.id.recyclerListItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //layoutManager.setStackFromEnd(true);
        //layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(mDatabase,Item.class).build();

        adapter = new FirebaseRecyclerAdapter<Item, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Item model) {

                holder.setType(model.getType());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());
                holder.setNote(model.getNote());

                holder.myView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        postKey = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();

                        updateData();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                float totAmount = calcTotAmount(snapshot);

                String fTotAmount = formatDecimal(totAmount);

                total.setText(String.valueOf(fTotAmount));

                Map<String, Object> updates = new HashMap<String,Object>();
                updates.put("amount",totAmount);

                FirebaseDatabase.getInstance().getReference("Shopping List").child(uid).child(listKey).updateChildren(updates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        back_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ListOfListsActivity.class));
            }
        });

        fab_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                customDialog();
            }
        });

        income.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),IncomeManage2.class));
            }
        });

        expense.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExpenseManage.class));
            }
        });

        debt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DebtListActivity.class));
            }
        });

        list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ListOfListsActivity.class));
            }
        });

    }

    public void customDialog(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ListActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ListActivity.this);
        View myView = inflater.inflate(R.layout.input_item,null);

        final AlertDialog dialog = myDialog.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog.setView(myView);

        final EditText type = myView.findViewById(R.id.edit_item_type);
        final EditText amount = myView.findViewById(R.id.edit_item_amount);
        final EditText note = myView.findViewById(R.id.edit_item_note);
        final Button save = myView.findViewById(R.id.btnAddItemSave);

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String mType = type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                if(TextUtils.isEmpty(mType)){
                    type.setError("Enter Item Name");
                    return;
                }

                if(TextUtils.isEmpty(mNote)){
                    note.setError("Enter Item Quantity");
                    return;
                }

                if(TextUtils.isEmpty(mAmount)){
                    amount.setError("Enter amount");
                    return;
                }

                float amFloat = Float.parseFloat(mAmount);

                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                Item item = new Item(mType,amFloat,mNote,date,id);

                mDatabase.child(id).setValue(item);

                Toast.makeText(ListActivity.this,"Item Added",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public float calcTotAmount(DataSnapshot snapshot){
        float totAmount = 0;

        for(DataSnapshot snap:snapshot.getChildren()){
            Item item = snap.getValue(Item.class);
            totAmount += item.getAmount();
        }

        return totAmount;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String formatDecimal(float val){
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String a = decimalFormat.format(val);
        return  a;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateData(){

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        AlertDialog.Builder myDialog = new AlertDialog.Builder(ListActivity.this);

        LayoutInflater inflater = LayoutInflater.from(ListActivity.this);

        View mView = inflater.inflate(R.layout.update_item,null);

        final AlertDialog dialog = myDialog.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        dialog.setView(mView);

        final EditText edtType = mView.findViewById(R.id.edit_type_upd);
        final EditText edtAmount = mView.findViewById(R.id.edit_amount_upd);
        final EditText edtNote = mView.findViewById(R.id.edit_note_upd);

        edtType.setText(type);
        edtType.setSelection(type.length());

        String tempAmount = decimalFormat.format(amount);
        edtAmount.setText(tempAmount);
        edtAmount.setSelection(tempAmount.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        Button btnUpdate = mView.findViewById(R.id.btnSaveUpd);
        Button btnDelete = mView.findViewById(R.id.btnDelete);

        btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                String mAmount = edtAmount.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Enter Item Name");
                    return;
                }

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Enter Item Quantity");
                    return;
                }

                if(TextUtils.isEmpty(mAmount)){
                    edtAmount.setError("Enter amount");
                    return;
                }

                float floatAmount = Float.parseFloat(mAmount);

                String date = DateFormat.getDateInstance().format(new Date());

                Item item = new Item(type,floatAmount,note,date,postKey);

                mDatabase.child(postKey).setValue(item);

                Toast.makeText(ListActivity.this,"Item Updated",Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(postKey).removeValue();

                Toast.makeText(ListActivity.this,"Item Deleted",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setType(String type){
            TextView mType = myView.findViewById(R.id.list_item_type);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote = myView.findViewById(R.id.list_item_qty);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = myView.findViewById(R.id.list_item_date);
            mDate.setText(date);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public  void setAmount(float amount){

            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String nAmount = decimalFormat.format(amount);

            TextView mAmount = myView.findViewById(R.id.list_item_amount);
            mAmount.setText(String.valueOf(nAmount));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}