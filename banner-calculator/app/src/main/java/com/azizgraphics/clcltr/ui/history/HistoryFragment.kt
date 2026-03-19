package com.azizgraphics.clcltr.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azizgraphics.clcltr.R
import com.azizgraphics.clcltr.data.db.AppDatabase
import com.azizgraphics.clcltr.data.model.BannerItem
import com.azizgraphics.clcltr.data.model.Order
import com.azizgraphics.clcltr.data.repository.OrderRepository
import com.azizgraphics.clcltr.databinding.FragmentHistoryBinding
import com.azizgraphics.clcltr.util.Fmt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!
    private lateinit var repo: OrderRepository

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHistoryBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        repo = OrderRepository(AppDatabase.get(requireContext()).orderDao())
        val adapter = OAdapter(
            onDel = { lifecycleScope.launch { repo.delete(it) } },
            onShare = { share(it) }
        )
        b.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        b.rvOrders.adapter = adapter
        repo.allOrders.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                b.emptyState.visibility = View.VISIBLE; b.rvOrders.visibility = View.GONE
            } else {
                b.emptyState.visibility = View.GONE; b.rvOrders.visibility = View.VISIBLE
                adapter.submit(list)
            }
        }
    }

    private fun share(o: Order) {
        val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val items: List<BannerItem> = try {
            Gson().fromJson(o.itemsJson, object : TypeToken<List<BannerItem>>() {}.type)
        } catch (_: Exception) { emptyList() }
        val sb = StringBuilder("Banner Calculator by AG\n========================\n")
        sb.appendLine("Date: ${df.format(Date(o.date))}")
        sb.appendLine("Currency: ${o.currency}\n")
        items.forEachIndexed { i, it ->
            sb.appendLine("Item ${i + 1}: ${it.dimensionLabel()} × ${it.quantity} qty")
            if (it.material.isNotEmpty()) sb.appendLine("  Material: ${it.material}")
            sb.appendLine("  Area: ${String.format("%.2f", it.totalArea)} sq.ft")
            sb.appendLine("  Price: ${Fmt.price(it.totalPrice, o.currency)}")
        }
        sb.appendLine("\nTotal: ${Fmt.price(o.totalAmount, o.currency)}")
        sb.appendLine("Total Area: ${String.format("%.2f", o.totalSqft)} sq.ft")
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }, "Share Order"))
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class OAdapter(
    private val onDel: (Order) -> Unit,
    private val onShare: (Order) -> Unit
) : RecyclerView.Adapter<OAdapter.VH>() {

    private var data: List<Order> = emptyList()
    fun submit(l: List<Order>) { data = l; notifyDataSetChanged() }
    override fun getItemCount() = data.size
    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_order_card, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(data[pos])

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvDate = v.findViewById<TextView>(R.id.tvOrderDate)
        private val tvAmt = v.findViewById<TextView>(R.id.tvOrderAmount)
        private val tvInfo = v.findViewById<TextView>(R.id.tvOrderInfo)
        private val btnS = v.findViewById<ImageButton>(R.id.btnShare)
        private val btnD = v.findViewById<ImageButton>(R.id.btnDelOrder)
        private val details = v.findViewById<LinearLayout>(R.id.expandedDetails)

        fun bind(o: Order) {
            val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            tvDate.text = df.format(Date(o.date))
            tvAmt.text = Fmt.price(o.totalAmount, o.currency)
            tvInfo.text = "${o.totalQty} items • ${String.format("%.2f", o.totalSqft)} sq.ft"
            btnD.setOnClickListener { onDel(o) }
            btnS.setOnClickListener { onShare(o) }
            var expanded = false
            itemView.setOnClickListener {
                expanded = !expanded
                if (expanded) {
                    details.visibility = View.VISIBLE; details.removeAllViews()
                    val items: List<BannerItem> = try {
                        Gson().fromJson(o.itemsJson, object : TypeToken<List<BannerItem>>() {}.type)
                    } catch (_: Exception) { emptyList() }
                    items.forEachIndexed { i, it ->
                        val tv = TextView(itemView.context).apply {
                            text = "  #${i + 1}: ${it.dimensionLabel()} × ${it.quantity}  =  ${Fmt.price(it.totalPrice, o.currency)}"
                            textSize = 13f; setPadding(0, 4, 0, 4)
                        }
                        details.addView(tv)
                    }
                } else details.visibility = View.GONE
            }
        }
    }
}
