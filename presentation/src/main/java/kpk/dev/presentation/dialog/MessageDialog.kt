package kpk.dev.presentation.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class MessageDialog : BaseDialog() {

    companion object {
        fun getInstance(args: Bundle): MessageDialog {
            val dialog = MessageDialog()
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

    override fun getDialogTag(): String = MessageDialog::class.java.simpleName
}