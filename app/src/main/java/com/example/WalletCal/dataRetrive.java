package com.example.WalletCal;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daytoday.R;

public class dataRetrive extends RecyclerView.ViewHolder {

    TextView amountR,typeR,noteR;

    LinearLayout lay;


    public dataRetrive(@NonNull View itemView) {
        super(itemView);


        amountR = itemView.findViewById(R.id.amount_Re);
        typeR = itemView.findViewById(R.id.type_Re);
        noteR = itemView.findViewById(R.id.note_Re);

        lay = itemView.findViewById(R.id.holderLinear);




    }
}
