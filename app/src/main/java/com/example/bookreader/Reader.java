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
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.*;

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

    class Section {
        String title, text;

        Section(String cont, String title) {
            this.title = title;
            this.text = cont;
            if (title == "<annotation>")
                text = "<i>".concat(cont).concat("</i>");
            if (title == "<image>") {
                //
            }
        }

        public ArrayList<Section> parseSection(String text, int num) {
            ArrayList<Section> ar = new ArrayList<>();
            StringBuffer sb = new StringBuffer(text);
            if (sb.indexOf("<aaaa" + Integer.toString(num + 1) + ">") == -1) {
                String title = sb.substring(sb.indexOf("<title>") + 7, sb.indexOf("</title>"));
                sb.delete(sb.indexOf("<title>"), sb.indexOf("</title>") + 8);
                ar.add(new Section(sb.substring(sb.indexOf("<aaaa" + Integer.toString(num) + ">") + 7, sb.indexOf("</aaaa" + Integer.toString(num) + ">") + 8), title));
            } else {
                ar.addAll(parseSection(sb.substring(sb.indexOf("<aaaa" + Integer.toString(num + 1) + ">"), sb.indexOf("<aaaa" + Integer.toString(num + 1) + ">")), num + 1));
            }
            return ar;
        }

        public ArrayList<Section> parseSections(String text) {
            ArrayList<Section> ar = new ArrayList<>();
            StringBuffer textbf = new StringBuffer(text);
            int n = 1;
            while (true) {
                int a = textbf.indexOf("<section>");
                int b = textbf.indexOf("</section>");
                if (a == -1 && b == -1) {
                    break;
                }
                if (a != -1 & a < b) {
                    textbf.replace(a, a + 9, "<aaaa" + Integer.toString(n) + ">");
                    n += 1;
                } else {
                    if (b != -1 & (b < a | a == -1)) {
                        n -= 1;
                        textbf.replace(b, b + 10, "</aaaa" + Integer.toString(n) + ">");
                    }
                }
            }
            while (textbf.indexOf("<aaaa1>") != -1) {
                ar.addAll(Section.parseSection(textbf.substring(textbf.indexOf("<aaaa1>"), textbf.indexOf("</aaaa1>") + 8), n));
                textbf.delete(textbf.indexOf("<aaaa1>"), textbf.indexOf("</aaaa1>") + 8);
            }
            return ar;
        }

        @Override
        public String toString() {
            return title + "\n" + text + "\n";
        }
    }

    public class Main {

        public ArrayList<Section> parseBook(String text) {
            ArrayList<Section> sections = new ArrayList<Section>();
            if (text.indexOf("<annotation>") != -1) {
                sections.add(new Section(text.substring(text.indexOf("<annotation>") + 13, text.indexOf("</annotation>") + 14), "<annotation>"));
            }
            //TODO: fix
            if (text.indexOf("<image>") != -1) {
                String image_hre = text.substring(text.substring(text.indexOf("<image>")).indexOf("href=\""), text.substring(text.indexOf("<image>")).indexOf("href") + 50);
                String image_href = image_hre.substring(image_hre.indexOf("#"), image_hre.indexOf("\""));
                sections.add(new Section("aaa", "<image>"));
            }
            if (text.indexOf("<section>") != -1) {
                //sections.addAll(Section.parseSections(text.substring(text.indexOf("<section>"))));
                sections.addAll(Section.parseSections(text.substring(text.indexOf("<body>"), text.indexOf("</body>") + 7)));
            }
            return sections;
        }

        public void main(String[] args) {
            String text = "";
            try {
                URL url = new URL("https://paraknig.com/files/9171/fb2");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String inputLine;
                StringBuilder textBuilder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    textBuilder.append(inputLine);//Можно   накапливать в StringBuilder а потом присвоить перемной String результат накопления
                }
                in.close();
                text = textBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(parseBook(text).toString());
        }
    }


}
