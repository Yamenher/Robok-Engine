package com.robok.ide.ui.fragments.settings.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.material.transition.MaterialSharedAxis

import com.robok.ide.R
import com.robok.ide.databinding.FragmentSettingsEditorBinding
import com.robok.ide.ui.base.RobokFragment
import com.robok.ide.ui.components.editor.RobokCodeEditor
import com.robok.ide.ui.components.editor.ThemeManager

import dev.trindadeaquiles.lib.ui.components.preferences.Preference

class SettingsEditorFragment(private val transitionAxis: Int = MaterialSharedAxis.X) : RobokFragment(transitionAxis) {

    private var _binding: FragmentSettingsEditorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbarNavigationBack(binding.toolbar)
        
        val codeEditor = RobokCodeEditor(requireContext())
        
        val editorTheme = Preference(requireContext()).apply {
            setTitle(getString(R.string.settings_editor_theme_title))
            setDescription(getString(R.string.settings_editor_theme_description))
            setPreferenceClickListener {
                ThemeManager.showSwitchThemeDialog(requireActivity(), codeEditor.getCodeEditor()) { which ->
                    ThemeManager.selectTheme(codeEditor.getCodeEditor(), which)
                }
            }
        }
        binding.content.addView(editorTheme)

        val savedThemeIndex = ThemeManager.loadTheme(requireContext())
        ThemeManager.selectTheme(codeEditor.getCodeEditor(), savedThemeIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}