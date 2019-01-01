package com.jash.shepard.bookex;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MyAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    private Context mContext;
    private int imageRes;
    private ArrayList<String> chapters, sections;
    private DbHelper dbHelper;
    private int lastposition = -1;
    private boolean flag = false;

    public MyAdapter(@NonNull Context context, int layout, @NonNull ArrayList<String> chapters) {
        super(context, layout, chapters);
        this.mContext = context;
        this.chapters = chapters;
    }

    public MyAdapter(@NonNull Context context, int layout, @NonNull ArrayList<String> chapters, ArrayList<String> sections) {
        super(context, layout, chapters);
        this.mContext = context;
        this.chapters = chapters;
        this.sections = sections;
    }

    public MyAdapter(@NonNull Context context, int layout, int imageRes, ArrayList<String> sections) {
        super(context, layout, imageRes, sections);
        this.mContext = context;
        this.sections = sections;
        this.imageRes = imageRes;
    }

    public MyAdapter(@NonNull Context context, int layout, boolean flag, ArrayList<String> sections) {
        super(context, layout, sections);
        this.mContext = context;
        this.sections = sections;
        this.flag = flag;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Ordibehesht_shablon.ttf");
        Typeface typefaceTwo = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Lalezar-Regular.ttf");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (sections == null && imageRes == 0 && chapters != null) {
            convertView = inflater.inflate(R.layout.sections_row, parent, false);
            TextView chapter = convertView.findViewById(R.id.section_tv);
            chapter.setTypeface(typeface);
            ImageView bm_icon = convertView.findViewById(R.id.bookmark_image);
            chapter.setTextColor(position % 2 == 0 ?
                    mContext.getResources().getColor(R.color.colorText) :
                    mContext.getResources().getColor(R.color.colorTextAlt));
            bm_icon.setVisibility(View.GONE);
            chapter.setText(chapters.get(position));
            rowAnimation(position,convertView);
        } else if (imageRes == 0 && sections != null && chapters != null) {
            convertView = inflater.inflate(R.layout.result_layout, parent, false);
            TextView chapter = convertView.findViewById(R.id.chapters_result_tv);
            TextView section = convertView.findViewById(R.id.sections_result_tv);
            section.setTextColor(position % 2 == 0 ?
                    mContext.getResources().getColor(R.color.colorText) :
                    mContext.getResources().getColor(R.color.colorTextAlt));
            chapter.setTextColor(position % 2 == 0 ?
                    mContext.getResources().getColor(R.color.colorText) :
                    mContext.getResources().getColor(R.color.colorAccent));
            chapter.setText(chapters.get(position));
            section.setText(sections.get(position));
            chapter.setTypeface(typeface);
            section.setTypeface(typeface);
            chapter.setOnClickListener(this);
            section.setOnClickListener(this);
            section.setTag(position);
            chapter.setTag(position);
            rowAnimation(position,convertView);
        } else if (flag) {
            convertView = inflater.inflate(R.layout.sections_row, parent, false);
            TextView section = convertView.findViewById(R.id.section_tv);
            ImageView bm_icon = convertView.findViewById(R.id.bookmark_image);
            section.setText(sections.get(position));
            section.setTypeface(typefaceTwo);
            dbHelper = new DbHelper(mContext);
            dbHelper.opendatabase();
            section.setTextColor(position % 2 == 0 ?
                    mContext.getResources().getColor(R.color.colorText) :
                    mContext.getResources().getColor(R.color.colorTextAlt));
            if (dbHelper.checkBookmark(sections.get(position)) == 1) {
                bm_icon.setVisibility(View.VISIBLE);
                bm_icon.setImageResource(R.drawable.fav_on);
            } else if (dbHelper.checkBookmark(sections.get(position)) == 0) {
                bm_icon.setVisibility(View.GONE);
            }
            rowAnimation(position,convertView);
        } else {
            convertView = inflater.inflate(R.layout.sections_row, parent, false);
            TextView section = convertView.findViewById(R.id.section_tv);
            ImageView bm_icon = convertView.findViewById(R.id.bookmark_image);
            section.setText(sections.get(position));
            section.setTypeface(typeface);
            bm_icon.setOnClickListener(this);
            bm_icon.setTag(position);
            YoYo.with(Techniques.StandUp)
                    .duration(1700)
                    .repeat(0)
                    .playOn(convertView);
            section.setTextColor(position % 2 == 0 ?
                    mContext.getResources().getColor(R.color.colorText) :
                    mContext.getResources().getColor(R.color.colorTextAlt));
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        dbHelper = new DbHelper(mContext);
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.bookmark_image: {
                confirmDelete(position);
            }
            break;
            case R.id.sections_result_tv: {
                mContext.startActivity(new Intent(mContext, PageActivity.class)
                        .putExtra("section", sections.get(position))
                        .putExtra("chapter", chapters.get(position)));

            }
            break;
            case R.id.chapters_result_tv: {
                mContext.startActivity(new Intent(mContext, Sections.class)
                        .putExtra("chapter", chapters.get(position)));
            }
            break;
            case R.id.section_tv: {
                mContext.startActivity(new Intent(mContext, PageActivity.class)
                        .putExtra("section", sections.get(position)));
            }
            break;
        }
    }
    public void rowAnimation(int position,View convertView){
        Animation animation = AnimationUtils.loadAnimation(mContext,
                position > lastposition ? R.anim.load_down_anim : R.anim.load_up_anim);
        convertView.setAnimation(animation);
        lastposition = position;
    }
    private void confirmDelete (int pos){
        final int position = pos;
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.setting_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        Button yes_btn,no_btn ;
        yes_btn = dialog.findViewById(R.id.yes_dialog);
        no_btn = dialog.findViewById(R.id.no_dialog);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.opendatabase();
                dbHelper.setBookmark(sections.get(position), 0);
                sections.remove(position);
                //Toast.makeText(mContext,"با موفقیت حذف شد!",Toast.LENGTH_SHORT).show();
                Toasty.success(mContext, "با موفقیت از لیست حذف شد", Toast.LENGTH_SHORT, true).show();
                notifyDataSetChanged();
                if (sections.size() == 0) {
                    //Toast.makeText(mContext,"همه موارد حذف شدند!",Toast.LENGTH_SHORT).show();
                    Toasty.warning(mContext, "لیست علاقمندی ها خالی شد", Toast.LENGTH_LONG, true).show();
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                }
                dbHelper.close();
                dialog.dismiss();
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
