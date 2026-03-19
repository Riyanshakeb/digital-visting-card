package com.azizgraphics.clcltr.ui.calculate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.azizgraphics.clcltr.R
import com.azizgraphics.clcltr.data.db.AppDatabase
import com.azizgraphics.clcltr.data.model.BannerItem
import com.azizgraphics.clcltr.data.model.Order
import com.azizgraphics.clcltr.data.repository.OrderRepository
import com.azizgraphics.clcltr.databinding.FragmentCalculateBinding
import com.azizgraphics.clcltr.util.CurrencyFormatter
import com.azizgraphics.clcltr.util.Prefs
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CalculateFragment : Fragment() {

    private var _binding: FragmentCalculateBinding? = null
    private val binding get() = _binding!!
    private val items = mutableListOf<BannerItem>()
    private val itemViews = mutableListOf<View>()
    private lateinit var prefs: Prefs
    private lateinit var repository: OrderRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        val db = AppDatabase.getDatabase(requireContext())
        repository = OrderRepository(db.orderDao())

        addItem()

        binding.btnAddItem.setOnClickListener { addItem() }
        binding.btnClearAll.setOnClickListener { clearAll() }
        binding.btnSaveOrder.setOnClickListener { saveOrder() }
    }

    private fun addItem() {
        val item = BannerItem(
            widthUnit = prefs.defaultUnit,
            heightUnit = prefs.defaultUnit,
            pricePerSqft = prefs.defaultPrice.toDouble()
        )
        items.add(item)

        val inflater = LayoutInflater.from(requireContext())
        val itemView = inflater.inflate(R.layout.item_banner, binding.itemsContainer, false)
        itemViews.add(itemView)
        binding.itemsContainer.addView(itemView)

        val index = items.size - 1
        setupItemView(itemView, item, index)
        updateSummary()
    }

    private fun setupItemView(view: View, item: BannerItem, index: Int) {
        val tvTitle = view.findViewById<TextView>(R.id.tvItemTitle)
        val tvTotal = view.findViewById<TextView>(R.id.tvItemTotal)
        val tvArea = view.findViewById<TextView>(R.id.tvItemArea)
        val etWidth = view.findViewById<EditText>(R.id.etWidth)
        val etHeight = view.findViewById<EditText>(R.id.etHeight)
        val etQty = view.findViewById<EditText>(R.id.etQuantity)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val spinnerWUnit = view.findViewById<Spinner>(R.id.spinnerWidthUnit)
        val spinnerHUnit = view.findViewById<Spinner>(R.id.spinnerHeightUnit)
        val btnDel = view.findViewById<ImageButton>(R.id.btnDelete)

        tvTitle.text = getString(R.string.item_number, index + 1)

        if (item.pricePerSqft > 0) {
            etPrice.setText(item.pricePerSqft.toString())
        }

        val units = arrayOf("Feet", "Inches")
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWUnit.adapter = unitAdapter
        spinnerHUnit.adapter = unitAdapter

        val wIdx = units.indexOf(item.widthUnit)
        if (wIdx >= 0) spinnerWUnit.setSelection(wIdx)
        val hIdx = units.indexOf(item.heightUnit)
        if (hIdx >= 0) spinnerHUnit.setSelection(hIdx)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                item.width = etWidth.text.toString().toDoubleOrNull() ?: 0.0
                item.height = etHeight.text.toString().toDoubleOrNull() ?: 0.0
                item.quantity = etQty.text.toString().toIntOrNull() ?: 1
                item.pricePerSqft = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                tvTotal.text = CurrencyFormatter.format(item.totalPrice, prefs.currency)
                tvArea.text = "Area: ${String.format("%.2f", item.totalArea)} sq.ft"
                updateSummary()
            }
        }

        etWidth.addTextChangedListener(watcher)
        etHeight.addTextChangedListener(watcher)
        etQty.addTextChangedListener(watcher)
        etPrice.addTextChangedListener(watcher)

        val unitListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                item.widthUnit = spinnerWUnit.selectedItem.toString()
                item.heightUnit = spinnerHUnit.selectedItem.toString()
                watcher.afterTextChanged(null)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerWUnit.onItemSelectedListener = unitListener
        spinnerHUnit.onItemSelectedListener = unitListener

        btnDel.setOnClickListener {
            val idx = itemViews.indexOf(view)
            if (idx >= 0 && items.size > 1) {
                items.removeAt(idx)
                itemViews.removeAt(idx)
                binding.itemsContainer.removeView(view)
                renumberItems()
                updateSummary()
            }
        }
    }

    private fun renumberItems() {
        itemViews.forEachIndexed { i, v ->
            v.findViewById<TextView>(R.id.tvItemTitle).text = getString(R.string.item_number, i + 1)
        }
    }

    private fun clearAll() {
        items.clear()
        itemViews.clear()
        binding.itemsContainer.removeAllViews()
        addItem()
    }

    private fun updateSummary() {
        val totalPrice = items.sumOf { it.totalPrice }
        val totalSqft = items.sumOf { it.totalArea }
        binding.tvTotalAmount.text = CurrencyFormatter.format(totalPrice, prefs.currency)
        binding.tvTotalSqft.text = "${String.format("%.2f", totalSqft)} sq.ft"
        binding.bannerPreview.setItems(items.toList())
    }

    private fun saveOrder() {
        val totalPrice = items.sumOf { it.totalPrice }
        val totalSqft = items.sumOf { it.totalArea }
        val totalQty = items.sumOf { it.quantity }

        if (totalPrice <= 0) {
            Toast.makeText(requireContext(), "Add items with values first", Toast.LENGTH_SHORT).show()
            return
        }

        val order = Order(
            totalAmount = totalPrice,
            totalSqft = totalSqft,
            totalQuantity = totalQty,
            currency = prefs.currency,
            itemsJson = Gson().toJson(items)
        )

        lifecycleScope.launch {
            repository.insert(order)
            Toast.makeText(requireContext(), "Order saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
