package com.jash.shepard.bookex;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.anwarshahriar.calligrapher.Calligrapher;

public class Contents extends AppCompatActivity {
    private ListView contents_lv;
    private MyAdapter adapter;
    private DbHelper dbHelper;
    private static String SELECT = "chapter";
    private ArrayList<String> chapters;
    private TextView toolbar_title;
    private ImageView toolbar_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        BackGroundAnimation();
        inits();
        refresher();
        setListView();
    }

    private void inits(){
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/adobe_arabic_shin.ttf",true);
        toolbar_title = findViewById(R.id.toolbar_title_tv);
        toolbar_title.setText(getString(R.string.contents_btn_txt));
        toolbar_back_btn = findViewById(R.id.toolbar_back_btn);
        toolbar_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Contents.this,MainActivity.class));
            }
        });
        contents_lv = findViewById(R.id.contents_lv);
        chapters = new ArrayList<>();
        dbHelper = new DbHelper(this);
    }

    private void refresher(){
        dbHelper.opendatabase();
        int count = dbHelper.chapter_Counter(SELECT);
        for(int i = 0 ; i < count ; i++){
            chapters.add(dbHelper.chapter_Getter(SELECT,i));
        }
        dbHelper.close();
    }
    public void setListView(){
        adapter = new MyAdapter(this,R.layout.sections_row,chapters);
        contents_lv.setAdapter(adapter);
        contents_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Contents.this,Sections.class)
                .putExtra("chapter",chapters.get(position)));
            }
        });
    }
    public void BackGroundAnimation(){
        RelativeLayout bg = findViewById(R.id.contents_act);
        AnimationDrawable animationDrawable = (AnimationDrawable)bg.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

}
