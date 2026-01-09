package com.notes.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Taken from https://composeicons.com/

val EYE_OPEN_ICON: ImageVector
    get() {
        if (_EYE_ICON != null) {
            return _EYE_ICON!!
        }
        _EYE_ICON =
            ImageVector
                .Builder(
                    name = "Eye",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
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
                        pathFillType = PathFillType.NonZero,
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
                        pathFillType = PathFillType.NonZero,
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
        _EYE_HIDDEN_ICON =
            ImageVector
                .Builder(
                    name = "EyeSlash",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
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
                        pathFillType = PathFillType.NonZero,
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

val EMAIL_ICON: ImageVector
    get() {
        if (_EMAIL_ICON != null) {
            return _EMAIL_ICON!!
        }
        _EMAIL_ICON =
            ImageVector
                .Builder(
                    name = "Alternate_email",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(480f, 880f)
                        quadToRelative(-83f, 0f, -156f, -31.5f)
                        reflectiveQuadTo(197f, 763f)
                        reflectiveQuadToRelative(-85.5f, -127f)
                        reflectiveQuadTo(80f, 480f)
                        reflectiveQuadToRelative(31.5f, -156f)
                        reflectiveQuadTo(197f, 197f)
                        reflectiveQuadToRelative(127f, -85.5f)
                        reflectiveQuadTo(480f, 80f)
                        reflectiveQuadToRelative(156f, 31.5f)
                        reflectiveQuadTo(763f, 197f)
                        reflectiveQuadToRelative(85.5f, 127f)
                        reflectiveQuadTo(880f, 480f)
                        verticalLineToRelative(58f)
                        quadToRelative(0f, 59f, -40.5f, 100.5f)
                        reflectiveQuadTo(740f, 680f)
                        quadToRelative(-35f, 0f, -66f, -15f)
                        reflectiveQuadToRelative(-52f, -43f)
                        quadToRelative(-29f, 29f, -65.5f, 43.5f)
                        reflectiveQuadTo(480f, 680f)
                        quadToRelative(-83f, 0f, -141.5f, -58.5f)
                        reflectiveQuadTo(280f, 480f)
                        reflectiveQuadToRelative(58.5f, -141.5f)
                        reflectiveQuadTo(480f, 280f)
                        reflectiveQuadToRelative(141.5f, 58.5f)
                        reflectiveQuadTo(680f, 480f)
                        verticalLineToRelative(58f)
                        quadToRelative(0f, 26f, 17f, 44f)
                        reflectiveQuadToRelative(43f, 18f)
                        reflectiveQuadToRelative(43f, -18f)
                        reflectiveQuadToRelative(17f, -44f)
                        verticalLineToRelative(-58f)
                        quadToRelative(0f, -134f, -93f, -227f)
                        reflectiveQuadToRelative(-227f, -93f)
                        reflectiveQuadToRelative(-227f, 93f)
                        reflectiveQuadToRelative(-93f, 227f)
                        reflectiveQuadToRelative(93f, 227f)
                        reflectiveQuadToRelative(227f, 93f)
                        horizontalLineToRelative(200f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(0f, -280f)
                        quadToRelative(50f, 0f, 85f, -35f)
                        reflectiveQuadToRelative(35f, -85f)
                        reflectiveQuadToRelative(-35f, -85f)
                        reflectiveQuadToRelative(-85f, -35f)
                        reflectiveQuadToRelative(-85f, 35f)
                        reflectiveQuadToRelative(-35f, 85f)
                        reflectiveQuadToRelative(35f, 85f)
                        reflectiveQuadToRelative(85f, 35f)
                    }
                }.build()
        return _EMAIL_ICON!!
    }

private var _EMAIL_ICON: ImageVector? = null

val KEY_ICON: ImageVector
    get() {
        if (_KEY_ICON != null) {
            return _KEY_ICON!!
        }
        _KEY_ICON =
            ImageVector
                .Builder(
                    name = "Vpn_key",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(280f, 720f)
                        quadToRelative(-100f, 0f, -170f, -70f)
                        reflectiveQuadTo(40f, 480f)
                        reflectiveQuadToRelative(70f, -170f)
                        reflectiveQuadToRelative(170f, -70f)
                        quadToRelative(66f, 0f, 121f, 33f)
                        reflectiveQuadToRelative(87f, 87f)
                        horizontalLineToRelative(432f)
                        verticalLineToRelative(240f)
                        horizontalLineToRelative(-80f)
                        verticalLineToRelative(120f)
                        horizontalLineTo(600f)
                        verticalLineToRelative(-120f)
                        horizontalLineTo(488f)
                        quadToRelative(-32f, 54f, -87f, 87f)
                        reflectiveQuadToRelative(-121f, 33f)
                        moveToRelative(0f, -80f)
                        quadToRelative(66f, 0f, 106f, -40.5f)
                        reflectiveQuadToRelative(48f, -79.5f)
                        horizontalLineToRelative(246f)
                        verticalLineToRelative(120f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(-120f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(-80f)
                        horizontalLineTo(434f)
                        quadToRelative(-8f, -39f, -48f, -79.5f)
                        reflectiveQuadTo(280f, 320f)
                        reflectiveQuadToRelative(-113f, 47f)
                        reflectiveQuadToRelative(-47f, 113f)
                        reflectiveQuadToRelative(47f, 113f)
                        reflectiveQuadToRelative(113f, 47f)
                        moveToRelative(0f, -80f)
                        quadToRelative(33f, 0f, 56.5f, -23.5f)
                        reflectiveQuadTo(360f, 480f)
                        reflectiveQuadToRelative(-23.5f, -56.5f)
                        reflectiveQuadTo(280f, 400f)
                        reflectiveQuadToRelative(-56.5f, 23.5f)
                        reflectiveQuadTo(200f, 480f)
                        reflectiveQuadToRelative(23.5f, 56.5f)
                        reflectiveQuadTo(280f, 560f)
                        moveToRelative(0f, -80f)
                    }
                }.build()
        return _KEY_ICON!!
    }

private var _KEY_ICON: ImageVector? = null

val SAVE_ICON: ImageVector
    get() {
        if (_Save != null) return _Save!!

        _Save =
            ImageVector
                .Builder(
                    name = "Save",
                    defaultWidth = 16.dp,
                    defaultHeight = 16.dp,
                    viewportWidth = 16f,
                    viewportHeight = 16f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Black),
                    ) {
                        moveTo(2f, 1f)
                        arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
                        verticalLineToRelative(12f)
                        arcToRelative(1f, 1f, 0f, false, false, 1f, 1f)
                        horizontalLineToRelative(12f)
                        arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)
                        verticalLineTo(2f)
                        arcToRelative(1f, 1f, 0f, false, false, -1f, -1f)
                        horizontalLineTo(9.5f)
                        arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
                        verticalLineToRelative(7.293f)
                        lineToRelative(2.646f, -2.647f)
                        arcToRelative(0.5f, 0.5f, 0f, false, true, 0.708f, 0.708f)
                        lineToRelative(-3.5f, 3.5f)
                        arcToRelative(0.5f, 0.5f, 0f, false, true, -0.708f, 0f)
                        lineToRelative(-3.5f, -3.5f)
                        arcToRelative(0.5f, 0.5f, 0f, true, true, 0.708f, -0.708f)
                        lineTo(7.5f, 9.293f)
                        verticalLineTo(2f)
                        arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
                        horizontalLineTo(14f)
                        arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                        verticalLineToRelative(12f)
                        arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                        horizontalLineTo(2f)
                        arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
                        verticalLineTo(2f)
                        arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
                        horizontalLineToRelative(2.5f)
                        arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                        close()
                    }
                }.build()

        return _Save!!
    }

private var _Save: ImageVector? = null

val CLEAR_ALL: ImageVector
    get() {
        if (_ClearAll != null) return _ClearAll!!

        _ClearAll =
            ImageVector
                .Builder(
                    name = "ClearAll",
                    defaultWidth = 16.dp,
                    defaultHeight = 16.dp,
                    viewportWidth = 16f,
                    viewportHeight = 16f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Black),
                    ) {
                        moveTo(10f, 12.6f)
                        lineToRelative(0.7f, 0.7f)
                        lineToRelative(1.6f, -1.6f)
                        lineToRelative(1.6f, 1.6f)
                        lineToRelative(0.8f, -0.7f)
                        lineTo(13f, 11f)
                        lineToRelative(1.7f, -1.6f)
                        lineToRelative(-0.8f, -0.8f)
                        lineToRelative(-1.6f, 1.7f)
                        lineToRelative(-1.6f, -1.7f)
                        lineToRelative(-0.7f, 0.8f)
                        lineToRelative(1.6f, 1.6f)
                        lineToRelative(-1.6f, 1.6f)
                        close()
                        moveTo(1f, 4f)
                        horizontalLineToRelative(14f)
                        verticalLineTo(3f)
                        horizontalLineTo(1f)
                        verticalLineToRelative(1f)
                        close()
                        moveToRelative(0f, 3f)
                        horizontalLineToRelative(14f)
                        verticalLineTo(6f)
                        horizontalLineTo(1f)
                        verticalLineToRelative(1f)
                        close()
                        moveToRelative(8f, 2.5f)
                        verticalLineTo(9f)
                        horizontalLineTo(1f)
                        verticalLineToRelative(1f)
                        horizontalLineToRelative(8f)
                        verticalLineToRelative(-0.5f)
                        close()
                        moveTo(9f, 13f)
                        verticalLineToRelative(-1f)
                        horizontalLineTo(1f)
                        verticalLineToRelative(1f)
                        horizontalLineToRelative(8f)
                        close()
                    }
                }.build()

        return _ClearAll!!
    }

private var _ClearAll: ImageVector? = null

val Arrow_up: ImageVector
    get() {
        if (_Arrow_up != null) return _Arrow_up!!

        _Arrow_up =
            ImageVector
                .Builder(
                    name = "Keyboard_arrow_up",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                    ) {
                        moveTo(480f, 432f)
                        lineTo(296f, 616f)
                        lineToRelative(-56f, -56f)
                        lineToRelative(240f, -240f)
                        lineToRelative(240f, 240f)
                        lineToRelative(-56f, 56f)
                        close()
                    }
                }.build()

        return _Arrow_up!!
    }

private var _Arrow_up: ImageVector? = null

val List: ImageVector
    get() {
        if (_List != null) return _List!!

        _List =
            ImageVector
                .Builder(
                    name = "List",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                    ) {
                        moveTo(280f, 360f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(560f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(0f, 160f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(560f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(0f, 160f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(560f)
                        verticalLineToRelative(80f)
                        close()
                        moveTo(160f, 360f)
                        quadToRelative(-17f, 0f, -28.5f, -11.5f)
                        reflectiveQuadTo(120f, 320f)
                        reflectiveQuadToRelative(11.5f, -28.5f)
                        reflectiveQuadTo(160f, 280f)
                        reflectiveQuadToRelative(28.5f, 11.5f)
                        reflectiveQuadTo(200f, 320f)
                        reflectiveQuadToRelative(-11.5f, 28.5f)
                        reflectiveQuadTo(160f, 360f)
                        moveToRelative(0f, 160f)
                        quadToRelative(-17f, 0f, -28.5f, -11.5f)
                        reflectiveQuadTo(120f, 480f)
                        reflectiveQuadToRelative(11.5f, -28.5f)
                        reflectiveQuadTo(160f, 440f)
                        reflectiveQuadToRelative(28.5f, 11.5f)
                        reflectiveQuadTo(200f, 480f)
                        reflectiveQuadToRelative(-11.5f, 28.5f)
                        reflectiveQuadTo(160f, 520f)
                        moveToRelative(0f, 160f)
                        quadToRelative(-17f, 0f, -28.5f, -11.5f)
                        reflectiveQuadTo(120f, 640f)
                        reflectiveQuadToRelative(11.5f, -28.5f)
                        reflectiveQuadTo(160f, 600f)
                        reflectiveQuadToRelative(28.5f, 11.5f)
                        reflectiveQuadTo(200f, 640f)
                        reflectiveQuadToRelative(-11.5f, 28.5f)
                        reflectiveQuadTo(160f, 680f)
                    }
                }.build()

        return _List!!
    }

private var _List: ImageVector? = null

val Format_list_numbered: ImageVector
    get() {
        if (_Format_list_numbered != null) return _Format_list_numbered!!

        _Format_list_numbered =
            ImageVector
                .Builder(
                    name = "Format_list_numbered",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                    ) {
                        moveTo(120f, 880f)
                        verticalLineToRelative(-60f)
                        horizontalLineToRelative(100f)
                        verticalLineToRelative(-30f)
                        horizontalLineToRelative(-60f)
                        verticalLineToRelative(-60f)
                        horizontalLineToRelative(60f)
                        verticalLineToRelative(-30f)
                        horizontalLineTo(120f)
                        verticalLineToRelative(-60f)
                        horizontalLineToRelative(120f)
                        quadToRelative(17f, 0f, 28.5f, 11.5f)
                        reflectiveQuadTo(280f, 680f)
                        verticalLineToRelative(40f)
                        quadToRelative(0f, 17f, -11.5f, 28.5f)
                        reflectiveQuadTo(240f, 760f)
                        quadToRelative(17f, 0f, 28.5f, 11.5f)
                        reflectiveQuadTo(280f, 800f)
                        verticalLineToRelative(40f)
                        quadToRelative(0f, 17f, -11.5f, 28.5f)
                        reflectiveQuadTo(240f, 880f)
                        close()
                        moveToRelative(0f, -280f)
                        verticalLineToRelative(-110f)
                        quadToRelative(0f, -17f, 11.5f, -28.5f)
                        reflectiveQuadTo(160f, 450f)
                        horizontalLineToRelative(60f)
                        verticalLineToRelative(-30f)
                        horizontalLineTo(120f)
                        verticalLineToRelative(-60f)
                        horizontalLineToRelative(120f)
                        quadToRelative(17f, 0f, 28.5f, 11.5f)
                        reflectiveQuadTo(280f, 400f)
                        verticalLineToRelative(70f)
                        quadToRelative(0f, 17f, -11.5f, 28.5f)
                        reflectiveQuadTo(240f, 510f)
                        horizontalLineToRelative(-60f)
                        verticalLineToRelative(30f)
                        horizontalLineToRelative(100f)
                        verticalLineToRelative(60f)
                        close()
                        moveToRelative(60f, -280f)
                        verticalLineToRelative(-180f)
                        horizontalLineToRelative(-60f)
                        verticalLineToRelative(-60f)
                        horizontalLineToRelative(120f)
                        verticalLineToRelative(240f)
                        close()
                        moveToRelative(180f, 440f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(480f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(0f, -240f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(480f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(0f, -240f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(480f)
                        verticalLineToRelative(80f)
                        close()
                    }
                }.build()

        return _Format_list_numbered!!
    }

private var _Format_list_numbered: ImageVector? = null

val Undo: ImageVector
    get() {
        if (_Undo != null) return _Undo!!

        _Undo =
            ImageVector
                .Builder(
                    name = "Undo",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                    ) {
                        moveTo(280f, 760f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(284f)
                        quadToRelative(63f, 0f, 109.5f, -40f)
                        reflectiveQuadTo(720f, 540f)
                        reflectiveQuadToRelative(-46.5f, -100f)
                        reflectiveQuadTo(564f, 400f)
                        horizontalLineTo(312f)
                        lineToRelative(104f, 104f)
                        lineToRelative(-56f, 56f)
                        lineToRelative(-200f, -200f)
                        lineToRelative(200f, -200f)
                        lineToRelative(56f, 56f)
                        lineToRelative(-104f, 104f)
                        horizontalLineToRelative(252f)
                        quadToRelative(97f, 0f, 166.5f, 63f)
                        reflectiveQuadTo(800f, 540f)
                        reflectiveQuadToRelative(-69.5f, 157f)
                        reflectiveQuadTo(564f, 760f)
                        close()
                    }
                }.build()

        return _Undo!!
    }

private var _Undo: ImageVector? = null

val UndoInactive: ImageVector
    get() {
        if (_UndoInactive != null) return _UndoInactive!!

        _UndoInactive =
            ImageVector
                .Builder(
                    name = "Undo",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                    ) {
                        moveTo(280f, 760f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(284f)
                        quadToRelative(63f, 0f, 109.5f, -40f)
                        reflectiveQuadTo(720f, 540f)
                        reflectiveQuadToRelative(-46.5f, -100f)
                        reflectiveQuadTo(564f, 400f)
                        horizontalLineTo(312f)
                        lineToRelative(104f, 104f)
                        lineToRelative(-56f, 56f)
                        lineToRelative(-200f, -200f)
                        lineToRelative(200f, -200f)
                        lineToRelative(56f, 56f)
                        lineToRelative(-104f, 104f)
                        horizontalLineToRelative(252f)
                        quadToRelative(97f, 0f, 166.5f, 63f)
                        reflectiveQuadTo(800f, 540f)
                        reflectiveQuadToRelative(-69.5f, 157f)
                        reflectiveQuadTo(564f, 760f)
                        close()
                    }
                }.build()

        return _UndoInactive!!
    }

private var _UndoInactive: ImageVector? = null

val Redo: ImageVector
    get() {
        if (_Redo != null) return _Redo!!

        _Redo =
            ImageVector
                .Builder(
                    name = "Redo",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                    ) {
                        moveTo(396f, 760f)
                        quadToRelative(-97f, 0f, -166.5f, -63f)
                        reflectiveQuadTo(160f, 540f)
                        reflectiveQuadToRelative(69.5f, -157f)
                        reflectiveQuadTo(396f, 320f)
                        horizontalLineToRelative(252f)
                        lineTo(544f, 216f)
                        lineToRelative(56f, -56f)
                        lineToRelative(200f, 200f)
                        lineToRelative(-200f, 200f)
                        lineToRelative(-56f, -56f)
                        lineToRelative(104f, -104f)
                        horizontalLineTo(396f)
                        quadToRelative(-63f, 0f, -109.5f, 40f)
                        reflectiveQuadTo(240f, 540f)
                        reflectiveQuadToRelative(46.5f, 100f)
                        reflectiveQuadTo(396f, 680f)
                        horizontalLineToRelative(284f)
                        verticalLineToRelative(80f)
                        close()
                    }
                }.build()

        return _Redo!!
    }

private var _Redo: ImageVector? = null

val RedoInactive: ImageVector
    get() {
        if (_RedoInactive != null) return _RedoInactive!!

        _RedoInactive =
            ImageVector
                .Builder(
                    name = "Redo",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Gray),
                    ) {
                        moveTo(396f, 760f)
                        quadToRelative(-97f, 0f, -166.5f, -63f)
                        reflectiveQuadTo(160f, 540f)
                        reflectiveQuadToRelative(69.5f, -157f)
                        reflectiveQuadTo(396f, 320f)
                        horizontalLineToRelative(252f)
                        lineTo(544f, 216f)
                        lineToRelative(56f, -56f)
                        lineToRelative(200f, 200f)
                        lineToRelative(-200f, 200f)
                        lineToRelative(-56f, -56f)
                        lineToRelative(104f, -104f)
                        horizontalLineTo(396f)
                        quadToRelative(-63f, 0f, -109.5f, 40f)
                        reflectiveQuadTo(240f, 540f)
                        reflectiveQuadToRelative(46.5f, 100f)
                        reflectiveQuadTo(396f, 680f)
                        horizontalLineToRelative(284f)
                        verticalLineToRelative(80f)
                        close()
                    }
                }.build()

        return _RedoInactive!!
    }

private var _RedoInactive: ImageVector? = null
