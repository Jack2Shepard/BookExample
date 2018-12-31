package com.jash.shepard.bookex;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import me.anwarshahriar.calligrapher.Calligrapher;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout contents,search,bookmarks,settings;
    private DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackGroundAnimation();
        inits();
    }

    public void inits(){
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/adobe_arabic_shin.ttf",true);
        contents = findViewById(R.id.contents_btn);
        search = findViewById(R.id.search_btn);
        bookmarks = findViewById(R.id.bookmark_btn);
        settings = findViewById(R.id.settings_btn);
        dbHelper = new DbHelper(this);
        contents.setOnClickListener(this);
        search.setOnClickListener(this);
        bookmarks.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contents_btn :{
                startActivity(new Intent(MainActivity.this,Contents.class));
            }
            break;
            case R.id.search_btn :{
                startActivity(new Intent(MainActivity.this,Search.class));
            }
            break;
            case R.id.bookmark_btn :{
                if(checkFavorites()){
                    //Toast.makeText(this,"موردی برای نمایش وجود ندارد!",Toast.LENGTH_LONG).show();
                    Toasty.warning(this,"لیست علاقمندی ها خالی است",
                            Toast.LENGTH_SHORT,true).show();
                }else
                startActivity(new Intent(MainActivity.this,Bookmarks.class));
            }
            break;
            case R.id.settings_btn :{
                startActivity(new Intent(MainActivity.this,Settings.class));
            }
            break;
        }
    }
    public boolean checkFavorites(){
        boolean isEmpty = false;
        dbHelper.opendatabase();
        int count = dbHelper.fav_counter();
        dbHelper.close();
        if(count == 0)
            isEmpty = true;
        return isEmpty;
    }
    public void BackGroundAnimation(){
        RelativeLayout bg = findViewById(R.id.main_act);
        AnimationDrawable animationDrawable = (AnimationDrawable)bg.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
}
