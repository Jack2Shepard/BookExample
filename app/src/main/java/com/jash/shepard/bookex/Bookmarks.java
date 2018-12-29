package com.jash.shepard.bookex;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import me.anwarshahriar.calligrapher.Calligrapher;

public class Bookmarks extends AppCompatActivity {
    private ListView bookmarks_lv;
    private ArrayList<String> favorites;
    private DbHelper dbHelper;
    private MyAdapter myAdapter;
    private TextView tb_title_tv;
    private ImageView tb_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        BackGroundAnimation();
        inits();
        refresher();
    }

    public void inits() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "adobe_arabic_shin.ttf", true);
        bookmarks_lv = findViewById(R.id.bookmarks_lv);
        favorites = new ArrayList<>();
        dbHelper = new DbHelper(this);
        tb_title_tv = findViewById(R.id.toolbar_title_tv);
        tb_back_btn = findViewById(R.id.toolbar_back_btn);
        tb_title_tv.setText(getString(R.string.favorites_toolbar_text));
        tb_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Bookmarks.this, MainActivity.class));
            }
        });
    }

    public void refresher() {
        dbHelper.opendatabase();
        int count = dbHelper.fav_counter();
        for (int i = 0; i < count; i++) {
            favorites.add(dbHelper.fav_getter("section", i));
        }
        dbHelper.close();
        setListView();
    }

    public void setListView() {
        myAdapter = new MyAdapter(this, R.layout.sections_row, R.drawable.fav_on, favorites);
        bookmarks_lv.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        bookmarks_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                dbHelper.opendatabase();
                startActivity(new Intent(Bookmarks.this, PageActivity.class)
                        .putExtra("section", favorites.get(position))
                        .putExtra("chapter", dbHelper.getChapterfromSection("chapter",
                                favorites.get(position))));
                dbHelper.close();
            }
        });
    }

    public void BackGroundAnimation() {
        LinearLayout bg = findViewById(R.id.bookmarks_act);
        AnimationDrawable animationDrawable = (AnimationDrawable) bg.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myAdapter = new MyAdapter(this, R.layout.sections_row, R.drawable.fav_on, favorites);
        bookmarks_lv.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        if (favorites.size() == 0) {
            //Toast.makeText(mContext,"همه موارد حذف شدند!",Toast.LENGTH_SHORT).show();
            Toasty.warning(this, "لیست علاقمندی ها خالی است", Toast.LENGTH_LONG, true).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
