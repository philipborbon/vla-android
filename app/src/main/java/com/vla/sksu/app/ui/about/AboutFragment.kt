package com.vla.sksu.app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vla.sksu.app.BuildConfig
import com.vla.sksu.app.R

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val textVersion: TextView = view.findViewById(R.id.version)
        textVersion.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)

        return view
    }
}