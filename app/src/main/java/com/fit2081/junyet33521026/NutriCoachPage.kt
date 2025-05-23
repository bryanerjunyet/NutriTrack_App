package com.fit2081.junyet33521026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import com.fit2081.junyet33521026.data.Patient
import com.fit2081.junyet33521026.data.AIViewModel
import com.fit2081.junyet33521026.data.FruitResponse
import com.fit2081.junyet33521026.data.NutriCoachTip
import com.fit2081.junyet33521026.data.NutriCoachTipViewModel
import com.fit2081.junyet33521026.data.UIState
import com.fit2081.junyet33521026.network.FruitRepository

/**
 * Main activity for the NutriCoach application.
 */
class NutriCoachPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NutriCoachPageScreen(
                        Modifier.padding(innerPadding),
                        patientViewModel
                    )
                }
            }
        }
    }
}

/**
 * Composable function for the NutriCoach Page UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachPageScreen(
    modifier: Modifier = Modifier,
    patientViewModel: PatientViewModel
) {
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("NutriCoach") }
    val currentUserID = AuthManager.currentUserId ?: return

    // ViewModels
    val aiViewModel: AIViewModel = viewModel(
        factory = AIViewModel.AIViewModelFactory(context)
    )
    val tipViewModel: NutriCoachTipViewModel = viewModel(
        factory = NutriCoachTipViewModel.NutriCoachTipViewModelFactory(context)
    )

    // Centralized Repository
    val fruitRepository = remember { FruitRepository() }

    // States
    var searchQuery by remember { mutableStateOf("") }
    var fruitResult by remember { mutableStateOf<FruitResponse?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var patient by remember { mutableStateOf<Patient?>(null) }
    var randomImageUrl by remember { mutableStateOf("") }
    var showTipsDialog by remember { mutableStateOf(false) }
    var savedTips by remember { mutableStateOf<List<NutriCoachTip>>(emptyList()) }
    var isLoadingImage by remember { mutableStateOf(false) }
    // AI Bot states
    var aiQuestion by remember { mutableStateOf("") }
    var isAskingAI by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val uiState by aiViewModel.uiState.collectAsState()

    // Load patient data on startup
    LaunchedEffect(currentUserID) {
        patient = patientViewModel.getPatient(currentUserID)

        // Load random fruit image if fruit scores are optimal
        patient?.let { p ->
            if (calculateFruitScore(p.fruitSizeScore, p.fruitVariationsScore) >= 5) {
                isLoadingImage = true
                randomImageUrl = fruitRepository.getRandomFruitImageUrl()
                isLoadingImage = false
            }
        }

        // Load saved tips
        savedTips = tipViewModel.getTipsForPatient(currentUserID)
    }

    // Save generated tip when AI generates new message
    LaunchedEffect(uiState) {
        if (uiState is UIState.NutriCoachSuccess) {
            val message = (uiState as UIState.NutriCoachSuccess).message
            tipViewModel.saveTip(currentUserID, message)
            // Refresh saved tips
            savedTips = tipViewModel.getTipsForPatient(currentUserID)
        }
    }
    // Handle AI chat response
    LaunchedEffect(uiState) {
        if (uiState is UIState.AIChatSuccess) {
            aiResponse = (uiState as UIState.AIChatSuccess).message
            isAskingAI = false
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
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

        // Conditional Display: Fruit Search Section OR Random Image Section
        patient?.let { p ->

            // Show Fruit Search if scores are NOT optimal (< 2)
            if (calculateFruitScore(p.fruitSizeScore, p.fruitVariationsScore) < 5) {
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

                                Button(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            coroutineScope.launch {
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
                                    if (isSearching) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.Red,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (isSearching) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.White,
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
                    }
                }

                // Fruit Search Results (only shown when search section is displayed)
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FruitResultCard(fruit = fruitResult)
                    }
                }
            }
            // Show Random Image if scores are optimal (>= 2)
            else {
                item {
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
                            Text(
                                text = "Well done! You scored well on fruit score! " +
                                        "\nHere is a random image for you!",
                                fontSize = 16.sp,
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (isLoadingImage) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.Red)
                                }
                            } else if (randomImageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = randomImageUrl,
                                    contentDescription = "Random image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Refresh image button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isLoadingImage = true
                                        randomImageUrl = fruitRepository.getRandomFruitImageUrl()
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

        // Motivational Message Section
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
                        painter = painterResource(id = R.drawable.message_icon), // Using your
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

        // AI Generated Message Display
        item {
            when (uiState) {
                is UIState.NutriCoachLoading -> {
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

                }
                is UIState.NutriCoachSuccess -> {
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
                }

                is UIState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Error: ${(uiState as UIState.Error).errorMessage}",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is UIState.Initial -> {
                    // Show nothing initially
                }

                is UIState.ClinicianSuccess -> {
                    // This state is not used in NutriCoach, only in Clinician dashboard
                }

                is UIState.AIChatSuccess -> {
                }

                UIState.ClinicianLoading -> {}
                UIState.AIChatLoading -> {}
            }
        }

        // Show All Tips Button
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = { showTipsDialog = true },
//                    modifier = Modifier.fillMaxWidth(0.6f),
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
// AI Bot Section
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
                    Text(
                        text = "NutriCoach AI Bot",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = aiQuestion,
                            onValueChange = { aiQuestion = it },
                            placeholder = {
                                val suggestions = listOf(
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

                        Button(
                            onClick = {
                                isAskingAI = true
                                aiResponse = null // Clear previous response
                                aiViewModel.generateCustomNutritionResponse(currentUserID, aiQuestion.trim())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
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
                }
            }
        }

// AI Response Card

// Add this to handle the updated UI state in the existing when (uiState) block
// Update the existing when (uiState) is UIState.NutriCoachSuccess case to not interfere with AI chat

// Update the existing AI Generated Message Display item
        item {
            when (uiState) {
                is UIState.AIChatLoading -> {
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

                }

                is UIState.AIChatSuccess -> {
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
                                    contentDescription = "AI Response",
                                    tint = Color(0xFF7B1FA2),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Nutrition Assistant",
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
                }

                is UIState.NutriCoachSuccess -> {
                    // This is handled by the AI Response Card above
                }

                is UIState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Error: ${(uiState as UIState.Error).errorMessage}",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is UIState.Initial -> {
                    // Show nothing initially
                }

                is UIState.ClinicianSuccess -> {
                    // This state is not used in NutriCoach, only in Clinician dashboard
                }

                UIState.ClinicianLoading -> {}
                UIState.NutriCoachLoading -> {}
            }
        }
        // Add some bottom spacing for the navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Bottom Navigation
    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }

    // Tips Dialog
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
fun FruitResultCard(fruit: FruitResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Use actual fruit data if available, otherwise use default values
            val family = fruit?.family ?: "-"
            val calories = fruit?.nutritions?.calories?.toString() ?: "0"
            val fat = fruit?.nutritions?.fat?.toString() ?: "0"
            val sugar = fruit?.nutritions?.sugar?.toString() ?: "0"
            val carbohydrates = fruit?.nutritions?.carbohydrates?.toString() ?: "0"
            val protein = fruit?.nutritions?.protein?.toString() ?: "0"

            // Table rows with dividers
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
fun TableRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp), // Add vertical padding for spacing
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp, // Increased font size
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f).padding(start = 12.dp) // Equal space for both columns
        )
        VerticalDivider(
            modifier = Modifier
                .height(25.dp)
                .padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp)) // Space between label and value
        Text(
            text = value,
            fontSize = 16.sp, // Increased font size
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TipsDialog(
    tips: List<NutriCoachTip>,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit
) {
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
                // "x" button in the top-right corner
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Align to the top-right corner
//                        .padding(8.dp) // Add padding for spacing
                ) {
                    Text(text = "x", fontSize = 25.sp, color = Color.Black)
                }

                // Main content of the dialog
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Past Tips",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

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

                    if (tips.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
            Text(
                text = tip.tipMessage,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Advice from ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(tip.timestamp))}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

fun calculateFruitScore(fruitSizeScore: Float, fruitVariationsScore: Float): Double {
    // Cap the scores
    val cappedFruitServeSize = if (fruitSizeScore > 2) 2 else fruitSizeScore
    val cappedFruitVariationsScore = if (fruitVariationsScore > 5) 5 else fruitVariationsScore

    // Calculate the optimal score
    return (cappedFruitServeSize.toDouble() / 2 * 5) + cappedFruitVariationsScore.toDouble()
}

fun processText(input: String): String {
    // Remove empty lines and process each line
    val trimmedLines = input.lines()
        .filter { it.isNotBlank() } // Remove empty lines
        .joinToString("\n") { line ->
            // Replace sentences wrapped with ** to bold
            line.replace(Regex("\\*\\*(.*?)\\*\\*")) { matchResult ->
                "<b>${matchResult.groupValues[1]}</b>"
            }
        }
    return trimmedLines
}