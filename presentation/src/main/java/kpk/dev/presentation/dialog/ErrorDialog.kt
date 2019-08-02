package kpk.dev.presentation.dialog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog

class ErrorDialog : BaseDialog() {

    companion object {
        fun getInstance(args: Bundle): ErrorDialog {
            val dialog = ErrorDialog()
            dialog.arguments = args
            return dialog
        }
    }

    override fun setupDialog(dialogBuilder: AlertDialog.Builder) {
        dialogBuilder.apply {
            this.setPositiveButton("OK") { _, _ -> dismiss() }
        }
        dialogBuilder.setTitle(arguments?.getString(TITLE_ARG_KEY))
        dialogBuilder.setMessage(arguments?.getString(MESSAGE_ARG_KEY))
    }

    override fun getDialogTag(): String = ErrorDialog::class.java.simpleName
}