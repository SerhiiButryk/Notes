package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notes.ui.VideoPlayer
import com.notes.ui.isPhoneLandScape
import com.notes.ui.isTabletOrFoldableExpanded

@Composable
internal fun OnboardingScreen(onContinue: () -> Unit = {}) {
    OnboardingScreenImpl(onContinue = onContinue)
}

@Composable
private fun OnboardingScreenImpl(onContinue: () -> Unit = {}) {

    val sc = currentWindowAdaptiveInfo().windowSizeClass

    val sizeModifier: Modifier =
        if (isTabletOrFoldableExpanded(sc)) {
            // Add max width bound
            Modifier.widthIn(max = 800.dp)
        } else {
            // Full size
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        }

    OnBoardingUIImpl(
        modifier = sizeModifier,
        onContinue = onContinue,
        preview = {
            // Generated using https://www.vidnoz.com/image-to-video-ai.html
            // From image https://undraw.co/search/notes
            val url = "asset:///preview_video.mp4"

            if (isPhoneLandScape(sc) && !isTabletOrFoldableExpanded(sc)) {
                VideoPlayer(
                    videoLink = url,
                    modifier =
                        Modifier
                            .size(200.dp)
                            .padding(bottom = 32.dp),
                )
            } else {
                VideoPlayer(
                    videoLink = url,
                    modifier =
                        Modifier
                            .size(400.dp)
                            .padding(bottom = 32.dp),
                )
            }
        }
    )

}