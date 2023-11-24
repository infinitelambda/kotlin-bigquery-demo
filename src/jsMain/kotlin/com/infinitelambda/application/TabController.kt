package com.infinitelambda.application

import mdc.tabBar.MDCTabBar
import org.w3c.dom.Element

class TabController(container: Element?, private val tabContainers: List<Element>, private var selected: Int = 0) {

    private val mdcTabBar = MDCTabBar(container)

    init {
        mdcTabBar.listen("MDCTabBar:activated") {
            val newSelected = it.detail.index
            if (selected == newSelected) return@listen

            tabContainers[selected].classList.remove("content--active")
            tabContainers[newSelected].classList.add("content--active")
            selected = newSelected
        }
    }

}