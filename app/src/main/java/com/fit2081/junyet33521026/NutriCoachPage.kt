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
import com.fit2081.junyet33521026.data.AuthManager
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

    val coroutineScope = rememberCoroutineScope()
    val uiState by aiViewModel.uiState.collectAsState()

    // Load patient data on startup
    LaunchedEffect(currentUserID) {
        patient = patientViewModel.getPatient(currentUserID)

        // Load random fruit image if fruit scores are optimal
        patient?.let { p ->
            if (p.fruitSizeScore <= 2 && p.fruitVariationsScore <= 2) {
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

            // Cap the scores at 2
            val cappedFruitServeSize = if (p.fruitSizeScore > 2) 2 else p.fruitSizeScore
            val cappedFruitVariationsScore = if (p.fruitVariationsScore > 5) 2 else p.fruitVariationsScore

            // Calculate optimal score: (fruitServeSize/2 * 5 + fruitVariationsScore) >= 5
            val optimalScore = (cappedFruitServeSize.toDouble() / 2 * 5) + cappedFruitVariationsScore.toDouble()

            // Show Fruit Search if scores are NOT optimal (< 2)
            if (optimalScore < 5) {
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
                                text = "Fruit Name",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

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
                                    shape = RoundedCornerShape(8.dp),
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
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No image available",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
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
                is UIState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
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