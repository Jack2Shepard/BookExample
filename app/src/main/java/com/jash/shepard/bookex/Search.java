package com.jash.shepard.bookex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import me.anwarshahriar.calligrapher.Calligrapher;

public class Search extends AppCompatActivity {
    private EditText search_ed;
    private ImageView search_icon;
    private RadioGroup search_rg;
    private LinearLayout magic_row;
    private ListView search_results_lv;
    private DbHelper dbHelper;
    private ArrayList<String> chapters, sections, texts;
    private static String SELECT_TITLES = "chapter,section",
            SELECT_TEXTS = "substr(text,1,50)";
    private String select;
    private MyAdapter myAdapter;
    private TextView tb_title_tv;
    private ImageView tb_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        intits();
        handle_Clicks();
    }

    public void intits() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"adobe_arabic_shin.ttf",true);
        search_ed = findViewById(R.id.search_ed);
        search_icon = findViewById(R.id.search_icon);
        search_rg = findViewById(R.id.radio_group);
        magic_row = findViewById(R.id.magic_row);
        search_results_lv = findViewById(R.id.search_result_lv);
        dbHelper = new DbHelper(this);
        select = SELECT_TITLES;
        tb_title_tv = findViewById(R.id.toolbar_title_tv);
        tb_back_btn = findViewById(R.id.toolbar_back_btn);
        tb_title_tv.setText(getString(R.string.search_toolbar_title));
        tb_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Search.this,MainActivity.class));
            }
        });
    }

    public void handle_Clicks() {
        search_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.in_titles_rd) {
                    select = SELECT_TITLES;
                } else if (checkedId == R.id.in_text_rd) {
                    select = SELECT_TEXTS;
                }
            }
        });
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputQuery()) {
                   // Toast.makeText(Search.this, "عبارت مورد نظر خود را وارد کنید",
                     //       Toast.LENGTH_SHORT).show();
                    Toasty.info(Search.this,"عبارت مورد نظر خود را وارد کنید",
                            Toast.LENGTH_SHORT,true).show();
                } else
                    search(search_ed.getText().toString().trim());
            }
        });
    }

    public void search(String query) {
        dbHelper.opendatabase();
        chapters = new ArrayList<>();
        sections = new ArrayList<>();
        texts = new ArrayList<>();
        if (select.equalsIgnoreCase(SELECT_TITLES)) {
            int count = dbHelper.search_in_titles_counter(SELECT_TITLES, query);
            if (count == 0) {
                //Toast.makeText(Search.this, "عبارت مورد نظر در عناوین یافت نشد!",
                        //Toast.LENGTH_SHORT).show();
                Toasty.error(Search.this,"عبارت مورد نظر در عناوین یافت نشد.",
                        Toast.LENGTH_SHORT,true).show();
                search_results_lv.setVisibility(View.GONE);
                magic_row.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < count; i++) {
                    chapters.add(dbHelper.search_in_titles_getter(SELECT_TITLES, query, i)[0]);
                    sections.add(dbHelper.search_in_titles_getter(SELECT_TEXTS, query, i)[1]);
                }
                myAdapter = new MyAdapter(this, R.layout.result_layout, chapters, sections);
                search_results_lv.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
                magic_row.setVisibility(View.VISIBLE);
            }

        } else if (select.equalsIgnoreCase(SELECT_TEXTS)) {
            int count = dbHelper.search_in_texts_counter(SELECT_TEXTS, query);
            if (count == 0) {
                //Toast.makeText(Search.this, "عبارت مورد نظر در متون یافت نشد!",
                        //Toast.LENGTH_SHORT).show();
                Toasty.error(Search.this,"عبارت مورد نظر در متون یافت نشد.",
                        Toast.LENGTH_SHORT,true).show();
                search_results_lv.setVisibility(View.GONE);
                magic_row.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < count; i++) {
                    texts.add(dbHelper.search_in_texts_getter(SELECT_TEXTS, query, i));
                }
            }
            myAdapter = new MyAdapter(this, R.layout.sections_row, texts);
            search_results_lv.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
            magic_row.setVisibility(View.GONE);
        }
        dbHelper.close();
    }

    public boolean checkInputQuery() {
        boolean isFilled = false;
        if (search_ed.getText().toString().trim().length() == 0) {
            isFilled = true;
        }
        return isFilled;
    }
}
