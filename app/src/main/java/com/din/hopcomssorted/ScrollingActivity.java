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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;



import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class ScrollingActivity extends AppCompatActivity {

    List<Items> itemsallFinal = new ArrayList<>();
    ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
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
        Document document;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        Connection.Response response;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HashMap<String, Float> hopcomsmap = new HashMap<>();
                /*CharSequence date = android.text.format.DateFormat.format("MMM_dd_yyyy", new java.util.Date());
                String fileName = date+"_Hopcoms"+".txt";*/

                //String url = "https://hopcoms.kar.nic.in/(S(1uvvvq55ledgfuzbeavr2by5))/RateList.aspx";
                String url = "http://hopcoms.karnataka.gov.in/CropRates.aspx";
                try {
                    response = Jsoup.connect(url).execute();
                    progressBar.setProgress(50);
                }catch (Exception e){
                    Snackbar.make(findViewById(android.R.id.content), "Error Fetching DATA", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Action", null).show();
                }





                Document document =response.parse();

                //2nd & 3rdrow
                /*Elements names1stlist = document.select("#ctl00_LC_grid1 td:eq(1)");
                Elements prices1stlist = document.select("#ctl00_LC_grid1 td:eq(2)");
                Elements names2ndlist = document.select("#ctl00_LC_grid1 td:eq(4)");
                Elements prices2ndlist = document.select("#ctl00_LC_grid1 td:eq(5)");*/

                Elements names1stlist = document.select(".MachineName");
                Elements prices1stlist = document.select(".Status");

                //Last updated Date
                //Elements dateElement = document.select("#ctl00_LC_DateText");
                Elements dateElement = document.select("#lblDate");

                //read Last updated date
                if (dateElement.hasText()) date = dateElement.get(0).text().toString();

//                Collection<String> items1 = new ArrayList<>();
//                Collection<String> items2 = new ArrayList<String>();
//                Collection<Float> prices1 = new ArrayList<Float>();
//                Collection<Float> prices2 = new ArrayList<Float>();

                Collection<String> items = new ArrayList<String>();
                Collection<Float> prices = new ArrayList<Float>();

                for (Element item : names1stlist) {
                    if (item.hasText() == true)
                        items.add(item.text()); //====================================> main items list
                }
                /*for (Element item : names2ndlist) {
                    if (item.hasText() == true)
                        items2.add(item.text());
                }*/
//                items.addAll(items1);
//                items.addAll(items2);//====================================> main items list


                for (Element item : prices1stlist) {
                    if (item.hasText() == true)
                        prices.add(Float.valueOf(item.text())); //====================================> main prices list
                }
                /*for (Element item : prices2ndlist) {
                    if (item.hasText() == true)
                        prices2.add(Float.valueOf(item.text()));
                }
                prices.addAll(prices1);
                prices.addAll(prices2);//====================================> main prices list*/

                String[] itemsArray = items.toArray(new String[0]);
                Float[] pricesArray = prices.toArray(new Float[0]);

                size = items.size();
                //System.out.println("Total Items Listed "+size);


                for (int i = 0; i < size; i++) {
                    Items item4 = new Items(itemsArray[i], pricesArray[i]);
                    itemall.add(item4);//====================================> Final data list
                }

            } catch (Exception e) {

                Log.d("error", "url is not connecting");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
            List<String> fruits = new ArrayList<String>(Arrays.asList("APPLE DELICIOUS","APPLE SIMLA","APPLE MISRI","APPLE WASHINGTON","APPLE ROYAL GALA","APPLE GOLDEN DELICIOUS","APPLE CHINA VARIETY","APPLE BRAZIL","APPLE DELICIOUS EXPORT","BANANA PACHABALE","BANANA YELLAKKI","BANAN CHANDRA","BANAN NENDEA","CHAKKOTHA FRUIT","BANANA RASABLE","CUSTORD APPLE","CHIKO","GUAVA","GUAVA ALLAHABAD","GRAPES BANGALORE BLUE","GRAPES ANOB E SHABI","GRAPES - DILLKUSH","GRAPES - SHARAD","ITALY APPLE","GRAPES SONIKA","GRAPES FLAME","GRAPES KRISHNA SHOROD","GRAPES CRIM SAN","GRAPES TS","GRAPES RED GLOBE AUSTRALIA","GRAPES DRY 200 gram","JACK FRUITS","Indian Black Globe Grapes","PAPAYA RED INDIAN","ROW MANGO AMARAPALLI","CUSTARD APPLE SAHANA","GUAVA TAIWAN","MANGO RASPURI","MANGO SENDARA","MANGO BYGANPALLI","MANGO MALAGOA","MANGO MALLIKA","MANGO THOTAPURI","MANGO KALAPADU","MANGO NEELAM","MANGO DASHIRI","MANGA LANGADA","MANGA KESAR","MANGO AMARPALLI","MANGO RATHNAGIRI","MANGO SAKKEREGUTTI","ORANGE KODAGU","ORANGE NAGPUR","APPLE CHINA FUZI","ORANGE AUSTRALIA","ORANGE EGYPT","ORANGE SOUTH AFRICA","POMEGRANTE","POME GRANATE BHAGAV","POME GRANATE GANESHA","PAPAYA LOCAL","PAPAYA THAIWAN RED LADY","PAPAYA RED LADY","PAPAYA SOLO","PINE APPLE","PLUMS OOTY","PLUM AUSTRALIA","KAMRAK","DRAGAN FRUIT","ROSE - APPLE","WATER MELON LOCAL","WATER MELON KIRAN","MELON NANDHARI","MOSAMBI","MOSAMBI POLO","SWEET MELON NANDHARI (RED)","SWEET MELON LOCAL","BORE FRUIT","BUTTER FRUIT","PANNERLE FRUITS","APPLE CHILLE","GRAPES INDIAN RED GLOBE","BED FRUIT ALASU","MANGO BADAMI BOX","AMLA","LIME LOCAL","MANGO RAW OMBLET","MANGO RAW","JUKUNI","BANANNA COOKING","APPLE NEWZEALAND GALA","APPLE NEWZEALAND","COCONUT OIL 500ML","SWAGATH JAM 1KG","JAM 500GM","PEPAR 200GM","PEPAR 100GM","PROCEED FAND 290","CHOCOLATES DRINK -1","CARRY BAG","COPRA","Gulkan 1 KG","CARREY BAG BIG","Gulkan 500 GMS","BRAZIL RED LETIES","CUCUMBER WHITE","BANANA LEAVES","SWEET CORN SEED BOIL","BALE HOOVU","LETTEUS","ONION FLOWER","BERRY CHINA","SUGAR BANANA","CORN MIZE PACK","BYALADHA HANNU","Apple premium","APPLE KINNOR","SOUTH AFRICA GALA APPLE","Leeks","Parsle","Litchi Punjab","Grapes Tash ganesh","Rose berry","Washington Gala Apple","MANGO Alfhans Box","Sweet melon Laknow","Newzealand royal gala apple","KADAPA WATER MELON","KADAPA WATER MELON","MANGO NATI","MANGAO ROMANIYA","MANGO ROMANIA Raw","Romaton","Thothapuri Red","Egg Fruit","Paneer Grapes","Neelam Periculam","Dates Fruit","Naspathi berry","Orange Kino","South Africa chakotha","Fashion Fruit","MANGO LANGADA","Mosambi Medium","APPLE ITALY VARIETY","Misery Apple","Chaina Delicious","APPLE ITALY","Grapes sonaka super Box","Green Apple","CHAINA VARIETY APPLE","Orange Economey","Mango Badami","THOTHAPURI","Mango Malagova (M)","Jammu narale fruit","American Misery Apple","BANANA(Buda)","Amla Nakshratra","CHAKKOTA","Painapal (Shirasi)","CUSTORD APPLE RAM","APPLE TURKI (Puji)","HALASINA KAI","ORANGE GRADE II","POME GRANTE BHAGVA SMALL","JACK FRUIT RAW","MIXED CUT VEGITABLE BOX","Bore fruits big","ROW MANGO SAKKAREGUTTI","SENDURA RAW MANGO","APPLE FRANCE","APPLE POLAND","GRAPES WASHIGHTON RED GLOB","GRAPES INDIAN BLACK RED GLOB","GRAPES SONAKA SUPER","GRAPES KRISHNA SHARAD SUPER","SWEET MELON SUN","ROW MANGO BYGANAPALLI","ROW MANGO KALAPADU","ROW MANGO DASARI","ROW MANGO RASAPURI","ROW MANGO MALLIKA","ROW MANGO BADAMI","ROW MANGO MALAGOVA","ROW MANGO NATI","JUMBO NERALE","JACK FRUITS BOX","APPLE BELGIUM","MOSAMBI NEW","TAMILNADU BANANA","POTATO (LOCAL)","LECCHI","APPLE ROSE","APPLE CHILLI","KASHMIR CHERRY","MANGO PERIKULAM","GAJ LIME","GOLDEN APPLE","Hajrath palli apple","kobbari hannu","MANGO DASARI","Bitrot Oty","APPLE RED GOLD",
                    "Anjura/Fig", "Apple Chilli", "Apple Delicious",
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
            List<String> LeafyVegetables = new ArrayList<String>(Arrays.asList("CHILLIES (GREEN)","CHILLIES BAJJI",
                    "CHILLIES CLEANED","CHILLIES SMALL","CORIONDER","CURRY LEAVES","DHANTU","MINT","MENTHIYA","PALAK","PARVAL","SABBAKKAI","BASALE GREENS","AREVE GREEN","CHAKKOTA GREENS","CORRIANDER LEAVES NATI","Selari",
                    "NATI CORIONDER","LUSON GREENS",
                    "Basale Greens", "Chakota greens", "Dhantu greens", "Greens Sabbakki", "Corriander Leave", "Curry leave", "Mint Leaves",
                    "Arive greens", "Chilakarive green", "Kashini (Ganike) Greens", "Kashini greens", "Letteus Greens", "Parsley", "Selari", "Menthya Greens"));
            List<String> others = new ArrayList<String>(Arrays.asList("TAIWAN GELLY 100 GM","DRY APRICOT","DRY GRAPES 200GM","Tender coconut Big","CHERRY FRUIT Box","Anjura Fruit box","SWEET TOMRIND(Thailand) 250 gram","Anjura","CORN BAILED 120GMS","CORN BAILED 300GMS","Raw Almond","SOUTH AFRICA SOFT BERRY","On Juice 200ML","On Juice Apple 1Lts","N Juice 200 ml","KF 500 ml","Sugar can(small) 1 No.","MUSHROOM BUTTON","MUSHROOM MILKY","MUSHROOM OYSTER","GARLIC 100 GRAM BOX","GARLIC","GARLIC CLEANED","EGG","BABY CORN","BABY CORN CLEAN","BERRY POLAR","BERRY GOLDEN","Onion cleaned","OOTY BERRY","SANDOWS GREEN BERRY","BERRY SOFT","GREEN BERRY","BER FRUIT","CHERRY FRUIT 100 gram","CHERRY KASHMIR","CASHEWNUT100 gram","DATES 200 GM","KIMIA DATES","DATES SEEDLESS","DRY DATES 200 GM","BADAMI 100 GM","DRY GRAPES 100GMS","JELLY","SUGAR CANE RED AND BLACK","KF 2 litre","KIWI FRUIT","LITCHI LOCAL","LITCHI THAIWAN / CHAINA","STRAWBERRY BOX","TENDER COCONUT","Nectro Plus Honey 250 gm","TOMRIND SEEDLEES","THINDA","DATES AREBIAN","PISTA 100 gram","TENDER COCONUT SMALL","TENDER COCONUT PACKED","FRUITJUICE THAILAND","GROUND NUT OIL NET RICE500M","GROUND NUT OIL 1LTR","GROUND NUT PREMIUM 1LTR","GROUND NUT RICE BRON 1LTR","GROUND NUT OIL RICE 1LTR","SAFAL DEEP 1L OIL","GROUND NET GOLDEN G NUT1L","GROUND NUT SUN GOLD 1LTR","RICE BRON 15LTS","NISARGA PICKLES 500GMS","LIME PICKLE","Nisarga Pickles 300 gm","MIXED VEGETABLES PICKLE","TAMRIND PASTE 150GM","RICE BRON OIL 5LTR","SUN SAFAL 500ML","GROUND NUT OIL 5LTR","SAFAL DEEP 500ML","TAMRIND PEAST 400GM","SUN SAFAL 1 TIN 15LTS","SUN GOLD 5LTS","Tharaguni kai","On Juice Pine Apple 1Lts","Pineapple Juice","On Juice Cherry Berry 1Lts","Iran Dates","Orange Juice","Swagath 879 ml","Netrich 5 Lts","PANNEER WHITE","NISARGA PICKLES 200GMS","PICKELS 200 GM","PICKLES 500 Gram","NISARGA PICKLES 5KG","PICKLES 1KG","Paper Boat 250ML","On Juice Orange 1Lts","Bnatrual Pomegranate 200ML","PAPER BOAT POMEGRANATE","BABYCORN CLEANED BOX","Yellenne","DRY ANJURA 100 gram","Premium 1Ltr","Orange Polo","DATES(Bunch)","On Juice PomeGrante 1Lts","SWAGATH JAM 500 GMS","JALJEERA JUICE","Sun Lucknow Melon","Mango Jelly Taiwan 100ML","Tamrind","Perimum 5 Litre","Perimum 5Litre","Cherry fruits","Nisarga Honey 1KG","Nisarga Honey 500GM","Nisarga Honey 200GM","Nisarga Honey 100GM","Kashmir Nakh Berry","saft berry U.S.A","Kashmir Kabul Varitey","Kashmir Gulkan 1KG","Kashmir Gulkan 500GMS","Gulkan 250 GMS","TAMRIND PASTE 227GMS","BIJI CHILLI UGIN","KF 1litre","Mixed Juice","On Juice Guava 1Lts","PERIMIUM 15LTRS","Chaina Red Delicious","Uttama bamul","Tin Juice","THOGARIKAI SEEDS","CHAKADIKAI ROUND CLEANED","CHAINA VARIETY","Shankaranthi mix 250GMS","Shankaranthi Sweets 100GMS","Shankaranthi Mix 500 mg","Thailand juice 100ML","Jack small","N Juice J","N Juice","Raw Jack Cleaned","Nil","Jack fruit packet","Sun Sweet melon","BEANS RING","AMLA STAR","APPLE SIMLA ECONOMY POCKET","APPLE NEWZILAND","ORANGE MALT","DRY GRAPES","CASHEW NUT","KING FISHER 1 LTR","BISLERI 2LTR","BISLERI 1LTR","BISLERI 5LTR","BISLERI 500ML","HONEY NECTOFRESH 1KG","HONEY NECTOFRESH 0.5KG","HONEY NECTOFRESH 100 GM","HONEY NECTOFRESH 200GM","HONEY KARNATAKA EPIARIES 250 GM","HONEY KARNATAKA EPIARIES 500 GM","HONEY KARNATAKA EPIARIES 1 KG","PICKLE LIME 200 GM","PICKLE LIME 500 GM","PICKLE APTEMADI 1 KG","NECTOR FRESH HONEY 1 KG","NECTOR FRESH HONEY 500 GM","NECTOR FRESH HONEY 250 GM","NECTOR FRESH HONEY 100 GM","NECTOR FRESH HONEY 50 GM","NECTOR FRESH FOREST HONEY 500 GM","NECTOR FRESH FOREST HONEY 200 GM","WET DATES PACKET","SHAVIGE","PASTE( GARLIC/ GINGER)","NERALE JUICE","ON JUICE 200 ML","JALJEERA JUICE 250 ML","JALJEERA JUICE 1 LTR","BNATURAL 200 ML","BNATURAL 200 ML BRAHMI","PREMIUM 1 LTR","GROUNDNUT OIL 15LTR","UTTAM PALM OIL 15 LTR","TENDER COCONUT 200 ML","SUNGOLD 2 LTR","COCONUT OIL 1 LTR","COCONUT OIL 100 GM","COCONUT OIL 1 kg","MULTI MILLI NUTRI FLOOUR","POSHTIK ROTI MIX","WHEAT NUTRI FLOUR","Amul cool cafe","RICE RICH","Amul can","Amul true","LITTLE MILLET (NAVANE)","FOXTAIL MILLET( SAJJE)","HURULI KALU","NAVANE HITTU 0.5 KG","RICE ARKA 0.5","SAAME RICE 0.5 KG","BARAGU RICE 0.5 KG","NAVANE RICE 0.5 KG","HOODHALU 0.5 KG","SAAME 1 Kg","SAJJE 1 Kg","HUDHALU 1 KG","MILLET MORNING HAMBLI 0.5 KG","NAVANE HITTU 1 KG","INST. RAVA IDLI MIX 500 GM","INST PLAIN UPMA MIX 170 GM","INST GULAB JAMUN MIX 200 GM","INST VERMICELLI PAYASAM MIX 180 GM","INST BADAM DRINK MIX 12 GM","INST BADAM DRINK MIX 100 GM","INST BADAM DRINK 200 GM","RASAGOLLA 120 GM","GULAB JAMUN PORTION PACK 100 GM","RASMALAI PORTION PACK 100 GM","BASUNDI PORTION PACK 100 GM","Butter fruits (S)l","SPICY POWDERS","PILIOGARE POWDER 35 GM","PILIOGARE POWDER 100 GM","PILIOGARE POWDER 200 GM","VANGI BATH 20 GM","VANGI BATH 100 GM","VANGI BATH 200 GM","CHUTNEY POWDER 100G","CHUTNEY POWDER 200G","SAMBAR POWDER 20G","SAMBAR POWDER 100G","SAMBAR POWDER 200G","BISIBELE MASALA 20G","BISIBELE MASALA 100G","VERMICELLI 400G","PICKLES POUCH","MANGO PICKLE SLICED POUCH","LIME PICKLE SLICED POUCH","MIXEDVEGETABLES PICKLE SLICED POUCH","MTR SNACKS UP","HOPCOMS WATER 0.5 LTR","HOPCOMS WATER 1 LTR","HOPCOMS WATER 2 LTR","GEMSIP 200 ML","GEMSIP 500 ML","POUSHTIK ROTI MIX 0.5 KG","HOODALU","PARSIMAN","MANGO STIN","MORNING HAMBLI","TROPICANA JUICE","COCOJAL","LASSI","BADAMI MILK","BUTTER MILK","Amul kadai bottle","RAGI ITTU","JOLADHA ITTU","SWEET JEERA","WET PEA BOX","YELLU BELLA MIX 1 kg","WET KASURI","SWEETJIRIGE","YELANEERU BIG","COCONUT OIL","ICE BURG","DATES","Peeches","CORN (Local)","Gold winner 1 Lt","Gold winner 5 Lt","Nurti Life 1 No 200gram","GRPES TS","Newtry live Juice 200 grm","Aarka","Saame","Baragu","Udlu","Navane","Raagi hittu","Sajje","Morning amlee","Poustik rotti","Navane uppittu","Maize Neutririch","Multi milit","Raagi multi green","Navane hittu","Jolada hittu","Sajje hittu","Health drinks 100g","Dose & Edli hittu 200 g","Dose & Edli hittu 400 g","Finger milit dose 400 g","Finger milit edli 400 g","milit box 900 g","water 500 ml","water 1 ltr","water 2 ltr","water 2 ltr(Bislary & KF)","Gem sip juice 200 ml","Gem sip juice 500 ml","lehaberry NT life 200 ml","Paper boot Alphance 180 ml","Paper boot Alphance 200 ml","Paper boot Alphance 250 ml","Paper boot Majjige 200 ml","Paper boot Pomogranet & Nerale 250 ml","Paper boat Chiki 18 g","Paper boat Chiki 35 g","Coco gel 200ml","Nandini milk 200 ml","Ghee 200 ml","Ghee 0.5 kg","Ghee 1 kg","Nandini M.L 200 ml","Nandini Plain lassi 200 ml","Nandini cuckies ( 1 jar)","Nandini peda","Nandini dharvada peda","Amul milk 200 ml","cool caffe 200 ml","Premium milk 200 ml","Amul badami milk 200 ml","Amul lassi 200 ml","lassi 200 ml","Amul masthi","Rava Idli Mix 1Kg","Rava Idli Mix 500g","Rava Idli Mix 200g","Masala Idli Mix 500g","KOKOZAL","LECCHI (Thaiwan)","Vada mix 200g","Dosa mix 500g","Dosa mix 200g","Rice Idli Mix 500g","Rice Idli Mix 200g","Rava dosa mix 500g","Masala Upma mix 180g","Ragi dosa mix 500g","Ragi rava idli mix 500g","Oats idli mix 500g","Multi grain Dosa mix 500g","Regular poha 180g","Vermicelli upma 170g","Regular poha cuppa 80 g","Gulab jamun mix 200g","Gulab jamun mix 100g","Gulab jamun mix 40g","Gulab jamun mix 500g","Varmicelli (Payasa)","Badam drinks mix 500g","Badam drinks mix 200g","Badam drinks mix 100g","Badam drinks mix 12g","Sambar mix 200g","Sambar Powder Spicy 200g","Sambar Powder Spicy 100g","Sambar Powder Spicy 40g","Sambar Powder Spicy 20g","Bajji and Bonda mix 200g","Gobi manchurian 300g","Rasagulla portion 120g","Rasagulla portion 500g(tin)","Gulab Jamun (Portion) 100g","Gulab Jamun (Portion) 500g(tin)","Badam Drink 180L","Chilli Powder 200g","Chilli Powder 100g","Chilli Powder 50g","Turmeric Powder 200g","Turmeric Powder 100g","Turmeric Powder 50g","Corinder powder 200g","Corinder powder 100g","Corinder powder 50g","Jeera Powder 50g","Jeera Powder 15g","Papper powder 50g","Papper powder 10g","TN. Sambar powder 100g","Spice curry Powder 50g","Puligare Paste 200g","Vermicelli (Sayam) 900g","Vermicelli (Sayam) 400g","Vermicelli (Sayam) 165g","Roost Vermicelli 430g","Puliyogare Powder 200g","Puliyogare Powder 100g","Puliyogare Powder 35g","Vangi bath Powder 200g","Vangi bath Powder 100g","Vangi bath Powder 20g","Spiced Chutney Powder 30g","Spiced Chutney Powder 100g","Spiced Chutney Powder 200g","Rasam Powder 200g","Rasam Powder 100g","Rasam Powder 20g","Bisibelebath Masala 200g","Bisibelebath Masala 100g","Bisibelebath Masala 20g","Palao Masala 10g","TN. Biriyani Masala 15g","Garam Masala 50g","Garam Masala 10g","Chat Masala 100g","Mango Thokku Pickle 300g","Mango Thokku Pickle 500g","Lime Pickle 300g","Lime Pickle 500g","Mango Sliced Pickle 300g","Mango Thokku Pickle 300g","Tomatto Pickle 300g","Erdicks pickles 300g","lime/ mixed pickle 200g","Mango sliced Pickle 200g","Mango/Lime/Tomato pickle 12g","Mango/Lime/Tomato pickle 60g","Snackupallu 30g - 35g","Snackupallu 40g","Snackupallu 180g","Snackupallu 90g","NEWZELAND QUSE","CHUTNEY POWDERS 200G","KADAK ROTTI 300G","COCONUT CHETNEY POWDER","KARDONT 250G","lADAGI LADU 250G","BELGAVMKUNDA 250G","ANTINA HUNDE 250G","THAKUR PEDA 250G","PICKLE LIME","Parsumon (Japan mango)","Parsumon (Japan mango)","kesar gedde","Painaple kerala","beet root ooty","carrate delhi","AUSTRALIA CHAKKOTHA","POME GRANATE JAM 180 g","WINNER 400 Grms","CREAM 12 g BOX","CREAM BIG BOX","MELTO 12 g","MELTO BIG","CRANCHAS","KRANCHOS BIG BOX","DARKA TAAN","DAIRY DREAM BOX","MEGHA BYTE BOX","TREAT","TURBO BOX","KRUST BOX","Campco Bar BOX","ECLAIR JUMB JAR (B) BOX","EKLOR JUMBO ZAR (M) BOX","ECLAIR (ES) BOX","ES STREBURY","GARLIC CLEANED BOX","Badami drink 180ml","Chocolate drink 180ml","LAYS & CHIPS 50 GRAMS","BISCUITS AND CAKES 100GRAMS","JUICE & AMUL COOL 0.500","Egg Fruits","BANANA (Black)","GRB GHEE RS.5/-","GRB GHEE RS.10/-","GRB GHEE RS.20/-","GRB GHEE 50ml SP","GRB GHEE 50ml ARP Pjar","GRB GHEE 100ml sp","GRB GHEE100ml ARP Pjar","GRB GHEE 200ml sp","GRB GHEE 200ml sp","GRB GHEE 500ml sp","GRB GHEE 500ml pp","GRB GHEE 500ml ARP Pjar","GRB GHEE 1 ltr pjar","GRB GHEE 1 ltr tin","GRB GHEE 1 ltr pp","GRB GHEE 2 ltr tin","GRB GHEE 5 ltr tin","GRB GHEE 15 ltr tin","YELLU BELLA MIX 250 grm","Grapes jumbo sharad","Egg (Nati)","Thadnikai","APPLE TARKI / IRAN","MANGO BADAMI BOX","BANANA RAIF","MANGO SENDARA KAAI","MANGO BYGANPALLI KAAI","MANGO MALAGOA KAAI","MANGO MALLIKA KAAI","MANGO THOTAPURI KAAI","MANGO KALAPADU KAAI","MANGO NEELAM KAAI","MANGO DASHIRI KAAI","MANGA LANGADA KAAI","MANGA KESAR KAAI","MANGO AMARPALLI KAAI","Tin Juice 250 ML","Tender coconut Big KAAI","MANGO SAKKEREGUTTI KAAI","lehaberry NT life 1 Liter","UMADI KARELA PICKLE 300G","UMADI MAGNI BER PICKLE 300G","UMADI GARLIC PICKLE 300G","UMADI TOMATO PICKLE 300G","UMADI DRY FRUIT PICKLE 300G","UMADI GUNGURA PICKLE 300G","UMADI SWEET LIME PICKLE 300G","UMADI MANGO PICKLE 300G","UMADI THECHA 300G","UMADI TAMRIND PICKLE 300G","UMADI TAMRIND PICKLE 100G","UMADI THECHA 100G","UMADI PLAIN PAPAD 100G","UMADI MASALA PAPAD 200G","UMADI MASALA CHILLY FRY 100G","UMADI SHENGA HOLIGE 5 PC","MADHUR JONY BELLA 1Kg","KOLAPUR ORGANIC BELLA 1Kg","Beans Horikart Oty","AMUL CAFE 200ml","NUTRI LIFE 1ltr 100%","Amul Royal Elaichi","Amul Royal Elaichi","ROSE ONION","Mango Kesar","OTY CARRAT 1ST QUALITY","Gooday buscuits 50g","Gooday buscuits 75g","Gooday buscuits 100g","Gooday buscuits 120g","marigold biscuits 130g","layes 42g","layes 50g","layes 52g","layes 100g","paper boat coconut water 200ml","ROW MANGO KESAR","juice amul 160 ml","juice amul 250ml","juice amul 400 ml","juice amul 600 ml","BANANA PACHABALE","BANANA YELLAKKI","BANAN CHANDRA","BANAN NENDEA","BANANA RASABLE","PERIKULAM ROW MANGO","miland honey multy flora 1 kg","miland honey multy flora 500 g","miland honey multy flora 250 g","miland honey multy flora 125 g","miland honey dry fruits 350g","miland honey leechy 250 g","miland honey jamoon 250g","miland honey forest 250 g","miland honey kashmiri 250 g","Rambostien","Pomegranate Drink 200 ml","Pomegranate Juice 200 ml","Pomegranate Sugar free 200 ml","Pomegranate Squesh 500 ml","Walnet 100 gram packet","Pom Delight Nata D Coco Pudding 100gram","Pom Delight Pudding 100","POMEGRANTE M","HEGADE PICKEL 300Grams","HEGADE PICKEL sp 300Grams","hunasehannu paste","ROTO CLOTH BAG","Brazil","BRAZIL SCOUT","RED JUKUNI","LAKSHMANA PALA","AVAREKARI (Hunasur)","chilli powder","Dhanya powder","sompa powder","sompa powder","Turmeric powder","chilli powder","gulab jamoon","jamoun","Rave idli rave dosa","jollada roti","Saancup","coconut chutney powder","Bisibele bath powder","mysorepack 250gm","mysorepack 100gm","mysorepack 25gm","nandini pedda 100gm","nandini pedda 25gm","nandini butter 500gm","nandini butter 200gm","puneer 1000gm","puneer 500gm","puneer 200gm","good life milk 200ml","good life milk 500ml","good life milk slim 500ml","Fruity 200 g","Enriched sweet 400g","sandwitch supreme 400g","Hi fibre brown 400g","100% whole wheat bread","Enriched sweet bun 200g(5*1)","Wow pav 210g","premium sweet bread rusk 200g","choco sweet fills 50g","Muffins-chocolate 180g","Nutri chais 100 gram","Nutri chais Ragi 150 gram","Mari light ots 100 gram","Nuti chais B 100 gram","Maza 600 gram","Coco light 250 ml","Kusuri girige","cut fruit donne","paper cover","Grapes jumbo sharad Box","mushroom protein papad 50gm","mushroom protein papad 100gm","Mazza 1.5 Liters","red bull 250ml","milk plus 350 gm","tea rusk 150gm","multi grains 450 gm","Garlic New","sun feast biscuit 40 mg","Mango Ginger","Lecchi Thailand","Chow Chow (Oty)","KO KO KOLA 250 ML","Beans (Nati)","Milk chocolate","White chocolate","Crackle chocolate","Fruit & nuts chocolate","Chakke (50 g)","Chakke (100 g)","Cardamom (20 g)","Cardamom (50 g)","Cardamom (100 g)","Jaay kai (50 g)","Ja patre (10 g)","Lavanga (50 g)","Lavanga (100 g)","Black pepper (50 g)","Black pepper (100 g)","White pepper (50 g)","Red chilli powder (100 g)","Red chilli powder (200 g)","Termaric powder (100 g)","Termaric powder (250 g)","Cocom juice (700 ml)","Jamoon sugar less (750 ml)","Brahmi juice (750 ml)","Cooking leaf samber box","Korle","Yam (Thamil nadu/Keral)","Bale dendu(paket)","Bale denu (k.g)","Cherry (box)","Badami(Raw)","Apricart (raw)","MANGO HIMAM PASAND","Grape juice","mango juice","mango juice","mango juice","juice glass","sugarcane juice",
                    "Eggs", "Brahmi amla juice", "Apple juice", "Cashew nut",
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

            /*"Eggs", "Brahmi amla juice", "Apple juice", "Cashew nut",
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
                    "G.oil Net rich 500 ml", "Grapes Dry 100gm", "Grapes Dry 250gm",*/


            /*"Basale Greens", "Chakota greens", "Dhantu greens", "Greens Sabbakki", "Corriander Leave", "Curry leave", "Mint Leaves",
                    "Arive greens", "Chilakarive green", "Kashini (Ganike) Greens", "Kashini greens", "Letteus Greens", "Parsley", "Selari", "Menthya Greens",*/


            /*"Anjura/Fig", "Apple Chilli", "Apple Delicious",
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
                    "South Af.red berry", "South Africa Gala apple", "Times Rose berry", "Washington gala apple", "Washington red apple", "Y.Bananan T.N.",*/
            Log.d("Itemsize", "==============================================>" + itemall.size());
            itemsallFinal = itemall;


            List<Items> vegList = new ArrayList<>();
            List<Items> greensList = new ArrayList<>();
            List<Items> fruitsList = new ArrayList<>();
            List<Items> othersList = new ArrayList<>();


            for (Items i : itemsallFinal) {
                if (fruits.contains(i.getName())&& !(i.getPrice().equals(0.0f))) {
                    fruitsList.add(i);
                } else if (LeafyVegetables.contains(i.getName())&&!(i.getPrice().equals(0.0f))) {
                    greensList.add(i);
                } else if (others.contains(i.getName())&&!(i.getPrice().equals(0.0f))) {
                    othersList.add(i);
                } else if(!(i.getPrice().equals(0.0f))){
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

