package com.notes.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EYE_OPEN_ICON: ImageVector
    get() {
        if (_EYE_ICON != null) {
            return _EYE_ICON!!
        }
        _EYE_ICON = ImageVector.Builder(
            name = "Eye",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.03555f, 12.3224f)
                curveTo(1.9665f, 12.1151f, 1.9664f, 11.8907f, 2.0354f, 11.6834f)
                curveTo(3.4237f, 7.5097f, 7.3608f, 4.5f, 12.0008f, 4.5f)
                curveTo(16.6387f, 4.5f, 20.5742f, 7.5069f, 21.9643f, 11.6776f)
                curveTo(22.0334f, 11.8849f, 22.0335f, 12.1093f, 21.9645f, 12.3166f)
                curveTo(20.5761f, 16.4903f, 16.6391f, 19.5f, 11.9991f, 19.5f)
                curveTo(7.3612f, 19.5f, 3.4256f, 16.4931f, 2.0356f, 12.3224f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15f, 12f)
                curveTo(15f, 13.6569f, 13.6569f, 15f, 12f, 15f)
                curveTo(10.3431f, 15f, 9f, 13.6569f, 9f, 12f)
                curveTo(9f, 10.3431f, 10.3431f, 9f, 12f, 9f)
                curveTo(13.6569f, 9f, 15f, 10.3431f, 15f, 12f)
                close()
            }
        }.build()
        return _EYE_ICON!!
    }

private var _EYE_ICON: ImageVector? = null

val EYE_HIDDEN_ICON: ImageVector
    get() {
        if (_EYE_HIDDEN_ICON != null) {
            return _EYE_HIDDEN_ICON!!
        }
        _EYE_HIDDEN_ICON = ImageVector.Builder(
            name = "EyeSlash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(3.97993f, 8.22257f)
                curveTo(3.0568f, 9.3138f, 2.3524f, 10.596f, 1.9344f, 12.0015f)
                curveTo(3.2256f, 16.338f, 7.2431f, 19.5f, 11.9991f, 19.5f)
                curveTo(12.9917f, 19.5f, 13.9521f, 19.3623f, 14.8623f, 19.1049f)
                moveTo(6.22763f, 6.22763f)
                curveTo(7.8839f, 5.1356f, 9.8677f, 4.5f, 12f, 4.5f)
                curveTo(16.756f, 4.5f, 20.7734f, 7.662f, 22.0647f, 11.9985f)
                curveTo(21.3528f, 14.3919f, 19.8106f, 16.4277f, 17.772f, 17.772f)
                moveTo(6.22763f, 6.22763f)
                lineTo(3f, 3f)
                moveTo(6.22763f, 6.22763f)
                lineTo(9.87868f, 9.87868f)
                moveTo(17.772f, 17.772f)
                lineTo(21f, 21f)
                moveTo(17.772f, 17.772f)
                lineTo(14.1213f, 14.1213f)
                moveTo(14.1213f, 14.1213f)
                curveTo(14.6642f, 13.5784f, 15f, 12.8284f, 15f, 12f)
                curveTo(15f, 10.3431f, 13.6569f, 9f, 12f, 9f)
                curveTo(11.1716f, 9f, 10.4216f, 9.3358f, 9.8787f, 9.8787f)
                moveTo(14.1213f, 14.1213f)
                lineTo(9.87868f, 9.87868f)
            }
        }.build()
        return _EYE_HIDDEN_ICON!!
    }

private var _EYE_HIDDEN_ICON: ImageVector? = null