package com.example.daytoday;


import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DebtViewHolder extends RecyclerView.ViewHolder {


    TextView name,datedue,amount,dis;
    LinearLayout debtlay;
    public DebtViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.debt_name);
        datedue = itemView.findViewById(R.id.datedue);
        amount = itemView.findViewById(R.id.amount_debt);
        dis = itemView.findViewById(R.id.debt_dis);
        debtlay = itemView.findViewById(R.id.debtview);



    }
}
