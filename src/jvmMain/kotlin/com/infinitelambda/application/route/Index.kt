package com.infinitelambda.application.route

import com.infinitelambda.application.data.FavouriteFood
import com.infinitelambda.application.data.KotlinInterestLevel
import com.infinitelambda.application.html.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Routing.index() {
    get("/") {
        call.respondHtml(HttpStatusCode.OK, HTML::index)
    }
}

private fun HTML.index() {
    head {
        meta(name = "viewport", content = "width=device-width, initial-scale=1")
        title("Real-time data analytics with Kotlin and BigQuery")

        mdcCss()
        customCss()
        materialIcons()

        mdcJs()
        googleChartsLoaderJs()
        googleChartsModuleLoaderJs()
    }
    body("mdc-typography material-theme") {
        appBar("Kotlin and BigQuery Demo")
        mainContent {
            tabBar {
                tab("list", "Feedback Form", isActive = true)
                tab("pie_chart", "Dashboard")
            }
            tabContent {
                feedbackForm("content content--active", formId)
                feedbackDashboard("content", dashboardId)
            }
        }
    }
}

private fun HEAD.googleChartsModuleLoaderJs() {
    script {
        unsafe {
            raw(
                """
                    google.charts.load('current', {'packages':['corechart']});
                    google.charts.setOnLoadCallback(onLoaded);
                    
                    function onLoaded() {
                        console.log("loaded chart package");
                        
                        var scriptTag = document.createElement("script");
                        scriptTag.src = "/static/kotlin-bigquery-demo.js"
                        document.body.appendChild(scriptTag);
                    }
                """.trimIndent()
            )
        }
    }
}

private fun HEAD.googleChartsLoaderJs() {
    script {
        src = "https://www.gstatic.com/charts/loader.js"
    }
}

private fun HEAD.mdcJs() {
    script {
        src = "https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"
    }
}

private fun HEAD.customCss() {
    style {
        unsafe {
            raw(
                """
                        .material-theme {
                            --mdc-theme-primary: #24242e;
                            --mdc-theme-secondary: #30b593;
                            --mdc-theme-surface: #f5f5f5;
                            --mdc-theme-background: #ffffff;
                        }

                        .card {
                            padding: 16px;
                            margin-bottom: 16px;
                        }
                        
                        .content {
                            display: none;
                        }
                        
                        .content--active {
                            display: block;
                        }
                        
                        .chart {
                            width: 100%;
                            height: auto;
                            margin-left: auto;
                            margin-right: auto;
                        }
                    """
            )
        }
    }
}

private fun HEAD.materialIcons() {
    link {
        href = "https://fonts.googleapis.com/icon?family=Material+Icons"
        rel = "stylesheet"
    }
}

private fun HEAD.mdcCss() {
    link {
        href = "https://unpkg.com/material-components-web@latest/dist/material-components-web.min.css"
        rel = "stylesheet"
    }
}

private fun BODY.appBar(title: String) {
    header("mdc-top-app-bar") {
        div {
            classes = setOf("mdc-top-app-bar__row")

            section {
                classes = setOf("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-start")

                span {
                    classes = setOf("mdc-top-app-bar__title")

                    +title
                }
            }
        }
    }
}

private inline fun BODY.mainContent(crossinline block: MAIN.() -> Unit) {
    main("mdc-top-app-bar--fixed-adjust") {
        block()
    }
}

private inline fun MAIN.tabBar(crossinline block: DIV.() -> Unit) {
    div("mdc-tab-bar") {
        role = "tablist"

        div("mdc-tab-scroller") {
            div("mdc-tab-scroller__scroll-area") {
                div("mdc-tab-scroller__scroll-content") {
                    block()
                }
            }
        }
    }
}

private fun DIV.tab(icon: String, label: String, isActive: Boolean = false) {
    button {
        classes = setOf("mdc-tab", "mdc-tab--active")
        role = "tab"
        attributes["aria-selected"] = "true"
        tabIndex = "0"

        span("mdc-tab__content") {
            span("mdc-tab__icon material-icons") {
                +icon
            }
            span("mdc-tab__text-label") {
                +label
            }
        }
        span {
            classes = mutableSetOf("mdc-tab-indicator").apply {
                if (isActive) add("mdc-tab-indicator--active")
            }

            span("mdc-tab-indicator__content mdc-tab-indicator__content--underline") { }
        }
        span("mdc-tab__ripple") { }
    }
}

private inline fun MAIN.tabContent(crossinline block: DIV.() -> Unit) {
    div("mdc-layout-grid") {
        div("mdc-layout-grid__inner") {
            div("mdc-layout-grid__cell--span-4-desktop mdc-layout-grid__cell--span-2-tablet mdc-layout-grid__cell--span-1-phone") {}
            div("mdc-layout-grid__cell--span-4-desktop mdc-layout-grid__cell--span-8-tablet mdc-layout-grid__cell--span-10-phone") {
                block()
            }
            div("mdc-layout-grid__cell--span-4-desktop mdc-layout-grid__cell--span-2-tablet mdc-layout-grid__cell--span-1-phone") {}
        }
    }
}

private fun DIV.feedbackForm(classes: String? = null, id: String) {
    form(classes = classes) {
        this.id = id

        titleCard()
        multipleChoiceCard(
            title = "What is your favourite food?",
            name = "favouriteFood",
            options = arrayOf(
                "Pizza" to FavouriteFood.PIZZA.name,
                "Burger" to FavouriteFood.BURGER.name
            )
        )
        multipleChoiceCard(
            title = "How interested are you in learning more about Kotlin?",
            name = "kotlinInterestLevel",
            options = arrayOf(
                "Very much" to KotlinInterestLevel.HIGH.name,
                "Somewhat" to KotlinInterestLevel.MEDIUM.name,
                "Not at all" to KotlinInterestLevel.LOW.name
            )
        )
        commentCard()
        submitButton()
    }
}

private fun FORM.titleCard() = card {
    h4("mdc-typography--headline4") {
        +"Feedback Form"
    }
    span("mdc-typography--body1") {
        +"Share your thought about this talk"
    }
}


private fun FORM.multipleChoiceCard(title: String, name: String, vararg options: Pair<String, String>) = card {
    span("mdc-typography--body1") { +title }

    options.forEachIndexed { index, option ->
        radioButton(
            id = "$name$index",
            name = name,
            label = option.first,
            value = option.second
        )
    }
}

private fun FORM.commentCard() = card {
    span("mdc-typography--body1") { +"Can you share your thoughts on the talk?" }

    label("mdc-text-field mdc-text-field-filled mdc-text-field-textarea mdc-text-field--no-label") {
        span("mdc-text-field__ripple") { }
        textArea {
            classes = setOf("mdc-text-field__input")
            placeholder = "Your comment"
            rows = "8"
            cols = "40"
            attributes["aria-label"] = "Label"
            name = "comment"
            required = true
        }
        span("mdc-line-ripple") { }
    }
}

private fun FORM.submitButton() {
    button {
        classes = setOf("mdc-button", "mdc-button--raised")
        type = ButtonType.submit
        span("mdc-button__ripple") { }
        span("mdc-button__label") { +"Send" }
    }
}

private fun DIV.radioButton(id: String, name: String, value: String, label: String) {
    div("mdc-form-field") {
        div("mdc-radio mdc-radio--touch") {
            radioInput {
                this.classes = setOf("mdc-radio__native-control")
                this.id = id
                this.name = name
                this.value = value
                required = true
            }

            div("mdc-radio__background") {
                div("mdc-radio__outer-circle") { }
                div("mdc-radio__inner-circle") { }
            }

            div("mdc-radio__ripple") { }
            div("mdc-radio__focus-ring") { }
        }

        label("mdc-label") {
            htmlFor = id
            +label
        }
    }
}

private fun DIV.feedbackDashboard(classes: String? = null, id: String) {
    div(classes) {
        this.id = id

        pieChart(favouriteFoodChartId)
        pieChart(kotlinInterestLevelChartId)
        pieChart(sentimentChartId)
    }
}

private fun DIV.pieChart(chartId: String) {
    div("chart") {
        id = chartId
    }
}

private inline fun FlowContent.card(crossinline block: DIV.() -> Unit) =
    div("mdc-card card", block)
