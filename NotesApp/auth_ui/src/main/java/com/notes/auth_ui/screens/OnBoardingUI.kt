package com.notes.auth_ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.auth_ui.Header
import com.notes.auth_ui.R
import com.notes.ui.isTabletOrFoldableExpanded
import com.notes.ui.theme.AppTheme

@Composable
fun OnboardingScreen(onContinue: () -> Unit = {}) {
    AppTheme {
        OnboardingScreenImpl(onContinue = onContinue)
    }
}

@Composable
private fun OnboardingScreenImpl(onContinue: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        val sc = currentWindowAdaptiveInfo().windowSizeClass

        val sizeModifier: Modifier = if (isTabletOrFoldableExpanded(sc)) {
            // Add max width bound
            Modifier.widthIn(max = 800.dp)
        } else {
            // Full size
            Modifier.fillMaxSize()
                .padding(24.dp)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = sizeModifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // From https://undraw.co/search/notes
                Image(
                    painter = painterResource(id = R.drawable.onboarding_notes_icon),
                    contentDescription = "Note Icon",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.Fit
                )

                Header("Welcome to Notes")

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Capture your thoughts and ideas instantly, stay organized, and never forget a thing.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier.widthIn(400.dp)
                ) {
                    Text("Continue")
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES
)
@Composable
private fun OnboardingScreenPrev() {
    OnboardingScreen()
}

