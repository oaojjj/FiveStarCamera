package com.oaojjj.fivestarcamera.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.oaojjj.fivestarcamera.R

/**
 * Shows an error message dialog.
 */
class ErrorDialog : DialogFragment() {
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity: FragmentActivity? = activity
        return AlertDialog.Builder(activity)
            .setMessage(arguments?.getString(ARG_MESSAGE))
            .setPositiveButton(
                R.string.ok
            ) { _, _ -> activity?.finish() }
            .create()
    }

    companion object {
        private const val ARG_MESSAGE = "message"
        fun newInstance(message: String?): ErrorDialog {
            val dialog = ErrorDialog()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }
}