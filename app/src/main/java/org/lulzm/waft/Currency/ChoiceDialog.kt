package org.lulzm.waft.Currency

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import org.lulzm.waft.R
import java.util.*

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
 * Time : 오후 9:38
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class ChoiceDialog : Activity(), View.OnClickListener, AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {
    private var clear: Button? = null
    private var select: Button? = null

    private var selectList: MutableList<Int>? = null

    private var adapter: ChoiceAdapter? = null

    private var mode = Main.DISPLAY_MODE

    // On create
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.currency_choose)

        // Find views
        val listView = findViewById<ListView>(R.id.list)

        val cancel = findViewById<Button>(R.id.cancel)
        clear = findViewById(R.id.clear)
        select = findViewById(R.id.select)

        // Set the listeners
        if (listView != null) {
            listView.onItemClickListener = this
            listView.onItemLongClickListener = this
        }

        cancel?.setOnClickListener(this)

        if (clear != null)
            clear!!.setOnClickListener(this)

        if (select != null)
            select!!.setOnClickListener(this)

        selectList = ArrayList()

        // Populate the lists
        val flagList = Arrays.asList(*Main.CURRENCY_FLAGS)
        val longNameList = Arrays.asList(*Main.CURRENCY_LONGNAMES)
        val nameList = Arrays.asList(*Main.CURRENCY_NAMES)

        // Create the adapter
        adapter = ChoiceAdapter(
            this, R.layout.currency_choice, flagList,
            nameList, longNameList, selectList!!
        )

        // Set the adapter
        if (listView != null)
            listView.adapter = adapter
    }

    // On restore
    public override fun onRestoreInstanceState(savedState: Bundle) {
        val list = savedState.getIntegerArrayList(Main.SAVE_SELECT)

        if (list != null) {
            // Update the selection list
            selectList!!.addAll(list)
        }

        // Disable buttons if empty
        if (selectList!!.isEmpty()) {
            if (clear != null)
                clear!!.isEnabled = false
            if (select != null)
                select!!.isEnabled = false
            mode = Main.DISPLAY_MODE
        } else {
            if (clear != null)
                clear!!.isEnabled = true
            if (select != null)
                select!!.isEnabled = true
            mode = Main.SELECT_MODE
        }// Enable buttons if selection

        // Notify adapter
        adapter!!.notifyDataSetChanged()
        super.onRestoreInstanceState(savedState)
    }

    // On save
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the selection list
        outState.putIntegerArrayList(
            Main.SAVE_SELECT,
            selectList as ArrayList<Int>?
        )
    }

    // On click
    override fun onClick(v: View) {
        val id = v.id

        when (id) {
            // Cancel
            R.id.cancel -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            // Clear
            R.id.clear -> {
                if (clear != null)
                    clear!!.isEnabled = false
                if (select != null)
                    select!!.isEnabled = false
                mode = Main.DISPLAY_MODE

                // Start a new selection
                selectList!!.clear()
                adapter!!.notifyDataSetChanged()
            }

            // Select
            R.id.select -> {
                // Return new currency list in intent
                val intent = Intent()
                intent.putIntegerArrayListExtra(
                    Main.CHOICE,
                    selectList as ArrayList<Int>?
                )
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    // On currency_item click
    override fun onItemClick(
        parent: AdapterView<*>, view: View,
        position: Int, id: Long
    ) {
        // Check mode
        when (mode) {
            // Normal
            Main.DISPLAY_MODE -> {
                selectList!!.add(position)
                // Return new currency in intent
                val intent = Intent()
                intent.putIntegerArrayListExtra(
                    Main.CHOICE,
                    selectList as ArrayList<Int>?
                )
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            // Select
            Main.SELECT_MODE -> {
                if (selectList!!.contains(position))
                    selectList!!.removeAt(selectList!!.indexOf(position))
                else
                    selectList!!.add(position)

                if (selectList!!.isEmpty()) {
                    if (clear != null)
                        clear!!.isEnabled = false
                    if (select != null)
                        select!!.isEnabled = false
                    mode = Main.DISPLAY_MODE
                }

                adapter!!.notifyDataSetChanged()
            }
        }
    }

    // On currency_item long click
    override fun onItemLongClick(
        parent: AdapterView<*>, view: View,
        position: Int, id: Long
    ): Boolean {
        if (clear != null)
            clear!!.isEnabled = true
        if (select != null)
            select!!.isEnabled = true
        mode = Main.SELECT_MODE

        // Start a new selection
        selectList!!.clear()
        selectList!!.add(position)
        adapter!!.notifyDataSetChanged()
        return true
    }
}
