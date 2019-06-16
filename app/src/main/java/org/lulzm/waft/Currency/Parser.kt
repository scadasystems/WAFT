package org.lulzm.waft.Currency

import android.content.Context
import android.util.Xml
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.net.URL
import java.util.*
import kotlin.Exception as Exception1

/*********************************************************
 * $$\                  $$\             $$\      $$\
 * $$ |                 $$ |            $$$\    $$$ |
 * $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |
 * $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ |
 * $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |
 * $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |
 * $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |
 * \_______| \______/   \__| \________| \__|     \__|
 *
 * Project : WAFT
 * Created by Android Studio
 * Developer : Lulz_M
 * Date : 2019-05-12 012
 * Time : 오후 9:51
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class Parser {
    private var map: MutableMap<String, Double>? = null
    // Get date
    var date: String? = null
        private set
    // Get map
    fun getMap(): Map<String, Double>? {
        return map
    }
    // Start parser for a url
    fun startParser(s: String): Boolean {
        // Create the map and add value for Euro
        map = HashMap()
        map!!["EUR"] = 1.0
        // Read the xml from the url
        try {
            val url = URL(s)
            val stream = url.openStream()
            val handler = Handler()
            Xml.parse(stream, Xml.Encoding.UTF_8, handler)
            return true
        } catch (e: Exception) {
            map!!.clear()
        }
        return false
    }
    // Start parser from a resource
    fun startParser(context: Context, id: Int): Boolean {
        // Create the map and add value for Euro
        map = HashMap()
        map!!["EUR"] = 1.0

        val resources = context.resources
        // Read the xml from the resources
        try {
            val stream = resources.openRawResource(id)
            val handler = Handler()
            Xml.parse(stream, Xml.Encoding.UTF_8, handler)
            return true
        } catch (e: Exception) {
            map!!.clear()
        }
        return false
    }
    // Handler class
    private inner class Handler : DefaultHandler() {
        // Start element
        override fun startElement(
            uri: String, localName: String, qName: String,
            attributes: Attributes
        ) {
            var name = "EUR"
            var rate: Double

            if (localName == "Cube") {
                for (i in 0 until attributes.length) {
                    // Get the date
                    when (attributes.getLocalName(i)) {
                        "time" -> date = attributes.getValue(i)
                        // Get the currency name
                        "currency" -> name = attributes.getValue(i)
                        // Get the currency rate
                        "rate" -> {
                            try {
                                rate = java.lang.Double.parseDouble(attributes.getValue(i))
                            } catch (e: Exception) {
                                rate = 1.0
                            }
                            // Add new currency to the map
                            map!![name] = rate
                        }
                    }
                }
            }
        }
    }
}
