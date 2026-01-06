package com.notes.ui

import androidx.window.core.layout.WindowSizeClass

fun isTabletOrFoldableExpanded(sc: WindowSizeClass): Boolean =
    sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
        sc.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

fun isPhoneLandScape(sc: WindowSizeClass): Boolean =
    sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
        sc.isHeightAtLeastBreakpoint(0)

fun isMiddleWidth(sc: WindowSizeClass): Boolean = sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND + 1)
