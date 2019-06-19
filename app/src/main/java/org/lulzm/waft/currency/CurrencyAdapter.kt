package org.lulzm.waft.currency

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import org.lulzm.waft.R

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
 * Time : 오후 9:40
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class CurrencyAdapter// Constructor
    (
    context: Context, private val resource: Int, private val flags: List<Int>,
    private val names: List<String>, private val symbols: List<String>,
    private val values: List<String>, private val longNames: List<Int>,
    private val selection: List<Int>
) : BaseAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }// Save all the parameters
    override fun getCount(): Int {
        return names.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    // Create a new View for each currency_item referenced by the adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val flag: ImageView?
        val name: TextView?
        val symbol: TextView?
        val value: TextView?
        val longName: TextView?
        // Create a new view
        if (convertView == null)
            convertView = inflater.inflate(resource, parent, false)
        // Find the views
        flag = convertView!!.findViewById(R.id.flag)
        name = convertView.findViewById(R.id.name)
        symbol = convertView.findViewById(R.id.symbol)
        value = convertView.findViewById(R.id.value)
        longName = convertView.findViewById(R.id.long_name)
        // Update the views
        flag?.setImageResource(flags[position])

        if (name != null)
            name.text = names[position]
        if (symbol != null)
            symbol.text = symbols[position]
        if (value != null)
            value.text = values[position]
        longName?.setText(longNames[position])
        // Highlight if selected
        if (selection.contains(position))
            convertView.setBackgroundResource(android.R.color.holo_blue_dark)
        else
            convertView.setBackgroundResource(0)// Clear highlight
        return convertView
    }
}
