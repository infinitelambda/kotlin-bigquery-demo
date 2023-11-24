@file:JsQualifier("mdc.tabBar")
package mdc.tabBar

import org.w3c.dom.Element

external class MDCTabBar(container: Element?) {

    fun listen(eventName: String, onEvent: (MDCTabActivatedEvent) -> Unit)

}

external interface MDCTabActivatedEvent {

    var detail: MDCTabActivatedEventDetails

}

external interface MDCTabActivatedEventDetails {

    var index: Int

}