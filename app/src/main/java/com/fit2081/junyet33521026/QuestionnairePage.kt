package com.fit2081.junyet33521026

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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


/**
 * Main activity for the application.
 */
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
/**
 * Composable function for the UI of Food Intake Questionnaire screen.
 *
 * @param modifier Modifier to be applied.
 */
fun FoodIntakeQuestionnaireScreen(modifier: Modifier = Modifier) {
    // current context to start activity
    val context = LocalContext.current
    // load user accounts from CSV file
    val userID = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE).getString("userLoginID", "") ?: ""
    // load any saved responses
    val sharedPref = context.getSharedPreferences("${userID}Response", Context.MODE_PRIVATE)

    Log.d("Debug", "Current Logged-in User ID: $userID")
    Log.d("Debug", "Current SharedPreferences: ${sharedPref.all}")
    // Food selection
    val foodCategories = listOf("Fruits", "Red Meat", "Fish", "Vegetables", "Seafood", "Eggs", "Grains", "Poultry", "Nuts/Seeds")
    // load any saved foods
    val savedFoods = sharedPref.getStringSet("selectedFoods", setOf()) ?: setOf()
    // track current selected foods
    val selectedFoods = remember { mutableStateListOf<String>().apply { addAll(savedFoods) } }

    // Persona selection
    val personaCategories = mapOf(
        "Health Devotee" to Pair(R.drawable.health_devotee, "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        "Mindful Eater" to Pair(R.drawable.mindful_eater, "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        "Wellness Striver" to Pair(R.drawable.wellness_striver, "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go."),
        "Balance Seeker" to Pair(R.drawable.balance_seeker, "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        "Health Procrastinator" to Pair(R.drawable.health_procrastinator, "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        "Food Carefree" to Pair(R.drawable.food_carefree, "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.")
    )
    // load any saved persona
    val savedPersona = sharedPref.getString("selectedPersona", "") ?: ""
    // track current selected persona
    val selectedPersona = remember { mutableStateOf(savedPersona) }

    // load any saved times
    val savedMealTime = sharedPref.getString("mealTime", "00:00") ?: "00:00"
    val savedSleepTime = sharedPref.getString("sleepTime", "00:00") ?: "00:00"
    val savedWakeTime = sharedPref.getString("wakeTime", "00:00") ?: "00:00"
    // track current selected times
    val mealTime = remember { mutableStateOf(savedMealTime) }
    val sleepTime = remember { mutableStateOf(savedSleepTime) }
    val wakeTime = remember { mutableStateOf(savedWakeTime) }
    // Time selection
    val timeCategories = mapOf(
        "What time of day approx. do you normally eat your biggest meal?" to mealTime,
        "What time of day approx. do you go to sleep at night?" to sleepTime,
        "What time of day approx. do you wake up in the morning?" to wakeTime
    )

    Column(
        modifier = modifier.padding(14.dp)
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

        // Food selection
        Text(text = "Tick all the food categories you can eat", fontSize = 17.sp, fontWeight = FontWeight.Medium)
        FoodCheckboxes(foodCategories, selectedFoods)
        Spacer(modifier = Modifier.height(16.dp))

        // Persona description
        Text(text = "Your Persona", fontSize = 17.sp, fontWeight = FontWeight.Medium)
        PersonaModals(personaCategories)
        Spacer(modifier = Modifier.height(16.dp))

        // Persona selection
        Text(text = "Which persona best fits you?", fontSize = 17.sp, fontWeight = FontWeight.Medium)
        DropdownMenu(selectedPersona)
        Spacer(modifier = Modifier.height(16.dp))

        // Time selection
        Text(text = "Timings", fontSize = 17.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(10.dp))
        TimePickerFields(timeCategories)
        Spacer(modifier = Modifier.height(20.dp))

        // Save button
        Button(
            onClick = {
                // save responses to SharedPreferences
                saveResponse(
                    context,
                    userID,
                    selectedFoods,
                    selectedPersona.value,
                    mealTime.value,
                    sleepTime.value,
                    wakeTime.value
                )
                // navigate to HomePage
                context.startActivity(Intent(context, HomePage::class.java))
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row {
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
 * Composable function for food checkboxes.
 *
 * @param foodCategories List of food categories.
 * @param selectedFoods Current selected foods.
 */
@Composable
fun FoodCheckboxes(foodCategories: List<String>, selectedFoods: MutableList<String>) {
    Row {
        // split food categories into 3 columns
        val columns = foodCategories.chunked(3)
        columns.forEach { column ->
            Column { // create 3 columns
                column.forEach { category ->
                    // each food category
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = category in selectedFoods, // check if food selected
                            onCheckedChange = { isChecked ->
                                if (isChecked) { // checked food
                                    if (category !in selectedFoods) {
                                        selectedFoods.add(category)
                                    }
                                } else { // unchecked food
                                    selectedFoods.remove(category)
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
/**
 * Composable function for persona pop-up modals.
 *
 * @param personas Map of persona name -> image ID and description.
 */
fun PersonaModals(personas: Map<String, Pair<Int, String>>) {
    // track if a pop-up modal is shown
    var showModal by remember { mutableStateOf(false) }
    // track current selected pop-up modal
    var selectedModal by remember { mutableStateOf("") }

    Column {
        // split persona buttons into 2 columns
        personas.keys.chunked(2).forEach { column ->
            Row(horizontalArrangement = Arrangement.Start) {
                column.forEach { persona ->
                    Button(
                        onClick = {
                            // show pop-up modal
                            selectedModal = persona
                            showModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp), // less-rounded corners
                        modifier = Modifier.padding(end = 4.dp) // add space between buttons
                    ) {
                        Text(text = persona, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // show pop-up modal
    if (showModal) {
        val (imageID, description) = personas[selectedModal] ?: return
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = {
                // Persona title
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedModal,
                        textAlign = TextAlign.Center
                    )
                }
                    },
            text = {
                // Persona description
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = imageID),
                        contentDescription = selectedModal,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = description, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                // Dismiss button
                Button(
                    onClick = { showModal = false },
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
/**
 * Composable function for dropdown menu to select persona.
 *
 * @param selectedPersona Current selected persona.
 */
fun DropdownMenu(selectedPersona: MutableState<String>) {
    val personas = listOf("Health Devotee", "Mindful Eater", "Wellness Striver", "Balance Seeker", "Health Procrastinator", "Food Carefree")
    // check if show dropdown menu
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value, // show dropdown menu
        onExpandedChange = { expanded.value = !expanded.value } // toggle dropdown menu
    ) {
        OutlinedTextField(
            value = selectedPersona.value, // current selected persona
            onValueChange = { }, // do not allow user to type in
            label = { Text("Select Persona") },
            readOnly = true, // only can select from dropdown menu
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded.value) },
            modifier = Modifier.fillMaxWidth().menuAnchor() // show dropdown menu onto text field
        )
        ExposedDropdownMenu(
            expanded = expanded.value, // show dropdown menu
            onDismissRequest = { expanded.value = false } // hide dropdown menu
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
/**
 * Composable function for time picker fields.
 *
 * @param timeCategories Map of time category -> current time selected.
 */
fun TimePickerFields(timeCategories: Map<String, MutableState<String>>) {
    // current context to show time picker dialog
    val context = LocalContext.current

    Column {
        timeCategories.forEach { (category, time) ->
            // track if time picker dialog is shown
            var showTimePicker by remember { mutableStateOf(false) }

            Row {
                // Time category
                Text(
                    text = category,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.weight(0.7f)
                )
                // Time picker
                Button(
                    onClick = { showTimePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.weight(0.2f)
                ) {
                    Text(text = time.value, fontSize = 14.sp, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // show time picker pop-up
            if (showTimePicker) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                TimePickerDialog(
                    context,
                    { _, hour: Int, minute: Int ->
                        time.value = String.format("%02d:%02d", hour, minute)
                    }, hour, minute, false
                ).apply {
                    show()
                    setOnDismissListener { showTimePicker = false }
                }
            }
        }
    }
}


/**
 * Save user's responses to SharedPreferences.
 *
 * @param context Context to access SharedPreferences.
 * @param userID Current login user ID.
 * @param selectedFoods List of selected food categories.
 * @param selectedPersona Selected persona.
 * @param mealTime Selected meal time.
 * @param sleepTime Selected sleep time.
 * @param wakeTime Selected wake time.
 */
fun saveResponse(
    context: Context,
    userID: String,
    selectedFoods: List<String>,
    selectedPersona: String,
    mealTime: String,
    sleepTime: String,
    wakeTime: String
) {
    Log.d("Debug", "Current Logged-in User ID: $userID")

    // save all responses to SharedPreferences
    val sharedPref = context.getSharedPreferences("${userID}Response", Context.MODE_PRIVATE).edit()
    sharedPref.putBoolean("completedResponse", true)
    sharedPref.putStringSet("selectedFoods", selectedFoods.toSet())
    sharedPref.putString("selectedPersona", selectedPersona)
    sharedPref.putString("mealTime", mealTime)
    sharedPref.putString("sleepTime", sleepTime)
    sharedPref.putString("wakeTime", wakeTime)
    sharedPref.apply()

    Log.d("SharedPreferences", "Data saved to SharedPreferences: $selectedFoods, $selectedPersona, $mealTime, $sleepTime, $wakeTime")
}

