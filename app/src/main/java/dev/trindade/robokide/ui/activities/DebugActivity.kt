package dev.trindade.robokide.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import dev.trindade.robokide.R

import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.io.InputStream

class DebugActivity : AppCompatActivity() {
    
    private var madeErrMsg: String = ""
    private lateinit var error: TextView
    
    private val exceptionType = arrayOf(
        "StringIndexOutOfBoundsException",
        "IndexOutOfBoundsException",
        "ArithmeticException",
        "NumberFormatException",
        "ActivityNotFoundException"
    )
    private val errMessage = arrayOf(
        "Invalid string operation\n",
        "Invalid list operation\n",
        "Invalid arithmetical operation\n",
        "Invalid toNumber block operation\n",
        "Invalid intent operation"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        
        error = findViewById(R.id.error)
        
        val intent = intent
        var errMsg = ""
        madeErrMsg = ""
        if (intent != null) {
            errMsg = intent.getStringExtra("error") ?: ""
            val spilt = errMsg.split("\n")
            // errMsg = spilt[0]
            try {
                for (j in exceptionType.indices) {
                    if (spilt[0].contains(exceptionType[j])) {
                        madeErrMsg = errMessage[j]
                        val addIndex = spilt[0].indexOf(exceptionType[j]) + exceptionType[j].length
                        madeErrMsg += spilt[0].substring(addIndex, spilt[0].length)
                        break
                    }
                }
                if (madeErrMsg.isEmpty()) madeErrMsg = errMsg
            } catch (e: Exception) {
            }
        }
        setErrorText()
        error.text = madeErrMsg
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.debug_error_title)
            .setMessage(madeErrMsg)
            .setNeutralButton("End Application") { _, _ ->
                finish()
            }
            .show()
    }
    
    private fun setErrorText() {
        error.text = madeErrMsg
    }
}

