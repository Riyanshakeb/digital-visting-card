package com.azizgraphics.clcltr.ui.calculate

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
import com.azizgraphics.clcltr.util.Fmt
import com.azizgraphics.clcltr.util.Prefs
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CalculateFragment : Fragment() {

    private var _b: FragmentCalculateBinding? = null
    private val b get() = _b!!
    private val items = mutableListOf<BannerItem>()
    private val views = mutableListOf<View>()
    private lateinit var prefs: Prefs
    private lateinit var repo: OrderRepository

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentCalculateBinding.inflate(inflater, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        repo = OrderRepository(AppDatabase.get(requireContext()).orderDao())
        addItem()
        b.btnAddItem.setOnClickListener { addItem() }
        b.btnClearAll.setOnClickListener { clearAll() }
        b.btnSaveOrder.setOnClickListener { saveOrder() }
    }

    private fun addItem() {
        val item = BannerItem(
            widthUnit = prefs.defaultUnit, heightUnit = prefs.defaultUnit,
            ratePerSqft = prefs.defaultRate.toDouble()
        )
        items.add(item)
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.item_banner_card, b.itemsContainer, false)
        views.add(v)
        b.itemsContainer.addView(v)
        wireItem(v, item, items.size - 1)
        recalc()
    }

    private fun wireItem(v: View, item: BannerItem, idx: Int) {
        val tvTitle = v.findViewById<TextView>(R.id.tvItemTitle)
        val tvPrice = v.findViewById<TextView>(R.id.tvItemPrice)
        val tvArea = v.findViewById<TextView>(R.id.tvItemArea)
        val etW = v.findViewById<EditText>(R.id.etWidth)
        val etH = v.findViewById<EditText>(R.id.etHeight)
        val etQ = v.findViewById<EditText>(R.id.etQty)
        val etR = v.findViewById<EditText>(R.id.etRate)
        val spWU = v.findViewById<Spinner>(R.id.spinnerWidthUnit)
        val spHU = v.findViewById<Spinner>(R.id.spinnerHeightUnit)
        val spMat = v.findViewById<Spinner>(R.id.spinnerMaterial)
        val btnDel = v.findViewById<ImageButton>(R.id.btnDeleteItem)

        tvTitle.text = getString(R.string.item_num, idx + 1)
        if (item.ratePerSqft > 0) etR.setText(item.ratePerSqft.toString())

        val units = arrayOf("Feet", "Inches")
        val uAdapt = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        uAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spWU.adapter = uAdapt; spHU.adapter = uAdapt
        spWU.setSelection(units.indexOf(item.widthUnit).coerceAtLeast(0))
        spHU.setSelection(units.indexOf(item.heightUnit).coerceAtLeast(0))

        val mats = listOf("-- Material --") + prefs.materials.sorted()
        val mAdapt = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mats)
        mAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMat.adapter = mAdapt

        val tw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                item.width = etW.text.toString().toDoubleOrNull() ?: 0.0
                item.height = etH.text.toString().toDoubleOrNull() ?: 0.0
                item.quantity = etQ.text.toString().toIntOrNull() ?: 1
                item.ratePerSqft = etR.text.toString().toDoubleOrNull() ?: 0.0
                tvPrice.text = Fmt.price(item.totalPrice, prefs.currency)
                tvArea.text = "Area: ${String.format("%.2f", item.totalArea)} sq.ft"
                recalc()
            }
        }
        etW.addTextChangedListener(tw); etH.addTextChangedListener(tw)
        etQ.addTextChangedListener(tw); etR.addTextChangedListener(tw)

        val unitL = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, vv: View?, pos: Int, id: Long) {
                item.widthUnit = spWU.selectedItem.toString()
                item.heightUnit = spHU.selectedItem.toString()
                tw.afterTextChanged(null)
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        spWU.onItemSelectedListener = unitL; spHU.onItemSelectedListener = unitL

        spMat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, vv: View?, pos: Int, id: Long) {
                item.material = if (pos == 0) "" else mats[pos]
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        btnDel.setOnClickListener {
            val i = views.indexOf(v)
            if (i >= 0 && items.size > 1) {
                items.removeAt(i); views.removeAt(i); b.itemsContainer.removeView(v)
                views.forEachIndexed { j, vv ->
                    vv.findViewById<TextView>(R.id.tvItemTitle).text = getString(R.string.item_num, j + 1)
                }
                recalc()
            }
        }
    }

    private fun clearAll() {
        items.clear(); views.clear(); b.itemsContainer.removeAllViews()
        addItem()
        Toast.makeText(requireContext(), R.string.all_cleared, Toast.LENGTH_SHORT).show()
    }

    private fun recalc() {
        val totalP = items.sumOf { it.totalPrice }
        val totalA = items.sumOf { it.totalArea }
        val totalQ = items.sumOf { it.quantity }
        b.tvTotalPrice.text = Fmt.price(totalP, prefs.currency)
        b.tvTotalArea.text = "${String.format("%.2f", totalA)} sq.ft"
        b.tvTotalItems.text = totalQ.toString()
        b.bannerPreview.updateItems(items.toList())
    }

    private fun saveOrder() {
        val total = items.sumOf { it.totalPrice }
        if (total <= 0) {
            Toast.makeText(requireContext(), R.string.add_values_first, Toast.LENGTH_SHORT).show()
            return
        }
        val order = Order(
            totalAmount = total, totalSqft = items.sumOf { it.totalArea },
            totalQty = items.sumOf { it.quantity }, currency = prefs.currency,
            itemsJson = Gson().toJson(items)
        )
        lifecycleScope.launch {
            repo.insert(order)
            Toast.makeText(requireContext(), R.string.order_saved, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
