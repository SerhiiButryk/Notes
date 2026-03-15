package com.notes.auth_ui.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notes.ui.getIconByKey
import com.notes.ui.isTabletOrFoldableExpanded
import com.notes.ui.previewIcon

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

            Image(
                modifier = Modifier.padding(10.dp, 10.dp, bottom = 30.dp),
                painter = getIconByKey(previewIcon),
                contentDescription = null,
            )

            // TODO: Can have a video preview if I got a good animation
            // Generated using https://www.vidnoz.com/image-to-video-ai.html
            // From image https://undraw.co/search/notes
//            val url = "asset:///preview_video.mp4"
//
//            if (isPhoneLandScape(sc) && !isTabletOrFoldableExpanded(sc)) {
//                VideoPlayer(
//                    videoLink = url,
//                    modifier =
//                        Modifier
//                            .size(200.dp)
//                            .padding(bottom = 32.dp),
//                )
//            } else {
//                VideoPlayer(
//                    videoLink = url,
//                    modifier =
//                        Modifier
//                            .size(400.dp)
//                            .padding(bottom = 32.dp),
//                )
//            }
        }
    )

}