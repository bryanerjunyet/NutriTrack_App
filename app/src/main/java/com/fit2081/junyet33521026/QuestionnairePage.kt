package com.fit2081.junyet33521026

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import java.util.Calendar

class QuestionnairePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FoodIntakeQuestionnaireScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FoodIntakeQuestionnaireScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Food category checkboxes
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs", "Nuts/Seeds")
    val selectedFoods = remember { mutableStateListOf<String>() }

    // Persona selection
    val personas = mapOf(
        "Health Devotee" to Pair(R.drawable.health_devotee, "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        "Mindful Eater" to Pair(R.drawable.mindful_eater, "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        "Wellness Striver" to Pair(R.drawable.wellness_striver, "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go."),
        "Balance Seeker" to Pair(R.drawable.balance_seeker, "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        "Health Procrastinator" to Pair(R.drawable.health_procrastinator, "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        "Food Carefree" to Pair(R.drawable.food_carefree, "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.")
    )
    val selectedPersona = remember { mutableStateOf("") }

    // Time pickers
//    val biggestMealTime = remember { mutableStateOf("") }
//    val sleepTime = remember { mutableStateOf("") }
//    val wakeTime = remember { mutableStateOf("") }
    val biggestMealTime = remember { mutableStateOf("00:00") }
    val sleepTime = remember { mutableStateOf("00:00") }
    val wakeTime = remember { mutableStateOf("00:00") }

    Column(
        modifier = modifier.padding(14.dp),
    ) {
        // Questionnaire title
        Text(
            text = "Food Intake Questionnaire",
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tick all the food categories you can eat",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        FoodCheckboxes(foodCategories)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Persona",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )

        PersonaModal(personas)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Which persona best fits you?",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        DropdownMenu(selectedPersona)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Timings",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(10.dp))
        TimePickerField(
            label = "What time of day approx. do you normally eat your biggest meal?",
            timeState = biggestMealTime
        )
        TimePickerField(
            label = "What time of day approx. do you go to sleep at night?",
            timeState = sleepTime
        )
        TimePickerField(
            label = "What time of day approx. do you wake up in the morning?",
            timeState = wakeTime
        )
//        TimePickerField("What time of day approx. do you normally eat your biggest meal?", biggestMealTime, context)
//        TimePickerField("What time of day approx. do you go to sleep at night?", sleepTime, context)
//        TimePickerField("What time of day approx. do you wake up in the morning?", wakeTime, context)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                saveDataToSharedPreferences(
                    context,
                    selectedFoods,
                    selectedPersona.value,
                    biggestMealTime.value,
                    sleepTime.value,
                    wakeTime.value
                )
                context.startActivity(Intent(context, HomePage::class.java))
            },
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Red),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.save_icon),
                    contentDescription = "Save",
                    modifier = Modifier.size(23.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Save", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Checkbox for food categories using manual Rows.
 */
@Composable
fun FoodCheckboxes(foodCategories: List<String>) {
    val selectedCategories = remember { mutableStateOf(listOf<String>()) }

    Row {
        val columns = foodCategories.chunked(3)
        columns.forEach { column ->
            Column {
                column.forEach { category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = category in selectedCategories.value,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedCategories.value += category
                                } else {
                                    selectedCategories.value = selectedCategories.value.filter { it != category }
                                }
                            }
                        )
                        Text(
                            text = category,
                            fontSize = 15.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonaModal(personas: Map<String, Pair<Int, String>>) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDialog by remember { mutableStateOf("") }

    Column {
        personas.keys.chunked(2).forEach { column ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                column.forEach { persona ->
                    Button(
                        onClick = {
                            selectedDialog = persona
                            showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.DarkGray),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(text = persona, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    if (showDialog) {
        val (imageID, description) = personas[selectedDialog] ?: return
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedDialog,
                        textAlign = TextAlign.Center
                    )
                }
                    },
            text = {
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = imageID),
                        contentDescription = selectedDialog,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = description, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                // if the user clicks on the dismiss button,
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Dismiss", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(selectedPersona: MutableState<String>) {
    val expanded = remember { mutableStateOf(false) }
    val personas = listOf("Health Devotee", "Mindful Eater", "Wellness Striver", "Balance Seeker", "Health Procrastinator", "Food Carefree")

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value }
    ) {
        OutlinedTextField(
            value = selectedPersona.value,
            onValueChange = { },
            label = { Text("Select Persona") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded.value) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            personas.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona) },
                    onClick = {
                        selectedPersona.value = persona
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerField(label: String, timeState: MutableState<String>) {
    val mContext = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.weight(0.7f)
        )

        Button(
            onClick = { showTimePicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            modifier = Modifier.weight(0.2f)
        ) {
            Text(text = timeState.value, fontSize = 14.sp, color = Color.Black)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    if (showTimePicker) {
        val mCalendar = Calendar.getInstance()
        val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = mCalendar.get(Calendar.MINUTE)

        TimePickerDialog(
            mContext,
            { _, mHour: Int, mMinute: Int ->
                timeState.value = "$mHour:$mMinute"
            }, mHour, mMinute, false
        ).apply {
            show()
            setOnDismissListener { showTimePicker = false }
        }
    }
}

fun saveDataToSharedPreferences(
    context: Context,
    selectedFoods: List<String>,
    selectedPersona: String,
    biggestMealTime: String,
    sleepTime: String,
    wakeTime: String
) {
    val sharedPref = context.getSharedPreferences("FoodIntakePreferences", Context.MODE_PRIVATE).edit()
    sharedPref.putStringSet("selectedFoods", selectedFoods.toSet())
    sharedPref.putString("selectedPersona", selectedPersona)
    sharedPref.putString("biggestMealTime", biggestMealTime)
    sharedPref.putString("sleepTime", sleepTime)
    sharedPref.putString("wakeTime", wakeTime)
    sharedPref.apply()
    Log.d("SharedPreferences", "Data saved to SharedPreferences: $selectedFoods, $selectedPersona, $biggestMealTime, $sleepTime, $wakeTime")
    selectedFoods.forEach { food ->
        Log.d("SelectedFood", "Selected food: $food")
    }
    println("Print done")
}

