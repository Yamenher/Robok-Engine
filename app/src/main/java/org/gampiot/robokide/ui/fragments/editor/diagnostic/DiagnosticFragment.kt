package org.gampiot.robokide.ui.fragments.editor.diagnostic

import android.os.Bundle
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.material.transition.MaterialSharedAxis

import org.gampiot.robokide.R
import org.gampiot.robokide.databinding.FragmentDiagnosticBinding
import org.gampiot.robokide.feature.component.log.Log
import org.gampiot.robokide.feature.util.base.RobokFragment

class DiagnosticFragment (private val tansitionAxis : Int = MaterialSharedAxis.X) : RobokFragment(tansitionAxis) {

    private var _binding: FragmentDiagnosticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentLayoutResId(R.id.fragment_container)
    }
    
    fun addDiagnostic(context: Context, inflater: LayoutInflater, container: ViewGroup?, log: String) {
        _binding = FragmentDiagnosticBinding.inflate(inflater, container, false)
        setFragmentLayoutResId(R.id.fragment_container)
        val logView = Log(context, log)
        binding.content.addView(logView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}