package org.gampiot.robok.ui.fragments.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.Toolbar

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.tabs.TabLayout

import org.gampiot.robok.R
import org.gampiot.robok.databinding.FragmentEditorBinding
import org.gampiot.robok.feature.util.base.RobokFragment
import org.gampiot.robok.feature.component.terminal.RobokTerminal
import org.gampiot.robok.feature.component.progress.DotProgressBar
import org.gampiot.robok.feature.res.Strings
import org.gampiot.robok.ui.fragments.build.output.OutputFragment
import org.gampiot.robok.ui.fragments.editor.logs.LogsFragment
import org.gampiot.robok.ui.fragments.editor.diagnostic.DiagnosticFragment

import robok.compiler.logic.*
import robok.diagnostic.logic.*

class EditorFragment(private val transitionAxis: Int = MaterialSharedAxis.X) : RobokFragment(transitionAxis) {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private var dotProgressBar = null
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentLayoutResId(R.id.fragment_container)
        val path = arguments?.getString(PROJECT_PATH) ?: "/sdcard/Robok/Projects/Default/"
        val terminal = RobokTerminal(requireContext())
        
        val compilerListener = object : LogicCompilerListener {
            override fun onCompiling(log: String) {
                terminal.addLog(log)
            }

            override fun onCompiled(output: String) {
                val outputFragment = OutputFragment(MaterialSharedAxis.X)
                outputFragment.addOutput(requireContext(), layoutInflater, view as ViewGroup, output)

                Snackbar.make(binding.root, Strings.message_compiled, Snackbar.LENGTH_LONG)
                    .setAction(Strings.go_to_outputs) {
                        openFragment(outputFragment)
                        terminal.dismiss()
                    }
                    .show()
            }
        }
        
        val diagnosticHandlerListener = object : DiagnosticListener {
            override fun onDiagnosticStatusReceive(isError: Boolean) {
                if (isError) { /* if diagnostic received */  } else { /* if no diagnostic received */ }
            }
            override fun onDiagnosticReceive(line: Int, positionStart: Int, postionEnd: Int, msg: String) {
                
            }
        }
        
        val compiler = LogicCompiler(requireContext(), compilerListener)

        binding.runButton.setOnClickListener {
            val code = binding.codeEditor.text.toString()
            terminal.show()
            compiler.compile(code)
        }

        binding.seeLogs.setOnClickListener {
            terminal.show()
        }

        configureTabLayout()
        configureToolbar()
        configureDrawer()
        configureEditor()
    }
    
    fun configureTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.text) {
                        getString(Strings.text_logs) -> {
                            openCustomFragment(R.id.drawer_editor_right_fragment_container, LogsFragment(MaterialSharedAxis.Y))
                        }

                        getString(Strings.text_diagnostic) -> {
                            openCustomFragment(R.id.drawer_editor_right_fragment_container, DiagnosticFragment(MaterialSharedAxis.Y))
                        }
                        else -> {}
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
        })
    }
    
    fun configureToolbar() {
        val dotProgressBar = DotProgressBar.Builder()
             .setMargin(1)
             .setAnimationDuration(2000)
             .setDotBackground(org.gampiot.robok.feature.component.R.drawable.ic_dot_24)
             .setMaxScale(1f)
             .setMinScale(0.3f)
             .setNumberOfDots(3)
             .setDotRadius(4)
             .build(requireContext())
        dotProgressBar.startAnimation()
        binding.toolbar.addView(dotProgressBar)
        
        binding.toolbar.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        
        /*
        dotProgressBar.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }
        */
    }
    
    fun configureDrawer() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
             private var leftDrawerOffset = 0f
             private var rightDrawerOffset = 0f
             
             override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                 val drawerWidth = drawerView.width
                 when (drawerView.id) {
                     R.id.navigation_view_left -> {
                         leftDrawerOffset = drawerWidth * slideOffset
                         binding.content.translationX = leftDrawerOffset
                     }
                     R.id.navigation_view_right -> {
                         rightDrawerOffset = drawerWidth * slideOffset
                         binding.content.translationX = -rightDrawerOffset
                     }
                 }
             }
             override fun onDrawerOpened(drawerView: View) {}
             override fun onDrawerClosed(drawerView: View) {
                 binding.content.translationX = 0f
                 leftDrawerOffset = 0f
                 rightDrawerOffset = 0f
             }
             override fun onDrawerStateChanged(newState: Int) { }
        })
    }
    
    fun configureEditor () {
        binding.codeEditor.configureSymbolView(binding.robokSymbolInput)
        binding.undo.setOnClickListener{
             binding.editor.undo()
        }
        binding.redo.setOnClickListener {
             binding.editor.redo()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PROJECT_PATH = "arg_path"

        @JvmStatic
        fun newInstance(path: String): EditorFragment {
            return EditorFragment(MaterialSharedAxis.X).apply {
                arguments = Bundle().apply {
                    putString(PROJECT_PATH, path)
                }
            }
        }
    }
}