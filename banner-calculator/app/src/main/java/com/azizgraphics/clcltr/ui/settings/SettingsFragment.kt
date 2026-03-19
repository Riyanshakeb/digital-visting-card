package com.azizgraphics.clcltr.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.azizgraphics.clcltr.R
import com.azizgraphics.clcltr.databinding.FragmentSettingsBinding
import com.azizgraphics.clcltr.util.Fmt
import com.azizgraphics.clcltr.util.Prefs

class SettingsFragment : Fragment() {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private lateinit var prefs: Prefs

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentSettingsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        prefs = Prefs(requireContext())
        setupUnit(); setupRate(); setupTheme(); setupCurrency(); setupMaterials()
    }

    private fun setupUnit() {
        val units = arrayOf("Feet", "Inches")
        val a = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spUnit.adapter = a
        b.spUnit.setSelection(units.indexOf(prefs.defaultUnit).coerceAtLeast(0))
        b.spUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) { prefs.defaultUnit = units[pos] }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupRate() {
        if (prefs.defaultRate > 0) b.etDefRate.setText(prefs.defaultRate.toString())
        b.etDefRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) { prefs.defaultRate = s.toString().toFloatOrNull() ?: 0f }
        })
    }

    private fun setupTheme() {
        val themes = arrayOf("System", "Light", "Dark")
        val a = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themes)
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spTheme.adapter = a
        b.spTheme.setSelection(themes.indexOf(prefs.themeMode).coerceAtLeast(0))
        b.spTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                val sel = themes[pos]
                if (sel != prefs.themeMode) {
                    prefs.themeMode = sel
                    AppCompatDelegate.setDefaultNightMode(when (sel) {
                        "Light" -> AppCompatDelegate.MODE_NIGHT_NO
                        "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    })
                }
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupCurrency() {
        val cur = Fmt.currencies().toTypedArray()
        val a = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cur)
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spCurrency.adapter = a
        b.spCurrency.setSelection(cur.indexOf(prefs.currency).coerceAtLeast(0))
        b.spCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) { prefs.currency = cur[pos] }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupMaterials() {
        refreshMaterials()
        b.btnAddMaterial.setOnClickListener {
            val et = EditText(requireContext()).apply { hint = "Material Name"; setPadding(48, 24, 48, 24) }
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_material)
                .setView(et)
                .setPositiveButton(R.string.save) { _, _ ->
                    val name = et.text.toString().trim()
                    if (name.isNotEmpty()) {
                        prefs.materials = prefs.materials + name
                        refreshMaterials()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun refreshMaterials() {
        b.materialsContainer.removeAllViews()
        prefs.materials.sorted().forEach { mat ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            val tv = TextView(requireContext()).apply {
                text = mat; textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val btn = ImageButton(requireContext()).apply {
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                background = null
                setOnClickListener {
                    prefs.materials = prefs.materials - mat
                    refreshMaterials()
                }
            }
            row.addView(tv); row.addView(btn)
            b.materialsContainer.addView(row)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
