package com.ingloriousmind.android.propertyinvestortools

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.ingloriousmind.android.propertyinvestortools.model.AssetData
import com.ingloriousmind.android.propertyinvestortools.util.asFloat
import com.ingloriousmind.android.propertyinvestortools.util.show
import org.jetbrains.anko.*

class HomeActivity : AppCompatActivity() {

    enum class LineItem(val id: Int, val inputId: Int, @StringRes val labelResId: Int, val editable: Boolean) {
        ACQUISITION(View.generateViewId(), View.generateViewId(), R.string.label_acquisition_cost, true),
        PURCHASE(View.generateViewId(), View.generateViewId(), R.string.label_purchase_related_costs, true),
        RENT(View.generateViewId(), View.generateViewId(), R.string.label_annual_net_rent, true),
        RUNNING(View.generateViewId(), View.generateViewId(), R.string.label_annual_running_costs, true),
        EXT_ACQUISITION(View.generateViewId(), View.generateViewId(), R.string.label_extended_acquisition_cost, false),
        ADJ_RENT(View.generateViewId(), View.generateViewId(), R.string.label_adjusted_annual_net_rent, false),
        MULTIPLIER(View.generateViewId(), View.generateViewId(), R.string.label_multiplier, false),
        YIELD(View.generateViewId(), View.generateViewId(), R.string.label_yield, false),
    }

    val updateWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            calculateAndUpdate()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private var menu: Menu? = null

    private lateinit var itemPurchaseRelated: View
    private lateinit var itemRunningCosts: View
    private lateinit var itemAdjustedNetRent: View
    private lateinit var itemExtendedAcquisition: View

    private lateinit var inputAcquisitionCost: EditText
    private lateinit var inputPurchaseRelatedCosts: EditText
    private lateinit var inputAnnualNetRent: EditText
    private lateinit var inputRunningCosts: EditText

    private lateinit var resultExtendedAcquisitionCost: TextView
    private lateinit var resultAdjustedAnnualNetRent: TextView
    private lateinit var resultMultiplier: TextView
    private lateinit var resultYield: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        findViews()
    }

    private fun setupViews() {
        scrollView {
            verticalLayout {
                padding = dip(16)
                layoutTransition = LayoutTransition()
                for (item in LineItem.values()) {
                    verticalLayout {
                        id = item.id
                        orientation = LinearLayout.HORIZONTAL
                        textView(item.labelResId).lparams(width = dip(0)) {
                            weight = 2f
                        }
                        if (item.editable) {
                            editText() {
                                id = item.inputId
                                textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                                inputType = InputType.TYPE_CLASS_NUMBER
                            }.lparams(width = dip(0)) { weight = 1f }
                        } else {
                            textView() {
                                id = item.inputId
                                textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                                textSize = if (item == LineItem.YIELD) 18f else 14f
                                textColor = if (item == LineItem.YIELD) ContextCompat.getColor(this@HomeActivity, android.R.color.holo_green_dark) else Color.BLACK
                                gravity = Gravity.CENTER_VERTICAL
                            }.lparams(width = dip(0), height = matchParent) { weight = 1f }
                        }
                    }.lparams(width = matchParent, height = dip(42))
                }
            }
        }
    }

    private fun findViews() {
        itemPurchaseRelated = findViewById(LineItem.PURCHASE.id) as View
        itemRunningCosts = findViewById(LineItem.RUNNING.id) as View
        itemAdjustedNetRent = findViewById(LineItem.ADJ_RENT.id) as View
        itemExtendedAcquisition = findViewById(LineItem.EXT_ACQUISITION.id) as View

        inputAcquisitionCost = findViewById(LineItem.ACQUISITION.inputId) as EditText
        inputPurchaseRelatedCosts = findViewById(LineItem.PURCHASE.inputId) as EditText
        inputAnnualNetRent = findViewById(LineItem.RENT.inputId) as EditText
        inputRunningCosts = findViewById(LineItem.RUNNING.inputId) as EditText

        inputAcquisitionCost.addTextChangedListener(updateWatcher)
        inputPurchaseRelatedCosts.addTextChangedListener(updateWatcher)
        inputAnnualNetRent.addTextChangedListener(updateWatcher)
        inputRunningCosts.addTextChangedListener(updateWatcher)

        resultExtendedAcquisitionCost = findViewById(LineItem.EXT_ACQUISITION.inputId) as TextView
        resultAdjustedAnnualNetRent = findViewById(LineItem.ADJ_RENT.inputId) as TextView
        resultMultiplier = findViewById(LineItem.MULTIPLIER.inputId) as TextView
        resultYield = findViewById(LineItem.YIELD.inputId) as TextView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        this.menu = menu
        enableDetails(false)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_expand -> enableDetails(true)
            R.id.action_collapse -> enableDetails(false)
            R.id.action_about -> startActivity(intentFor<AboutActivity>())
        }
        return super.onOptionsItemSelected(item)
    }

    fun enableDetails(enable: Boolean) {
        inputPurchaseRelatedCosts.text.clear()
        inputRunningCosts.text.clear()
        calculateAndUpdate()
        menu?.findItem(R.id.action_collapse)?.isVisible = enable
        menu?.findItem(R.id.action_expand)?.isVisible = !enable
        itemPurchaseRelated.show(enable)
        itemRunningCosts.show(enable)
        itemAdjustedNetRent.show(enable)
        itemExtendedAcquisition.show(enable)
    }

    fun calculateAndUpdate() {
        updateUI(parseInputToModel())
    }

    fun updateUI(assetData: AssetData) {
        resultExtendedAcquisitionCost.text = String.format(getString(R.string.format_no_decimal_place), assetData.extendedAcquisitionCost())
        resultAdjustedAnnualNetRent.text = String.format(getString(R.string.format_no_decimal_place), assetData.adjustedAnnualNetRent())
        resultMultiplier.text = String.format(getString(R.string.format_multiplier), assetData.multiplier())
        resultYield.text = String.format(getString(R.string.format_yield), assetData.`yield`())
    }

    fun parseInputToModel(): AssetData {
        val acquisitionCost = inputAcquisitionCost.asFloat()
        val purchaseRelatedCosts = inputPurchaseRelatedCosts.asFloat()
        val annualNetRent = inputAnnualNetRent.asFloat()
        val runningCosts = inputRunningCosts.asFloat()

        return AssetData(acquisitionCost, purchaseRelatedCosts, annualNetRent, runningCosts)
    }

}
