package com.din.hopcomssorted;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        createShortCut();

        if (!haveNetworkConnection()) {
            Snackbar.make(findViewById(android.R.id.content), "Please Switch on Internet", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //pullToRefresh.setRefreshing(true);
            return;
        }

        new FetchWebsiteData().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView txttitle = (TextView) findViewById(R.id.id_text);


                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("message/rfc822");
                //intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                intent2.putExtra(Intent.EXTRA_SUBJECT, "Today's HOPCOMS sorted categorized price list");

                intent2.putExtra(Intent.EXTRA_TEXT, txttitle.getText());
                startActivity(intent2);


            }
        });
        Log.d("success", "success");

    }

    public void createShortCut() {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcutname));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.banana);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), ScrollingActivity.class));
        sendBroadcast(shortcutintent);
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent2 = new Intent();
            intent2.setAction(Intent.ACTION_SEND);
            intent2.setType("message/rfc822");
            intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{"dineshr93@gmail.com"});
            intent2.putExtra(Intent.EXTRA_SUBJECT, "Type your feedback");
            //TextView txttitle = (TextView) findViewById(R.id.id_text);
            intent2.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(intent2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class FetchWebsiteData extends AsyncTask<Void, Void, Void> {
        String title = "";
        int size = 0;

        HashMap<String, Float> temp = new LinkedHashMap<>();

        @Override
        protected void onPreExecute() {
            //progress dialog
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HashMap<String, Float> hopcomsmap = new HashMap<>();
                /*CharSequence date = android.text.format.DateFormat.format("MMM_dd_yyyy", new java.util.Date());
                String fileName = date+"_Hopcoms"+".txt";*/

                String url = "https://hopcoms.kar.nic.in/(S(1uvvvq55ledgfuzbeavr2by5))/RateList.aspx";


                Document document = Jsoup.connect(url).get();

                //2nd & 3rdrow
                Elements names1stlist = document.select("#ctl00_LC_grid1 td:eq(1)");
                Elements prices1stlist = document.select("#ctl00_LC_grid1 td:eq(2)");
                Elements names2ndlist = document.select("#ctl00_LC_grid1 td:eq(4)");
                Elements prices2ndlist = document.select("#ctl00_LC_grid1 td:eq(5)");

                //Last updated Date
                Elements dateElement = document.select("#ctl00_LC_DateText");

                //read Last updated date
                if (dateElement.hasText()) title = dateElement.get(0).text().toString();

                Collection<String> items1 = new ArrayList<>();
                Collection<String> items2 = new ArrayList<String>();
                Collection<Float> prices1 = new ArrayList<Float>();
                Collection<Float> prices2 = new ArrayList<Float>();

                Collection<String> items = new ArrayList<String>();
                Collection<Float> prices = new ArrayList<Float>();

                for (Element item : names1stlist) {
                    if (item.hasText() == true)
                        items1.add(item.text());
                }
                for (Element item : names2ndlist) {
                    if (item.hasText() == true)
                        items2.add(item.text());
                }
                items.addAll(items1);
                items.addAll(items2);


                for (Element item : prices1stlist) {
                    if (item.hasText() == true)
                        prices1.add(Float.valueOf(item.text()));
                }
                for (Element item : prices2ndlist) {
                    if (item.hasText() == true)
                        prices2.add(Float.valueOf(item.text()));
                }
                prices.addAll(prices1);
                prices.addAll(prices2);

                String[] itemsArray = items.toArray(new String[0]);
                Float[] pricesArray = prices.toArray(new Float[0]);

                size = items.size();
                //System.out.println("Total Items Listed "+size);

                for (int i = 0; i < size; i++) {
                    hopcomsmap.put(itemsArray[i], pricesArray[i]);
                }
                // Create a list from elements of HashMap
                List<Map.Entry<String, Float>> list =
                        new LinkedList<>(hopcomsmap.entrySet());

                // Sort the list
                Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
                    public int compare(Map.Entry<String, Float> o1,
                                       Map.Entry<String, Float> o2) {
                        return (o1.getValue()).compareTo(o2.getValue());
                    }
                });

                // put data from sorted list to hashmap
                //HashMap<String, Float> temp = new LinkedHashMap<>();
                for (Map.Entry<String, Float> aa : list) {
                    temp.put(aa.getKey(), aa.getValue());
                }

            } catch (IOException e) {
                //Toast.makeText(ScrollingActivity.this, "Please switch on internet! or in build url is not correct",
                //        Toast.LENGTH_LONG).show();
                Log.d("error", "url is not connecting");
            }
            return null;
        }

        int standardLength = 35;
        String filler = "_";
        String finalword = "/-";
        int j = 0;

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            StringBuilder r = new StringBuilder();
            // r.append(System.getProperty("line.separator"));
            //r.append("\n");
            r = printOnlyTitle(r, title);

            r = printOnlyTitle(r, "Total Items Listed " + size);

            List<String> fruits = new ArrayList<String>(Arrays.asList("Anjura/Fig", "Apple Chilli", "Apple Delicious",
                    "Apple Fuji chaina", "Apple Green smith", "Apple Washington", "Banana chandra", "Banana Nendra", "Banana pachabale",
                    "Banana Yellaki", "Chicco(Sapota)", "Grapes Blore blue", "Grapes Flame", "Grapes Krishna sharad", "Grapes Red globe",
                    "Grapes Sonika", "Grapes T.S.", "Guava", "Guava Allahabad(Red)", "Indian Black globe Grapes", "Mango Alphans", "Mango Alphans box",
                    "Mango Amarpalli", "Mango Badami", "Mango Bygan palli", "Mango Dasheri", "Mango kalapadu", "Mango Kesar",
                    "Mango malagova", "Mango mallika", "Mango Neelam", "Mango Raspuri", "Mango sakkaregutti", "Mango Sendura",
                    "Mango thotapuri", "Mosambi", "Orange", "Orange Australia", "Orange Ooty", "Papaya nati", "Papaya red indian",
                    "Papaya sola", "Papaya Taiwan", "Pineapple", "Pomegranate Bhagav", "S.mellon Local (Luck)", "Straw Berry",
                    "Watermellon", "Watermellon kiran", "Lime Local"));
            List<String> LeafyVegetables = new ArrayList<String>(Arrays.asList("Basale Greens", "Chakota greens", "Dhantu greens", "Greens Sabbakki"));
            List<String> NonVeg = new ArrayList<String>(Arrays.asList("Eggs"));


            //Print Vegetale
            r = printTitle(r, ENUM.VEGETABLES.toString());

            for (String key : temp.keySet()) {
                if (!fruits.contains(key) && !LeafyVegetables.contains(key) && !NonVeg.contains(key)) {

                    printFormatedString(r, (++j) + " ", key, temp.get(key).toString(), standardLength, filler, finalword);
                }
            }
            r = printSpace(r);

            //print Leafy_Greens
            r = printRate(r, temp, LeafyVegetables, ENUM.LEAFY_GREENS.toString());

            //print Fruits
            r = printRate(r, temp, fruits, ENUM.FRUITS.toString());

            //print Non_Veg
            r = printRate(r, temp, NonVeg, ENUM.OTHERS.toString());

            j = 0;


            TextView txttitle = (TextView) findViewById(R.id.id_text);
            txttitle.setText(r.toString());
            //mProgressDialog.dismiss();
        }

        private StringBuilder printFormatedString(StringBuilder r, String index, String key, String value, int standardLength, String filler, String finalword) {

            int length = index.length() + key.length() + value.length();

            int fillerSize = standardLength - length;
            String finalWord = "", temp = "";

            for (int i = 0; i < fillerSize; i++) {
                temp = temp + filler;
            }

            finalWord = index + key + temp + value + finalword;
            // r.append("\n");
            r.append(finalWord);
            r.append("\n");
            return r;
        }

        private StringBuilder printRate(StringBuilder r, HashMap<String, Float> hopcomsItems, List<String> types, String title) {
            //int j=0;
            r = printTitle(r, title);
            for (String key : hopcomsItems.keySet()) {
                if (types.contains(key)) {
                    r = printFormatedString(r, (++j) + " ", key, hopcomsItems.get(key).toString(), standardLength, filler, finalword);
                }
            }
            r = printSpace(r);
            return r;
        }

        private StringBuilder printSpace(StringBuilder r) {
            r.append("\n");
            //r.append();
            return r;
        }

        private StringBuilder printTitle(StringBuilder r, String title) {
            //r.append("===============================");
            r.append("\n");
            r = printFormatedString(r, "", title, "(price low to high)", standardLength, " ", "");
            r.append("===============================");
            r.append("\n");
            return r;
        }

        private StringBuilder printOnlyTitle(StringBuilder r, String title) {
            // r.append("===============================");
            r.append("\n");
            r = printFormatedString(r, "", title, "", standardLength, " ", "");
            // r.append("\n");
            r.append("===============================");
            //r.append("\n");
            return r;
        }

    }

    enum ENUM {
        FRUITS, LEAFY_GREENS, OTHERS, VEGETABLES
    }
}

