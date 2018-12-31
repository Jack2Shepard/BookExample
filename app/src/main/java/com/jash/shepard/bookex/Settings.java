package com.jash.shepard.bookex;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private TextView sample_Text;
    private Spinner font_spinner;
    private RadioGroup rg_modes;
    private RadioButton rb_day, rb_night;
    private SeekBar fontSize_seekBar, lineSpace_seekBar;
    private Button apply_btn, decline_btn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String[] fonts;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        inits();
        setPreferences();

    }

    public void inits() {
        sample_Text = findViewById(R.id.sample_text_tv);
        font_spinner = findViewById(R.id.fonts_spinner);
        rg_modes = findViewById(R.id.rg_light_mode);
        rb_day = findViewById(R.id.rb_day_mode);
        rb_night = findViewById(R.id.rb_night_mode);
        fontSize_seekBar = findViewById(R.id.fonts_seekBar);
        lineSpace_seekBar = findViewById(R.id.line_space_sb);
        apply_btn = findViewById(R.id.apply_settings);
        decline_btn = findViewById(R.id.cancel_settings);
        fonts = getResources().getStringArray(R.array.fonts);
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    public void setPreferences() {
        setFontType();
        setFontSize();
        setLineSpace();
        setReadMode();
    }

    public void setFontType() {
        sample_Text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" +
                sharedPreferences.getString("font", "Ordibehesht_shablon") + ".ttf"));
        if (sharedPreferences.getString("font", "Ordibehesht_shablon").equalsIgnoreCase("Ordibehesht_shablon")) {

        } else if (sharedPreferences.getString("font", "Ordibehesht_shablon").equalsIgnoreCase("adobe_arabic_shin")) {
            font_spinner.setSelection(0);

        } else if (sharedPreferences.getString("font", "Ordibehesht_shablon").equalsIgnoreCase("Khandevane")) {
            font_spinner.setSelection(1);

        } else if (sharedPreferences.getString("font", "Ordibehesht_shablon").equalsIgnoreCase("Lalezar-Regular")) {
            font_spinner.setSelection(2);

        } else {
            font_spinner.setSelection(3);
        }
        font_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mEditor.putString("font", fonts[0]);
                        typeface = Typeface.createFromAsset(getAssets(), "fonts/Ordibehesht_shablon.ttf");
                        break;
                    case 1:
                        mEditor.putString("font", fonts[1]);
                        typeface = Typeface.createFromAsset(getAssets(), "fonts/adobe_arabic_shin.ttf");
                        break;
                    case 2:
                        mEditor.putString("font", fonts[2]);
                        typeface = Typeface.createFromAsset(getAssets(), "fonts/Khandevane.ttf");
                        break;
                    case 3:
                        mEditor.putString("font", fonts[3]);
                        typeface = Typeface.createFromAsset(getAssets(), "fonts/Lalezar-Regular.ttf");
                        break;
                }
                applyChanges();
                sample_Text.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void setFontSize() {
        sample_Text.setTextSize(sharedPreferences.getInt("fontSize", fontSize_seekBar.getProgress()));
        fontSize_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEditor.putInt("fontSize", progress);
                applyChanges();
                sample_Text.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setLineSpace() {
        sample_Text.setLineSpacing(sharedPreferences.getInt("fontSize",
                lineSpace_seekBar.getProgress()), 1);
        lineSpace_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEditor.putInt("lineSpace", progress);
                applyChanges();
                float size = (float) progress;
                sample_Text.setLineSpacing(size, 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setReadMode() {
        if (sharedPreferences.getString("readMode", "day").equalsIgnoreCase("day")) {
            sample_Text.setBackgroundColor(Color.WHITE);
            sample_Text.setTextColor(Color.BLACK);
            rg_modes.clearCheck();
            rb_day.setSelected(true);
        } else {
            sample_Text.setBackgroundColor(Color.BLACK);
            sample_Text.setTextColor(Color.WHITE);
            rg_modes.clearCheck();
            rb_night.setSelected(true);
        }
        rg_modes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_day_mode: {
                        sample_Text.setTextColor(Color.BLACK);
                        sample_Text.setBackgroundColor(Color.WHITE);
                        mEditor.putString("readMode", "day");
                    }
                    break;
                    case R.id.rb_night_mode: {
                        sample_Text.setTextColor(Color.WHITE);
                        sample_Text.setBackgroundColor(Color.BLACK);
                        mEditor.putString("readMode", "night");
                    }
                    break;
                }
                applyChanges();
            }
        });
    }

    public void applyChanges() {
        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialoge();
            }
        });
        decline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.clear();
            }
        });
    }

    public void confirmDialoge() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.setting_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        Button yes_btn, no_btn;
        yes_btn = dialog.findViewById(R.id.yes_dialog);
        no_btn = dialog.findViewById(R.id.no_dialog);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.commit();
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
