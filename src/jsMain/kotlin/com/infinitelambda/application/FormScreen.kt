@file:Suppress("FunctionName")

package com.infinitelambda.application

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.infinitelambda.application.data.Comment
import com.infinitelambda.application.data.FavouriteFood
import com.infinitelambda.application.data.FormResult
import com.infinitelambda.application.data.KotlinInterestLevel
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

private interface MultipleChoiceOption<T> {

    val name: String
    val value: T
}

private sealed class FoodOption(override val name: String, override val value: FavouriteFood) :
    MultipleChoiceOption<FavouriteFood> {

    data object Pizza : FoodOption(name = "Pizza", value = FavouriteFood.PIZZA)

    data object Burger : FoodOption(name = "Burger", value = FavouriteFood.BURGER)

}

private val foodOptions = listOf(FoodOption.Pizza, FoodOption.Burger)

private sealed class KotlinInterestOption(override val name: String, override val value: KotlinInterestLevel) :
    MultipleChoiceOption<KotlinInterestLevel> {

    data object High : KotlinInterestOption(name = "Very much", value = KotlinInterestLevel.HIGH)

    data object Medium : KotlinInterestOption(name = "Somewhat", value = KotlinInterestLevel.MEDIUM)

    data object Low : KotlinInterestOption(name = "Not at all", value = KotlinInterestLevel.LOW)

}

private val kotlinInterestOptions =
    listOf(KotlinInterestOption.High, KotlinInterestOption.Medium, KotlinInterestOption.Low)

private suspend fun submitForm(client: HttpClient, data: FormResult) {
    client.post("http://localhost:8080/formResults") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }
}

@Composable
fun FormScreen(client: HttpClient) {
    var selectedFoodOption: FoodOption? by remember { mutableStateOf(null) }
    var selectedKotlinInterestOption: KotlinInterestOption? by remember { mutableStateOf(null) }
    var comment by remember { mutableStateOf("") }

    val canSend = remember(selectedFoodOption, selectedKotlinInterestOption, comment) {
        selectedFoodOption != null && selectedKotlinInterestOption != null && comment.isNotBlank()
    }

    val composableScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        TitleCard()
        FoodCard(selectedFoodOption) { selectedFoodOption = it }
        KotlinInterestCard(selectedKotlinInterestOption) { selectedKotlinInterestOption = it }
        CommentCard(comment) { comment = it }
        FormActions(
            canSend = canSend,
            onSendClick = {
                composableScope.launch {
                    submitForm(
                        client, FormResult(
                            favouriteFood = selectedFoodOption!!.value,
                            kotlinInterestLevel = selectedKotlinInterestOption!!.value,
                            comment = Comment(comment)
                        )
                    )
                }
            },
            onClearClick = {
                selectedFoodOption = null
                selectedKotlinInterestOption = null
                comment = ""
            }
        )
    }
}

@Composable
private fun TitleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(fraction = 0.3f)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Feedback Form",
                style = MaterialTheme.typography.h4
            )
            Text(
                text = "Share your thought about this talk",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
private fun FoodCard(selectedOption: FoodOption?, onSelected: (FoodOption) -> Unit) {
    MultipleChoiceCard(
        title = "What is your favourite food?",
        options = foodOptions,
        selectedOption = selectedOption,
        onSelected = onSelected
    )
}

@Composable
private fun KotlinInterestCard(selectedOption: KotlinInterestOption?, onSelected: (KotlinInterestOption) -> Unit) {
    MultipleChoiceCard(
        title = "How interested are you in learning more about Kotlin?",
        options = kotlinInterestOptions,
        selectedOption = selectedOption,
        onSelected = onSelected
    )
}

@Composable
private fun <T, O : MultipleChoiceOption<T>> MultipleChoiceCard(
    title: String,
    options: List<O>,
    selectedOption: O?,
    onSelected: (O) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(fraction = 0.3f)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .selectableGroup()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1
            )

            options.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = it == selectedOption,
                            onClick = { onSelected(it) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (it == selectedOption),
                        onClick = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = it.name,
                        style = MaterialTheme.typography.body1.merge()
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentCard(value: String, onValueChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(fraction = 0.3f)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Can you share your thoughts on the talk?",
                style = MaterialTheme.typography.body1
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                placeholder = { Text(text = "Your comment") },
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
private fun FormActions(canSend: Boolean, onSendClick: () -> Unit, onClearClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(fraction = 0.3f)
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onSendClick,
            enabled = canSend
        ) {
            Text("Send")
        }
        TextButton(onClick = onClearClick) {
            Text("Clear form")
        }
    }
}