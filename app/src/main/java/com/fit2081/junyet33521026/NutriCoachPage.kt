package com.fit2081.junyet33521026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.junyet33521026.utils.AuthManager
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import com.fit2081.junyet33521026.data.Patient
import com.fit2081.junyet33521026.data.AIViewModel
import com.fit2081.junyet33521026.data.FruitResponse
import com.fit2081.junyet33521026.data.NutriCoachTip
import com.fit2081.junyet33521026.data.NutriCoachTipViewModel
import com.fit2081.junyet33521026.data.UIState
import com.fit2081.junyet33521026.network.FruitRepository

/**
 * NutriCoach activity for the application.
 */
class NutriCoachPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // PatientViewModel setup for handling patient data
        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NutriCoachPageScreen(Modifier.padding(innerPadding), patientViewModel)
                }
            }
        }
    }
}

/**
 * Composable function for the NutriCoach Page UI.
 *
 * @param modifier Modifier to apply to the root composable.
 * @param patientViewModel ViewModel for managing patient data.
 */
@Composable
fun NutriCoachPageScreen(modifier: Modifier = Modifier, patientViewModel: PatientViewModel) {
    // current context, pages and user login
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("NutriCoach") }
    val currentUserID = AuthManager.currentUserId ?: return
    val patientName = remember { mutableStateOf("") }

    // AIViewModel setup for handling AI interactions
    val aiViewModel: AIViewModel = viewModel(
        factory = AIViewModel.AIViewModelFactory(context)
    )
    // NutriCoachTipViewModel setup for handling NutriCoach tips database
    val tipViewModel: NutriCoachTipViewModel = viewModel(
        factory = NutriCoachTipViewModel.NutriCoachTipViewModelFactory(context)
    )


    // all required states management

    var patient by remember { mutableStateOf<Patient?>(null) }
    val fruitRepository = remember { FruitRepository() }

    var searchQuery by remember { mutableStateOf("") }
    var fruitResult by remember { mutableStateOf<FruitResponse?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    var randomImageUrl by remember { mutableStateOf("") }
    var showTipsDialog by remember { mutableStateOf(false) }
    var savedTips by remember { mutableStateOf<List<NutriCoachTip>>(emptyList()) }
    var isLoadingImage by remember { mutableStateOf(false) }

    var aiQuestion by remember { mutableStateOf("") }
    var isAskingAI by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val uiState by aiViewModel.uiState.collectAsState()

    // launched effect to load patient data (access to fruit logic check and saved tips)
    LaunchedEffect(currentUserID) {
        patient = patientViewModel.getPatient(currentUserID)

        // FRUIT CONDITION CHECK !!! -> Random image generated
        patient?.let { p ->
            if (calculateFruitScore(p.fruitSize, p.fruitVariationsScore) >= 5) {
                isLoadingImage = true
                randomImageUrl = fruitRepository.getRandomImageUrl()
                isLoadingImage = false
            }
            patientName.value = p.name ?: ""
        }
        // load saved tips
        savedTips = tipViewModel.getTipsForPatient(currentUserID)
    }

    // launched effect to save new motivational message as tip
    LaunchedEffect(uiState) {
        if (uiState is UIState.NutriCoachSuccess) {
            val message = (uiState as UIState.NutriCoachSuccess).message
            tipViewModel.saveTip(currentUserID, message)
            // Refresh saved tips
            savedTips = tipViewModel.getTipsForPatient(currentUserID)
        }
    }

    // launched effect to handle AI chat response
    LaunchedEffect(uiState) {
        if (uiState is UIState.AIChatSuccess) {
            aiResponse = (uiState as UIState.AIChatSuccess).message
            isAskingAI = false
        }
    }

    LazyColumn( // scrollable content by default
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // NutriCoach title
        item {
            Text(
                text = "NutriCoach",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // FRUIT CONDITION CHECK !!!
        patient?.let { p ->
            // Fruit score (fruitSize + fruitVariationsScore) < 5 ----> NOT OPTIMAL SCORE !!!
            // FOR MORE UNDERSTANDING, READ calculateFruitScore() DOCUMENTATION
            if (calculateFruitScore(p.fruitSize, p.fruitVariationsScore) < 5) {
                item {
                    // Fruit search selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Fruit search title
                            Column (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Text(
                                    text = "Fruit Search",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Fruit search input
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = {
                                        val randomFruits = listOf("apple", "banana", "orange", "strawberry", "mango", "kiwi")
                                        Text(randomFruits.random())
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                // Search button
                                Button(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            coroutineScope.launch { // search fruit by API call
                                                isSearching = true
                                                fruitResult = fruitRepository.getFruitByName(searchQuery.trim())
                                                isSearching = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(40.dp),
                                    enabled = !isSearching
                                ) {
                                    if (isSearching) { // visual feedback of circular loading indicator
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.Red,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.search_icon),
                                            contentDescription = "Search",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )

                                    }

                                }
                            }
                        }
                    }
                }

                // Fruit search result
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FruitResultCard(fruitResult)
                    }
                }
            }
            // Fruit score (fruitSize + fruitVariationsScore) >= 5 ----> OPTIMAL SCORE !!!
            // FOR MORE UNDERSTANDING, READ calculateFruitScore() DOCUMENTATION
            else {
                item {
                    // Random image generated
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Congrats message
                            Text(
                                text = "Well done! You scored well on fruit score! " +
                                        "\nHere is a random image for you!",
                                fontSize = 16.sp,
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (isLoadingImage) { // visual feedback of circular loading indicator
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.Red)
                                }
                            } else if (randomImageUrl.isNotEmpty()) { // randome image displayed
                                AsyncImage(
                                    model = randomImageUrl,
                                    contentDescription = "Random image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)), // border radius
                                    contentScale = ContentScale.Crop // crop image nicely
                                )
                            }

                            // Regenerate image button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isLoadingImage = true
                                        randomImageUrl = fruitRepository.getRandomImageUrl()
                                        isLoadingImage = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(text = "Generate Image", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Motivational message button
        item {
            Button(
                onClick = {
                    aiViewModel.generateMotivationalMessage(currentUserID)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.message_icon),
                        contentDescription = "Generate message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Motivational Message (AI)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Motivational message generated
        item {
            // visual feedback of circular loading indicator
            if (uiState is UIState.NutriCoachLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Red
                    )
                }
            } // motivational message generated
            else if (uiState is UIState.NutriCoachSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF59D)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = (uiState as UIState.NutriCoachSuccess).message,
                            fontSize = 14.sp
                        )
                    }
                }
            } // error message
            else if (uiState is UIState.NutriCoachError) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Error: ${(uiState as UIState.NutriCoachError).errorMessage}",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Show All Tips Button
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = { showTipsDialog = true }, // open pop up modal to show all tips
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8222)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bulb_icon),
                            contentDescription = "Shows all tips",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Shows All Tips",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        //     *** NEW FEATURE ***    //
        // NutriAI chat bot
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(5.dp))

                    // NutriAI logo
                    Column (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Image(
                            painter = painterResource(id = R.drawable.nutriai_logo),
                            contentDescription = "NutriAI Logo",
                            modifier = Modifier.size(350.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Greetings message
                    Column (modifier = Modifier.align(Alignment.CenterHorizontally) ){
                        Text(
                            text = "Welcome back ${patientName.value}!",
                            fontSize = 28.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 45.dp)
                        )
                        Text(
                            text = "Ask me anything about your nutrition!",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp)
                        )
                    }

                    // Search engine
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chat prompt input
                        OutlinedTextField(
                            value = aiQuestion,
                            onValueChange = { aiQuestion = it },
                            placeholder = {
                                val suggestions = listOf( // suggestions shown in placeholder
                                    "Explain my persona type?",
                                    "What is the ideal time to sleep and wake up?",
                                    "How should I improve my diet?",
                                    "What foods should I eat more of?",
                                    "How can I increase my HEIFA score?",
                                    "What does my nutrition data tell you about me?"
                                )
                                Text(suggestions.random())
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 3
                        )

                        // Submit button
                        Button(
                            onClick = {
                                isAskingAI = true
                                aiResponse = null // clear previous response
                                aiViewModel.generateNutritionResponse(currentUserID, aiQuestion.trim())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF690B0E)),
                            shape = RoundedCornerShape(40.dp),
                        ) {
                            if (isAskingAI) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.send_icon),
                                        contentDescription = "Ask AI",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // NutriAI response
        item {
            // visual feedback of circular loading indicator
            if (uiState is UIState.AIChatLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Red
                    )
                }
            } // NutriAI response to question
            else if (uiState is UIState.AIChatSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7)), // Light purple
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.message_icon),
                                contentDescription = "NutriAI Response",
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NutriAI Response",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7B1FA2)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = (uiState as UIState.AIChatSuccess).message,
                            fontSize = 14.sp,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )
                    }
                }
            }  // error message
            else if (uiState is UIState.AIChatError) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Error: ${(uiState as UIState.AIChatError).errorMessage}",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Navigation bottom bar
    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }

    // Show All Tips pop up modal
    if (showTipsDialog) {
        TipsDialog(
            tips = savedTips,
            onDismiss = { showTipsDialog = false },
            onClearAll = {
                coroutineScope.launch {
                    tipViewModel.deleteAllTipsForPatient(currentUserID)
                    savedTips = emptyList()
                    showTipsDialog = false
                }
            }
        )
    }
}

@Composable
/**
 * Composable function for fruit search result.
 *
 * @param fruit Fruit search result
 */
fun FruitResultCard(fruit: FruitResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // default value to be zero
            val family = fruit?.family ?: "-"
            val calories = fruit?.nutritions?.calories?.toString() ?: "0"
            val fat = fruit?.nutritions?.fat?.toString() ?: "0"
            val sugar = fruit?.nutritions?.sugar?.toString() ?: "0"
            val carbohydrates = fruit?.nutritions?.carbohydrates?.toString() ?: "0"
            val protein = fruit?.nutritions?.protein?.toString() ?: "0"

            // fruit info table
            TableRow("Family", family)
            HorizontalDivider(thickness = 2.dp, color = Color.White)
            TableRow("Calories", calories)
            HorizontalDivider(thickness = 2.dp, color = Color.White)
            TableRow("Fat", fat)
            HorizontalDivider(thickness = 2.dp, color = Color.White)
            TableRow("Sugar", sugar)
            HorizontalDivider(thickness = 2.dp, color = Color.White)
            TableRow("Carbohydrates", carbohydrates)
            HorizontalDivider(thickness = 2.dp, color = Color.White)
            TableRow("Protein", protein)
        }
    }
}

@Composable
/**
 * Composable function for fruit info table row creation
 *
 * @param label Fruit info title
 * @param value Fruit info value
 */
fun TableRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp), // vertical padding for spacing
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f).padding(start = 12.dp) // equal space for both columns
        )
        VerticalDivider(
            modifier = Modifier
                .height(25.dp)
                .padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp)) // space between label and value
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
/**
 * Composable function for the show all tips pop up modal.
 *
 */
fun TipsDialog(tips: List<NutriCoachTip>, onDismiss: () -> Unit, onClearAll: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box {
                // Close button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // top-right corner
                ) {
                    Text(text = "x", fontSize = 25.sp, color = Color.Black)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    // Show all tips title
                    Text(
                        text = "Past Tips",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // ***ADDITIONAL FEATURE*** //
                    // Clear all button
                    // For users to delete all when too many tips compact tgt
                    // Or when a tip have been listened and executed by user
                    Button(
                        onClick = onClearAll,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(width = 2.dp, color = Color.Red),
                        shape = RoundedCornerShape(17.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(text = "Clear All", color = Color.Red, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Each motivational tips
                    if (tips.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // No tips displayed
                            Text(
                                text = "No tips generated yet.\nTap 'Motivational Message (AI)' to generate your first tip!",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f, false),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Show all motivational tips
                            items(tips) { tip ->
                                TipCard(tip = tip)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
/**
 * Composable function for each motivational tips stored.
 */
fun TipCard(tip: NutriCoachTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Motivational message
            Text(
                text = tip.tipMessage,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // ***ADDITIONAL FEATURE*** //
            // Time that the tips has been received by user
            // For users to keep track of tips easily
            Text(
                text = "Advice from ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(tip.timestamp))}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/**
 * Fruit score calculation for fruit conditonal check.
 */
fun calculateFruitScore(fruitSize: Float, fruitVariationsScore: Float): Double {
    // Fruit size score calculation
    val finalFruitServeSize =
        if (fruitSize > 2) 2 else fruitSize // capped fruit servesize = 2 (according specification appendix)
    val finalFruitServeScore = finalFruitServeSize.toDouble() / 2 * 5 // convert to fruitscore (max score = 5)
    val finalFruitVariationsScore =
        if (fruitVariationsScore > 5) 5 else fruitVariationsScore // capped fruit variationscore = 5 (max score = 5) (according specification appendix)

    // total fruit score calculation (Assume max score = 10, optimal score = 5)
    return finalFruitServeScore.toDouble() + finalFruitVariationsScore.toDouble()
}
