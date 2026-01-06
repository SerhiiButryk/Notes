package com.notes.auth_ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.auth_ui.Header
import com.notes.auth_ui.SubHeader
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun VerificationEmailUI(
    onRetry: () -> Unit,
    verificationEmailSent: Boolean,
    title: String,
    subTitle: String,
) {
    VerificationEmailUIImpl(
        onRetry = onRetry,
        title = title,
        subTitle = subTitle,
        verificationEmailSent = verificationEmailSent,
    )
}

@Composable
private fun VerificationEmailUIImpl(
    onRetry: () -> Unit,
    verificationEmailSent: Boolean,
    title: String,
    subTitle: String,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Header(
                text = title,
                modifier =
                    Modifier
                        .padding(all = 20.dp)
                        .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            if (verificationEmailSent) {
                SubHeader(
                    text = subTitle,
                    modifier =
                        Modifier
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                            .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            // Wait 60 seconds for confirmation and then allow resend
            var remainingTime by rememberSaveable { mutableIntStateOf(60) }
            var isRunning by rememberSaveable { mutableStateOf(verificationEmailSent) }

            LaunchedEffect(isRunning) {
                if (isRunning && remainingTime > 0) {
                    while (remainingTime > 0) {
                        delay(1.seconds)
                        remainingTime--
                    }
                    isRunning = false
                    remainingTime = 60
                }
            }

            if (isRunning) {
                Text(
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp),
                    text = remainingTime.toString(),
                    fontSize = 20.sp,
                )
            }

            // If timer is running then not showing button
            if (!isRunning) {
                Button(onClick = {
                    onRetry()
                    // Start timer
                    isRunning = true
                }) {
                    Text(text = "Send again")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
)
@Composable
private fun VerificationEmailUIPreview() {
    VerificationEmailUI(onRetry = {}, true, "Title", "Subtitle")
}
