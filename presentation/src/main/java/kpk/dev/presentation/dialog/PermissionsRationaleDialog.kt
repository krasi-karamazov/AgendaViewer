package kpk.dev.presentation.dialog

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kpk.dev.presentation.R

class PermissionsRationaleDialog: BaseDialog() {

    companion object {
        fun getInstance(args: Bundle, listener: DialogListener): PermissionsRationaleDialog {
            val dialog = PermissionsRationaleDialog()
            dialog.arguments = args
            dialog.dialogListener = listener
            return dialog
        }
    }

    private var dialogListener: DialogListener? = null

    private val dialogButtonsClickHandler: DialogInterface.OnClickListener = object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> dialogListener?.okClicked(this@PermissionsRationaleDialog)
                DialogInterface.BUTTON_NEGATIVE -> dialogListener?.cancelClicked()
            }
        }
    }

    override fun setupDialog(dialogBuilder: AlertDialog.Builder) {
        dialogBuilder.apply {
            this.setPositiveButton(getString(R.string.ok), dialogButtonsClickHandler)
            this.setNegativeButton(getString(R.string.cancel), dialogButtonsClickHandler)
        }
        dialogBuilder.setTitle(arguments?.getString(TITLE_ARG_KEY))
        dialogBuilder.setMessage(arguments?.getString(MESSAGE_ARG_KEY))
    }

    override fun getDialogTag(): String = PermissionsRationaleDialog::class.java.simpleName

    interface DialogListener{
        fun okClicked(dialog: BaseDialog)
        fun cancelClicked()
    }
}