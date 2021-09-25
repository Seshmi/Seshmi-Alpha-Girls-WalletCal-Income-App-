package com.example.daytoday;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daytoday.Model.Data;
import com.example.daytoday.Model.DataIncome;
import com.example.daytoday.Model.Debt;
import com.example.daytoday.Model.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class HomeActivity extends AppCompatActivity {

    //expense
    DatabaseReference reff;
    private TextView Expense_total_home,expense_prasantage;
    float expense_prasant;

    //debt
    DatabaseReference db;
    private TextView debt_total_home;

    //SHOPING LIST
    DatabaseReference lDatabase;
    private  TextView shopping_tot;

    //iNCOME
    DatabaseReference indb;
    private TextView Income_total_home,income_prasantage;
    float income_prasant;

    PieChart pieChart;

    float totIncome;
    float expense_total_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();


        if (mUser == null ){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        String uid = mUser.getUid();

        ImageView income = findViewById(R.id.imageView6);
        ImageView expense = findViewById(R.id.imageView7);
        ImageView list = findViewById(R.id.imageView9);
        ImageView debt = findViewById(R.id.imageView8);

        pieChart = findViewById(R.id.piechart);

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

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ListOfListsActivity.class));
            }
        });

        debt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DebtListActivity.class));
            }
        });

        //Expense

        Expense_total_home = findViewById(R.id.expense_total_home);
        expense_prasantage = findViewById(R.id.expense_pra);

        reff= FirebaseDatabase.getInstance().getReference("Expense").child(uid);

        reff.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Data data = dataSnapshot.getValue(Data.class);

                    expense_total_home +=data.getAmount();

                    expense_prasant = (expense_total_home/(expense_total_home+totIncome))*100;

                }
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String TotExpense = decimalFormat.format(expense_total_home);


                DecimalFormat decimalFormat1 = new DecimalFormat("#.0");
                String expPre = decimalFormat1.format(expense_prasant);
                String S = String.valueOf(expPre);

                Expense_total_home.setText(String.valueOf(TotExpense));
                expense_prasantage.setText("Expense : "+S+ "%");

                pieChart.addPieSlice(
                        new PieModel(
                                (int)expense_total_home,
                                Color.parseColor("#EF5350")));

                HomeActivity.super.onRestart();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //debt

        debt_total_home = findViewById(R.id.debt_total_home);

        db = FirebaseDatabase.getInstance().getReference("Debt").child(uid);

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

                debt_total_home.setText(String.valueOf(fTotDebt));

                HomeActivity.super.onRestart();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Shoppping List

        lDatabase = FirebaseDatabase.getInstance().getReference("Shopping List").child(uid);
        shopping_tot = findViewById(R.id.shopping_home_tot);

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

                shopping_tot.setText(String.valueOf(fTotAmount));

                HomeActivity.super.onRestart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Income

        Income_total_home = findViewById(R.id.income_tot_home);
        income_prasantage = findViewById(R.id.income_pra);

        indb = FirebaseDatabase.getInstance().getReference("DataIncome").child(uid);

        indb.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    DataIncome data = dataSnapshot.getValue(DataIncome.class);

                    totIncome +=data.getAmount();

                    income_prasant = 100-expense_prasant;

                }
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String TotIncome = decimalFormat.format(totIncome);

                DecimalFormat decimalFormat1 = new DecimalFormat("#.0");
                String incPre = decimalFormat1.format(income_prasant);
                String S1 = String.valueOf(incPre);

                Income_total_home.setText(String.valueOf(TotIncome));
                income_prasantage.setText("Income : "+S1+ "%");

                pieChart.addPieSlice(
                        new PieModel(
                                (int)totIncome,
                                Color.parseColor("#66BB6A")));

                HomeActivity.super.onRestart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pieChart.startAnimation();
    }

}