package com.villetainio.familiarstrangers.ui

import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext

import com.villetainio.familiarstrangers.MainActivity
import org.jetbrains.anko.*

class MainActivityUI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
        verticalLayout {
            val name = editText()
            button("Say Hello"){
                onClick { ctx.toast("Hello, ${name.text}!") }
            }
        }
    }.view
}
