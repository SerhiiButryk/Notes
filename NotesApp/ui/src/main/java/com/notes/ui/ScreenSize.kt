package com.notes.ui

import androidx.window.core.layout.WindowSizeClass

fun isAtLeastTablet(sc: WindowSizeClass): Boolean {
    return sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
            && sc.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
}

fun isPhoneLandScape(sc: WindowSizeClass): Boolean {
    return sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
            && sc.isHeightAtLeastBreakpoint(0)
}