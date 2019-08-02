package kpk.dev.presentation.dialog

import android.view.View

interface Layoutable {

    fun getLayoutId(): Int

    fun initUI(view: View)
}