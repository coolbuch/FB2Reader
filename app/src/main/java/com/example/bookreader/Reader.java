package com.example.bookreader;

import android.app.ActionBar;
import android.app.Application;
import android.graphics.Color;
import android.os.Debug;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleableRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Reader extends AppCompatActivity {

    String URL = "";
    ViewPager viewPager;
    TextView textView;
    boolean dayMode = true;
    List<View> pages;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Bundle parameters = getIntent().getExtras();
        if (parameters != null)
            URL = parameters.getString("URL");  //Ключ для передачи из другого Activity
        // Настраиваем кастомный ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater actionBarInflanter = LayoutInflater.from(this);
        View actionBarView = actionBarInflanter.inflate(R.layout.item, null);

        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayShowCustomEnabled(true);

        // Формируем "Страницы"
        LayoutInflater inflater = LayoutInflater.from(this);
        pages = new ArrayList<>();
        View page = inflater.inflate(R.layout.activity_reader, null);
        textView = page.findViewById(R.id.text_view);
        String s = String.valueOf(textView.getText());
        textView.append("Страница 1");
        pages.add(page);

        page = inflater.inflate(R.layout.activity_reader, null);
        textView = page.findViewById(R.id.text_view);
        textView.append("Страница 2");
        pages.add(page);

        page = inflater.inflate(R.layout.activity_reader, null);
        textView = page.findViewById(R.id.text_view);
        textView.append("Страница 3");
        pages.add(page);



        MyPagerAdapter adapter = new MyPagerAdapter(pages);
        viewPager = new ViewPager(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);


        Button swapMode = actionBarView.findViewById(R.id.button_swap);

    }


    // Адаптер для viewPager
    public class MyPagerAdapter extends PagerAdapter
    {
        List<View> pages = null;
        public MyPagerAdapter(List<View> pages)
        {
            this.pages = pages;
        }

        @NonNull
        @Override
        public Object instantiateItem(View collection, int position){
            View v = pages.get(position);
            ((ViewPager) collection).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(View collection, int position, Object view){
            ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object){
            return view.equals(object);
        }

        @Override
        public void finishUpdate(View arg0){
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1){
        }

        @Override
        public Parcelable saveState(){
            return null;
        }

        @Override
        public void startUpdate(View arg0){
        }

        @Override
        public int getCount()
        {
            return pages.size();
        }

    }

    // Переключаем ночной / дневной режим
    public void onClick(View v)
    {
        for (int i = 0; i < pages.size(); i ++)
        {
            if (dayMode)
            {
                pages.get(i).setBackgroundColor(Color.parseColor("#000000"));
                TextView tv =  pages.get(i).findViewById(R.id.text_view);
                tv.setTextColor(Color.parseColor("#DDDDDD"));
            }
            else
            {
                pages.get(i).setBackgroundColor(Color.parseColor("#dddddd"));
                TextView tv =  pages.get(i).findViewById(R.id.text_view);
                tv.setTextColor(Color.parseColor("#000000"));
            }
        }
        dayMode = !dayMode;
    }
}
