package com.example.WalletCal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    //move to next activity
    private static int SPLASH_SCREEN = 5000;

    //add animation variable
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //new (Remove the top status bar)
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hide the Action bar
        getSupportActionBar().hide();


        setContentView(R.layout.activity_main);

        //check the firebase connection success
        //Toast.makeText(MainActivity.this, "Firebase connection Success",Toast.LENGTH_LONG).show();

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);

        //assign animations
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        //create splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

                //create animation
                //Pair[] Pairs = new Pair[2];
                //Pairs[0] = new Pair<View, String>(image, "logo_image");
                //Pairs[1] = new Pair<View, String>(logo, "logo_text");

                //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,Pairs);
                //startActivity(intent,options.toBundle());

            }
        },SPLASH_SCREEN);
        //setContentView(R.layout.activity_main);
    }
}