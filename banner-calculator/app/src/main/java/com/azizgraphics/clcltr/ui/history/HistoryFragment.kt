package com.azizgraphics.clcltr.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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
import com.azizgraphics.clcltr.util.CurrencyFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: OrderRepository
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = AppDatabase.getDatabase(requireContext())
        repository = OrderRepository(db.orderDao())

        adapter = OrderAdapter(
            onDelete = { order ->
                lifecycleScope.launch { repository.delete(order) }
            },
            onExport = { order -> exportOrder(order) }
        )

        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        repository.allOrders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNullOrEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.recyclerHistory.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.recyclerHistory.visibility = View.VISIBLE
                adapter.submitList(orders)
            }
        }
    }

    private fun exportOrder(order: Order) {
        val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val items: List<BannerItem> = try {
            Gson().fromJson(order.itemsJson, object : TypeToken<List<BannerItem>>() {}.type)
        } catch (e: Exception) { emptyList() }

        val sb = StringBuilder()
        sb.appendLine("Banner Calculator by AG")
        sb.appendLine("========================")
        sb.appendLine("Date: ${df.format(Date(order.date))}")
        sb.appendLine("Currency: ${order.currency}")
        sb.appendLine()
        items.forEachIndexed { i, item ->
            sb.appendLine("Item ${i+1}: ${item.widthInFeet}ft × ${item.heightInFeet}ft × ${item.quantity} qty")
            sb.appendLine("  Area: ${String.format("%.2f", item.totalArea)} sq.ft")
            sb.appendLine("  Price: ${CurrencyFormatter.format(item.totalPrice, order.currency)}")
        }
        sb.appendLine()
        sb.appendLine("Total: ${CurrencyFormatter.format(order.totalAmount, order.currency)}")
        sb.appendLine("Total Area: ${String.format("%.2f", order.totalSqft)} sq.ft")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            putExtra(Intent.EXTRA_SUBJECT, "Banner Order - ${df.format(Date(order.date))}")
        }
        startActivity(Intent.createChooser(intent, "Share Order"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class OrderAdapter(
    private val onDelete: (Order) -> Unit,
    private val onExport: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.VH>() {

    private var orders: List<Order> = emptyList()

    fun submitList(list: List<Order>) {
        orders = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDate = view.findViewById<TextView>(R.id.tvOrderDate)
        private val tvTotal = view.findViewById<TextView>(R.id.tvOrderTotal)
        private val tvItems = view.findViewById<TextView>(R.id.tvOrderItems)
        private val btnExport = view.findViewById<ImageButton>(R.id.btnExport)
        private val btnDelete = view.findViewById<ImageButton>(R.id.btnDeleteOrder)
        private val details = view.findViewById<LinearLayout>(R.id.detailsContainer)

        fun bind(order: Order) {
            val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            tvDate.text = df.format(Date(order.date))
            tvTotal.text = CurrencyFormatter.format(order.totalAmount, order.currency)
            tvItems.text = "${order.totalQuantity} items • ${String.format("%.2f", order.totalSqft)} sq.ft"

            btnDelete.setOnClickListener { onDelete(order) }
            btnExport.setOnClickListener { onExport(order) }

            var expanded = false
            itemView.setOnClickListener {
                expanded = !expanded
                if (expanded) {
                    details.visibility = View.VISIBLE
                    details.removeAllViews()
                    val items: List<BannerItem> = try {
                        Gson().fromJson(order.itemsJson, object : TypeToken<List<BannerItem>>() {}.type)
                    } catch (e: Exception) { emptyList() }

                    items.forEachIndexed { i, item ->
                        val tv = TextView(itemView.context).apply {
                            text = "Item ${i+1}: ${String.format("%.1f", item.widthInFeet)}×${String.format("%.1f", item.heightInFeet)}ft, qty=${item.quantity}, ${CurrencyFormatter.format(item.totalPrice, order.currency)}"
                            textSize = 13f
                            setPadding(0, 4, 0, 4)
                        }
                        details.addView(tv)
                    }
                } else {
                    details.visibility = View.GONE
                }
            }
        }
    }
}
