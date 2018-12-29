package com.jash.shepard.bookex;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.service.quicksettings.TileService;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import me.anwarshahriar.calligrapher.Calligrapher;


public class PageActivity extends AppCompatActivity implements View.OnClickListener {
    private Button prev_btn, next_btn;
    private TextView page_number, section_title_tv, page_text;
    private String section, chapter;
    private int numOfPages, currentPage;
    private ArrayList<PageClass> pages;
    private DbHelper dbHelper;
    private ImageView bookmark;
    private TextView tb_title_tv;
    private ImageView tb_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        BackGroundAnimation();
        inits();
        refresher();
        showBookMarkStatus();
    }

    public void inits() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"Karbalaei.ttf",true);
        prev_btn = findViewById(R.id.prev_page_btn);
        next_btn = findViewById(R.id.next_page_btn);
        page_number = findViewById(R.id.page_number_tv);
        page_text = findViewById(R.id.page_text);
        section_title_tv = findViewById(R.id.section_title_tv);
        bookmark = findViewById(R.id.bookmark_icon);
        dbHelper = new DbHelper(this);
        Intent intent = getIntent();
        chapter = intent.getStringExtra("chapter");
        section = intent.getStringExtra("section");
        section_title_tv.setText(section);
        YoYo.with(Techniques.FlipInX)
                .duration(1700)
                .repeat(0)
                .playOn(section_title_tv);
        pages = new ArrayList<>();
        prev_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        tb_title_tv = findViewById(R.id.toolbar_title_tv);
        tb_back_btn = findViewById(R.id.toolbar_back_btn);
        tb_title_tv.setText((getString(R.string.chapter_toolbar_txt_append) + " " + chapter));
        tb_back_btn.setOnClickListener(this);
    }

    public void refresher() {
        dbHelper.opendatabase();
        numOfPages = dbHelper.page_counter("page", section);
        currentPage = 1;
        for (int i = 0; i < numOfPages; i++) {
            String text = dbHelper.page_getter("text", section, i + 1, 0);
            PageClass page = new PageClass(text, i + 1);
            pages.add(page);
        }
        setPage(pages.get(0).getPage_text(), pages.get(0).getPage_number(), numOfPages);
        checkButtons();
        dbHelper.close();
    }

    public void setPage(String text, int pageNumber, int total) {
        page_text.setText(text);
        page_number.setText(getString(R.string.pageOf) + " " + pageNumber + "  " +
                getString(R.string.from) + " " + total);
    }

    public void checkButtons() {
        if (currentPage == 1) {
            prev_btn.setEnabled(false);
            next_btn.setEnabled(true);
        } else if (currentPage == numOfPages) {
            prev_btn.setEnabled(true);
            next_btn.setEnabled(false);
        } else if (numOfPages == 1) {
            prev_btn.setEnabled(false);
            next_btn.setEnabled(false);
        } else {
            prev_btn.setEnabled(true);
            next_btn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_page_btn: {
                int nextPage = currentPage + 1;
                if (nextPage <= numOfPages) {
                    setPage(pages.get(currentPage).getPage_text(), pages.get(currentPage).getPage_number(),
                            numOfPages);
                }
                this.currentPage = nextPage;
                YoYo.with(Techniques.SlideOutDown)
                        .duration(1000)
                        .repeat(0)
                        .playOn(page_text);
                YoYo.with(Techniques.SlideInUp)
                        .duration(1000)
                        .repeat(0)
                        .playOn(page_text);
                checkButtons();
            }
            break;
            case R.id.prev_page_btn: {
                int prevPage = currentPage - 1;
                if (prevPage >= 1) {
                    setPage(pages.get(prevPage - 1).getPage_text(), pages.get(prevPage - 1).getPage_number(),
                            numOfPages);
                }
                this.currentPage = prevPage;
                YoYo.with(Techniques.SlideOutUp)
                        .duration(1000)
                        .repeat(0)
                        .playOn(page_text);
                YoYo.with(Techniques.SlideInDown)
                        .duration(1000)
                        .repeat(0)
                        .playOn(page_text);
                checkButtons();
            }
            break;
            case R.id.bookmark_icon:{
                toggleBookmark();
            }
            break;
            case R.id.toolbar_back_btn:{
                startActivity(new Intent(PageActivity.this,Sections.class)
                .putExtra("section",section)
                .putExtra("chapter",chapter));
            }
        }
    }

    public void toggleBookmark(){
        dbHelper.opendatabase();
        if (bookmark.getTag().toString().trim().equals("on")){
            bookmark.setImageResource(R.drawable.fav_off);
            bookmark.setTag("off");
            YoYo.with(Techniques.Flash)
                    .duration(700)
                    .repeat(0)
                    .playOn(bookmark);
            //Toast.makeText(PageActivity.this,"از علاقمندی ها حذف شد",Toast.LENGTH_SHORT).show();
            Toasty.warning(this,"از علاقمندی ها حذف شد",Toast.LENGTH_SHORT,true).show();
            dbHelper.setBookmark(section,0);
        }else if (bookmark.getTag().toString().trim().equals("off")){
            bookmark.setImageResource(R.drawable.fav_on);
            bookmark.setTag("on");
            YoYo.with(Techniques.RubberBand)
                    .duration(700)
                    .repeat(0)
                    .playOn(bookmark);
            //Toast.makeText(PageActivity.this,"به علاقمندی ها اضافه شد",Toast.LENGTH_SHORT).show();
            Toasty.success(this,"به علاقمندی ها اضافه شد",Toast.LENGTH_SHORT,true).show();
            dbHelper.setBookmark(section,1);
        }
        dbHelper.close();
    }

    public boolean checkBookmark(){
        dbHelper.opendatabase();
        boolean isMarked = false;
        if(dbHelper.checkBookmark(section) == 1) {
            isMarked = true;
        }
        dbHelper.close();
        return isMarked;
    }

    public void showBookMarkStatus(){
        if(checkBookmark()){
            bookmark.setImageResource(R.drawable.fav_on);
            bookmark.setTag("on");
        }else{
            bookmark.setImageResource(R.drawable.fav_off);
            bookmark.setTag("off");
        }
    }
    public void BackGroundAnimation(){
        LinearLayout bg = findViewById(R.id.page_act);
        AnimationDrawable animationDrawable = (AnimationDrawable)bg.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
}
