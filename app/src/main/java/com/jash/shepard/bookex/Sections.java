package com.jash.shepard.bookex;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

import me.anwarshahriar.calligrapher.Calligrapher;

public class Sections extends AppCompatActivity {
    private ListView sections_lv;
    private ArrayList<String> sections;
    private DbHelper dbHelper;
    private MyAdapter myAdapter;
    private String chapter;
    private TextView tb_title_tv;
    private ImageView tb_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);
        BackGroundAnimation();
        intits();
        refresher();
        setSectionListView();
    }

    public void intits(){
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"Ordibehesht_shablon.TTF",true);
        sections_lv = findViewById(R.id.sections_lv);
        Intent intent = getIntent();
        chapter = intent.getStringExtra("chapter");
        sections = new ArrayList<>();
        dbHelper = new DbHelper(this);
        tb_title_tv = findViewById(R.id.toolbar_title_tv);
        tb_back_btn = findViewById(R.id.toolbar_back_btn);
        tb_title_tv.setText(getString(R.string.chapter_toolbar_txt_append) + " " + chapter);
        tb_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sections.this,Contents.class));
            }
        });
    }

    public void refresher(){
        dbHelper.opendatabase();
        for(int i = 0 ; i < dbHelper.section_counter("section",chapter) ; i++){
            sections.add(dbHelper.section_getter("section",chapter,i));
        }
        dbHelper.close();
    }

    public void setSectionListView(){
        myAdapter = new MyAdapter(this,R.layout.sections_row,true,sections);
        sections_lv.setAdapter(myAdapter);
        sections_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Sections.this, PageActivity.class)
                .putExtra("chapter",chapter)
                .putExtra("section",sections.get(position)));
            }
        });
    }
    public void BackGroundAnimation(){
        RelativeLayout bg = findViewById(R.id.sections_act);
        AnimationDrawable animationDrawable = (AnimationDrawable)bg.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myAdapter = new MyAdapter(this,R.layout.sections_row,true,sections);
        sections_lv.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }
}
