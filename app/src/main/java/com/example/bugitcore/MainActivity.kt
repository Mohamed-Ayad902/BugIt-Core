package com.example.bugitcore

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bugitcore.ui.theme.BugItCoreTheme
import com.example.core_contracts.extensions.loge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugItCoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BugReportScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BugReportScreen(
    modifier: Modifier = Modifier,
    viewModel: BugReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    // Handle State Transitions
    LaunchedEffect(uiState) {
        when (uiState) {
            is BugReportUiState.Success -> {
                "Success: ${(uiState as BugReportUiState.Success)}".loge("BugReportScreen")
                Toast.makeText(context, (uiState as BugReportUiState.Success).message, Toast.LENGTH_SHORT).show()
                description = ""
                selectedImageUri = null
                viewModel.resetToIdle()
            }
            is BugReportUiState.Error -> {
                "Error: ${(uiState as BugReportUiState.Error)}".loge("BugReportScreen")
                Toast.makeText(context, (uiState as BugReportUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetToIdle()
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Submit a Bug", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe the bug") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            enabled = uiState !is BugReportUiState.Loading
        )

        Button(
            onClick = {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            enabled = uiState !is BugReportUiState.Loading
        ) {
            Text(if (selectedImageUri == null) "Attach Screenshot" else "Image Attached!")
        }

        Spacer(modifier = Modifier.weight(1f))

        // State-driven UI components
        when (uiState) {
            is BugReportUiState.Loading -> CircularProgressIndicator()
            else -> {
                Button(
                    onClick = {
                        if (selectedImageUri != null) {
                            viewModel.submitBug(description, selectedImageUri.toString())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = description.isNotBlank() && selectedImageUri != null
                ) {
                    Text("Submit Bug")
                }
            }
        }
    }
}