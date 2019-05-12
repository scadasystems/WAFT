package org.lulzm.waft.Currency;

import android.content.Context;
import android.content.res.Resources;
import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
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
 * Time : 오후 9:51                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class Parser {
    private Map<String, Double> map;
    private String date;

    // Get map
    public Map<String, Double> getMap() {
        return map;
    }

    // Get date
    public String getDate() {
        return date;
    }

    // Start parser for a url
    public boolean startParser(String s) {
        // Create the map and add value for Euro
        map = new HashMap<>();
        map.put("EUR", 1.0);

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

    // Start parser from a resource
    public boolean startParser(Context context, int id) {
        // Create the map and add value for Euro
        map = new HashMap<>();
        map.put("EUR", 1.0);

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
                            date = attributes.getValue(i);
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

                            // Add new currency to the map
                            map.put(name, rate);
                            break;
                    }
                }
            }
        }
    }
}
