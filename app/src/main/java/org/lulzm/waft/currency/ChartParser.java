package org.lulzm.waft.currency;

import android.content.Context;
import android.content.res.Resources;
import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*********************************************************
 *   $$\                  $$\             $$\      $$\   
 *   $$ |                 $$ |            $$$\    $$$ |  
 *   $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |  
 *   $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ | 
 *   $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |  
 *   $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |  
 *   $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |  
 *   \_______| \______/   \__| \________| \__|     \__|  
 *
 * Project : WAFT                             
 * Created by Android Studio                           
 * Developer : Lulz_M                                    
 * Date : 2019-05-12 012                                        
 * Time : 오후 9:35                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class ChartParser {
    private Map<String, Map<String, Double>> map;
    private Map<String, Double> entry;
    // Oldest possible date
    private String date = "1970-01-01";

    // Get map
    public Map<String, Map<String, Double>> getMap() {
        return map;
    }

    // Get date
    public String getDate() {
        return date;
    }

    // Start parser for a url
    public boolean startParser(String s) {
        // Create the map
        map = new LinkedHashMap<>();

        // Read the xml from the url
        try {
            URL url = new URL(s);
            InputStream stream = url.openStream();
            Handler handler = new Handler();
            Xml.parse(stream, Xml.Encoding.UTF_8, handler);
            return true;
        } catch (Exception e) {
            map.clear();
        }

        return false;
    }

    // Start parser from resources
    public boolean startParser(Context context, int id) {
        // Create the map
        map = new LinkedHashMap<>();

        Resources resources = context.getResources();

        // Read the xml from the resources
        try {
            InputStream stream = resources.openRawResource(id);
            Handler handler = new Handler();
            Xml.parse(stream, Xml.Encoding.UTF_8, handler);
            return true;
        } catch (Exception e) {
            map.clear();
        }

        return false;
    }

    // Handler class
    private class Handler extends DefaultHandler {
        // Start element
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) {
            String name = "EUR";
            double rate;

            if (localName.equals("Cube")) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    // Get the date
                    switch (attributes.getLocalName(i)) {
                        case "time":
                            String time = attributes.getValue(i);

                            // Check if more recent
                            if (time.compareTo(date) > 0)
                                date = time;

                            // Create a map for this date
                            entry = new HashMap<>();
                            // Add euro to the entry
                            entry.put("EUR", 1.0);
                            // Add the entry to the map
                            map.put(time, entry);
                            break;

                        // Get the currency name
                        case "currency":
                            name = attributes.getValue(i);
                            break;

                        // Get the currency rate
                        case "rate":
                            try {
                                rate = Double.parseDouble(attributes.getValue(i));
                            } catch (Exception e) {
                                rate = 1.0;
                            }

                            // add new element to the entry
                            entry.put(name, rate);
                            break;
                    }
                }
            }
        }
    }
}
