package com.din.hopcomssorted;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.listeners.OnScrollListener;
import de.codecrafters.tableview.listeners.TableHeaderClickListener;
import de.codecrafters.tableview.providers.TableDataRowBackgroundProvider;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;



import com.google.android.material.snackbar.Snackbar;

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

    List<Items> itemsallFinal = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        createShortCut();

        if (!haveNetworkConnection()) {
            Snackbar.make(findViewById(android.R.id.content), "Please Switch on Internet and start again", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
            return;
        }

        new FetchWebsiteData().execute();


    }


    //=========================================================== End of On Create


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


    private class FetchWebsiteData extends AsyncTask<Void, Void, Void> {

        HashMap<String, Float> temp = new LinkedHashMap<>();
        List itemall = new ArrayList();
        int size = 0;
        String date = null;

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
                if (dateElement.hasText()) date = dateElement.get(0).text().toString();

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
                items.addAll(items2);//====================================> main items list


                for (Element item : prices1stlist) {
                    if (item.hasText() == true)
                        prices1.add(Float.valueOf(item.text()));
                }
                for (Element item : prices2ndlist) {
                    if (item.hasText() == true)
                        prices2.add(Float.valueOf(item.text()));
                }
                prices.addAll(prices1);
                prices.addAll(prices2);//====================================> main prices list

                String[] itemsArray = items.toArray(new String[0]);
                Float[] pricesArray = prices.toArray(new Float[0]);

                size = items.size();
                //System.out.println("Total Items Listed "+size);


                for (int i = 0; i < size; i++) {
                    Items item4 = new Items(itemsArray[i], pricesArray[i]);
                    itemall.add(item4);//====================================> Final data list
                }

            } catch (IOException e) {

                Log.d("error", "url is not connecting");
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {


            List<String> fruits = new ArrayList<String>(Arrays.asList("Anjura/Fig", "Apple Chilli", "Apple Delicious",
                    "Apple Fuji chaina", "Apple Green smith", "Apple Washington", "Banana chandra", "Banana Nendra", "Banana pachabale",
                    "Banana Yellaki", "Chicco(Sapota)", "Grapes Blore blue", "Grapes Flame", "Grapes Krishna sharad", "Grapes Red globe",
                    "Grapes Sonika", "Grapes T.S.", "Guava", "Guava Allahabad(Red)", "Indian Black globe Grapes", "Mango Alphans", "Mango Alphans box",
                    "Mango Amarpalli", "Mango Badami", "Mango Bygan palli", "Mango Dasheri", "Mango kalapadu", "Mango Kesar",
                    "Mango malagova", "Mango mallika", "Mango Neelam", "Mango Raspuri", "Mango sakkaregutti", "Mango Sendura",
                    "Mango thotapuri", "Mosambi", "Orange", "Orange Australia", "Orange Ooty", "Papaya nati", "Papaya red indian",
                    "Papaya sola", "Papaya Taiwan", "Pineapple", "Pomegranate Bhagav", "S.mellon Local (Luck)", "Straw Berry",
                    "Watermellon", "Watermellon kiran", "Lime Local", "Amla", "Apple Australia", "Apple Chaina Delicious", "Apple Economy",
                    "Apple Golden delicious", "Apple hazarath palli", "Apple Newzeland", "Apple Premium", "Apple Simla", "Badami",
                    "Banana cooking R.Banana", "Banana karpura", "Banana Rasabale", "Ber fruit/Bore fruit", "Berry ball", "Berry Delhi",
                    "Berry fruit Ooty", "Berry Golden", "Berry Green", "Berry Soft", "Berry Southafrica", "Bore fruit", "Bread fruit", "Butter fruit",
                    "Byladha Hannu", "Chain Berry", "Cherry fruit", "Cherry kashmir", "Cherry Tomoto", "Chicco(Sapota) rapined", "Custerd Apple",
                    "Grapes Crimson", "Grapes Dilkush", "Grapes Rose", "Grapes Sharad", "GrapesAnabi shahi", "Grapesh Tash ganesh", "Indian kinora apple",
                    "Indian red globe", "Jack fruit", "Jambo fruit(Nerale)", "Kiwi fruit", "Komark fruit", "Lime Bijapur", "Lime Local", "Litchi Local",
                    "Litchi Taiwan/chaina", "Malenian apple", "Mango", "Mango Langada", "Mango rathnagiri(red)", "Mango Raw Amblet", "Mosambi polo",
                    "Nagapur Orange Economy", "Orange Nagpura", "Orange South Africa", "Papaya Red lady", "Peaches", "Pomegranate", "Pomegranate A.raktha",
                    "Pomegranate Ganesh", "Pomello/Chakotha", "Rampal", "Rose apple", "Rumenia Mango", "S.mellon namdhari", "S.mellon namdhari (Red)",
                    "South Af.red berry", "South Africa Gala apple", "Times Rose berry", "Washington gala apple", "Washington red apple", "Y.Bananan T.N."));
            List<String> LeafyVegetables = new ArrayList<String>(Arrays.asList("Basale Greens", "Chakota greens", "Dhantu greens", "Greens Sabbakki", "Corriander Leave", "Curry leave", "Mint Leaves",
                    "Arive greens", "Chilakarive green", "Kashini (Ganike) Greens", "Kashini greens", "Letteus Greens", "Parsley", "Selari", "Menthya Greens"));
            List<String> others = new ArrayList<String>(Arrays.asList("Eggs", "Brahmi amla juice", "Apple juice", "Cashew nut",
                    "Chocolates Drink box", "Tamarind Paste 150 gm", "Tamarind Paste 450 gm", "Tamarind seedless", "Tamarind sweet 500gm", "Tamrind Chatisghar",
                    "Tender Coco packed", "Tender Coconut", "Tender Coconut(M)", "Tender Coconut(S)", "Thiland Jelly", "Thiland Juice", "Sweet corn",
                    "Sweet corn cleaned", "Sweet corn seeds", "Sweet corn seeds spok", "Sun safal Tin 15 lt", "Sungold oil 5 lt", "Sungold oil 5 lt packet",
                    "Stevia powder", "Safal deep 500 ml", "Rice Bran 15 lt Tin", "Rice Bran 5 lt", "Pumpkin Red", "Pomegranate Juice", "Pineapple juice", "Pista",
                    "Plum Australia", "Plum Ooty", "Pepper", "Pickles", "Pickles mixed veg", "Orange juice", "Orange malt", "Net rich 5 lt oil", "Mushroom Button",
                    "Mushroom Milky", "Mushroom Oyster", "N.Juice 200 ml", "Mineral water 1 lt", "mango Pickles", "Maize", "Lime Pickles", "Litchi Juice", "Jam 1 kg",
                    "Jam 1/2 kg", "Jam 100 gm", "Jam 200 gm", "Honney 1 Kg", "Honney 1/2", "Honney 100 gm", "Honney 200 gm", "Gulkan 1 kg", "Gulkan 1/2 kg",
                    "Gulkan 1/4 kg", "Ground nut Hybrid", "Ground nut Local", "Ground nut oil 1 lt", "Ground nut oil 5 lt", "Coconut (B)", "Coconut (M)", "Coconut (OS)", "Coconut (S)", "Coconut oil 500 ml", "Copra", "Cowpea Local", "Cowpea Long",
                    "Dates", "Dates Arebian", "Dates seedless", "Dry apricot", "Dry dates", "Dry fruit mixed", "Fanda", "Fruit Juice Tailand", "G. nut Golden oil 1 lt",
                    "G. nut oil Rice 1 lt", "G. nut oil safal deep1 lt", "G. nut sungold 1 lt", "G.nut oil Premium1 lt", "G.nut oil Rice Bron 1 lt",
                    "G.oil Net rich 500 ml", "Grapes Dry 100gm", "Grapes Dry 250gm"));


            Log.d("Itemsize", "==============================================>" + itemall.size());
            itemsallFinal = itemall;


            List<Items> vegList = new ArrayList<>();
            List<Items> greensList = new ArrayList<>();
            List<Items> fruitsList = new ArrayList<>();
            List<Items> othersList = new ArrayList<>();


            for (Items i : itemsallFinal) {
                if (fruits.contains(i.getName())) {
                    fruitsList.add(i);
                } else if (LeafyVegetables.contains(i.getName())) {
                    greensList.add(i);
                } else if (others.contains(i.getName())) {
                    othersList.add(i);
                } else {
                    vegList.add(i);
                }
            }

            String[] TABLE_HEADER_Veg = {"Vegetable nos: " + vegList.size(), "Price"};
            String[] TABLE_HEADER_Green = {"Green nos: " + greensList.size(), "Price"};
            String[] TABLE_HEADER_Fruit = {"Fruit nos: " + fruitsList.size(), "Price"};
            String[] TABLE_HEADER_Other = {"Other nos: " + othersList.size(), "Price"};

            SortableTableView sortableTableView = (SortableTableView) findViewById(R.id.tableView);
            sortableTableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), TABLE_HEADER_Veg));
            sortableTableView.setDataAdapter(new ItemsTableDataAdapter(ScrollingActivity.this, vegList));
            sortableTableView.setColumnComparator(0, new ItemsComparator());
            sortableTableView.setColumnComparator(1, new ItemsPriceComparator());
            sortableTableView.addHeaderClickListener(new MyHeaderClickListener());
            sortableTableView.addOnScrollListener(new MyOnScrollListener());
            //TextView tv = (TextView) findViewById(R.id.tv);
            //tv.setText("Hold one finger on other table and scroll with other finger");

            SortableTableView sortableTableView2 = (SortableTableView) findViewById(R.id.tableView2);
            sortableTableView2.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), TABLE_HEADER_Green));
            sortableTableView2.setDataAdapter(new ItemsTableDataAdapter(ScrollingActivity.this, greensList));
            sortableTableView2.setColumnComparator(0, new ItemsComparator());
            sortableTableView2.setColumnComparator(1, new ItemsPriceComparator());
            sortableTableView2.addHeaderClickListener(new MyHeaderClickListener());
            sortableTableView2.addOnScrollListener(new MyOnScrollListener());

            SortableTableView sortableTableView3 = (SortableTableView) findViewById(R.id.tableView3);
            sortableTableView3.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), TABLE_HEADER_Fruit));
            sortableTableView3.setDataAdapter(new ItemsTableDataAdapter(ScrollingActivity.this, fruitsList));
            sortableTableView3.setColumnComparator(0, new ItemsComparator());
            sortableTableView3.setColumnComparator(1, new ItemsPriceComparator());
            sortableTableView3.addHeaderClickListener(new MyHeaderClickListener());
            sortableTableView3.addOnScrollListener(new MyOnScrollListener());

            SortableTableView sortableTableView4 = (SortableTableView) findViewById(R.id.tableView4);
            sortableTableView4.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), TABLE_HEADER_Other));
            sortableTableView4.setDataAdapter(new ItemsTableDataAdapter(ScrollingActivity.this, othersList));
            sortableTableView4.setColumnComparator(0, new ItemsComparator());
            sortableTableView4.setColumnComparator(1, new ItemsPriceComparator());
            sortableTableView4.addHeaderClickListener(new MyHeaderClickListener());
            sortableTableView4.addOnScrollListener(new MyOnScrollListener());

            sortableTableView.setDataRowBackgroundProvider(new ItemPriceRowColorProvider());
            sortableTableView2.setDataRowBackgroundProvider(new ItemPriceRowColorProvider());
            sortableTableView3.setDataRowBackgroundProvider(new ItemPriceRowColorProvider());
            sortableTableView4.setDataRowBackgroundProvider(new ItemPriceRowColorProvider());

            /*int colorEvenRows = getResources().getColor(R.color.white);
            int colorOddRows = getResources().getColor(R.color.gray);
            sortableTableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
            sortableTableView2.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
            sortableTableView3.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
            sortableTableView4.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));*/
        }


    }

    enum ENUM {
        FRUITS, LEAFY_GREENS, OTHERS, VEGETABLES
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    private class ItemsPriceComparator implements Comparator<Items> {
        @Override
        public int compare(Items item1, Items item2) {
            return item1.getPrice().compareTo(item2.getPrice());
        }
    }

    private class MyHeaderClickListener implements TableHeaderClickListener {
        @Override
        public void onHeaderClicked(int columnIndex) {
            String notifyText = "clicked column " + (columnIndex + 1);
            //Toast.makeText(getContext(), notifyText, Toast.LENGTH_SHORT).show();
        }
    }

    private static class ItemsComparator implements Comparator<Items> {
        @Override
        public int compare(Items item1, Items item2) {
            return item1.getName().compareTo(item2.getName());
        }
    }

    public class ItemsTableDataAdapter extends TableDataAdapter<Items> {

        //List<Items> itemsall = new ArrayList<>();
        public ItemsTableDataAdapter(Context context, List<Items> data) {
            super(context, data);
            // this.itemsall = data;
            Log.d("Constructor", "Constrsize=========>" + data.size());
        }

        @Override
        public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
            Items item = getRowData(rowIndex);
            View renderedView = null;
            // Context context = parentView.getContext();
            switch (columnIndex) {
                case 0:
                    renderedView = renderItemsName(item, parentView);
                    break;
                case 1:
                    renderedView = renderPriceName(item, parentView);
                    break;
            }
            return renderedView;
        }

        private View renderPriceName(Items item, ViewGroup parentView) {

            return renderString(item.getPrice().toString());
        }

        private View renderItemsName(Items item, ViewGroup parentView) {

            return renderString(item.getName());
        }

        private View renderString(final String value) {
            final TextView textView = new TextView(ScrollingActivity.this);
            Log.d("Value", "=========================>" + value);
            textView.setText(value);
            textView.setPadding(20, 10, 20, 10);
            textView.setTextSize(14);
            return textView;
        }

    }

    private class MyOnScrollListener implements OnScrollListener {
        @Override
        public void onScroll(final ListView tableDataView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {



           tableDataView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Log.v("TAG","CHILD TOUCH_SCROLL, totalItemCount "+totalItemCount);
                    Log.v("TAG","CHILD TOUCH_SCROLL, firstVisibleItem "+firstVisibleItem);
                    Log.v("TAG","CHILD TOUCH_SCROLL, visibleItemCount "+visibleItemCount);
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }

        @Override
        public void onScrollStateChanged(final ListView tableDateView, final ScrollState scrollState) {

          /*  if(scrollState == ScrollState.TOUCH_SCROLL){
            // listen for scroll state changes
            tableDateView.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    Log.v("TAG","CHILD TOUCH_SCROLL");
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            }else if(scrollState == ScrollState.IDLE){
                tableDateView.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        Log.v("TAG","CHILD IDLE");
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                });
            }else if(scrollState == ScrollState.FLING){
                tableDateView.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        Log.v("TAG","CHILD Fling");
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                });
            }*/
        }
    }

    private   class ItemPriceRowColorProvider implements TableDataRowBackgroundProvider<Items> {


        @Override
        public Drawable getRowBackground(final int rowIndex, final Items item) {
            int rowColor = ContextCompat.getColor(getApplicationContext(), R.color.white);

            if(item.getPrice() <= 50) {
                rowColor = ContextCompat.getColor(getApplicationContext(),R.color.light_green);
            } else if(item.getPrice() > 50) {
                rowColor = ContextCompat.getColor(getApplicationContext(),R.color.light_red);
            }

            return new ColorDrawable(rowColor);
        }
    }

}

