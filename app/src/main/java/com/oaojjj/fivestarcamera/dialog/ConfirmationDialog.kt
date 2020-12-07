package com.oaojjj.fivestarcamera.dialog

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.oaojjj.fivestarcamera.fragment.Camera2Fragment
import com.oaojjj.fivestarcamera.R

/**
 * Shows OK/Cancel confirmation dialog about camera permission.
 */
class ConfirmationDialog(var permission: String) : DialogFragment() {
    @RequiresApi(Build.VERSION_CODES.Q)
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val parent: Fragment? = parentFragment
        var permissionString = -1;
        when (permission) {
            Manifest.permission.CAMERA -> permissionString = R.string.request_camera_permission
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> permissionString =
                R.string.request_storage_write_permission
            Manifest.permission.READ_EXTERNAL_STORAGE -> permissionString =
                R.string.request_storage_read_permission
        }
        return AlertDialog.Builder(activity)
            .setMessage(permissionString)
            .setPositiveButton(
                R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    parent?.requestPermissions(
                        arrayOf(permission),
                        Camera2Fragment.REQUEST_PERMISSION
                    )
                })
            .setNegativeButton(
                R.string.cancle,
                DialogInterface.OnClickListener { dialog, which ->
                    val activity: FragmentActivity? = parent?.activity
                    activity?.finish()
                })
            .create()
    }
}