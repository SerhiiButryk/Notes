/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serhii.apps.notes.R

@Composable
fun ButtonUI(
    text: String,
    @SuppressLint("ModifierParameter") modifier: Modifier? = null,
    onClick: () -> Unit
) {

    val bottomPadding = dimensionResource(R.dimen.button_bottom_padding)

    val buttonModifier = modifier ?: Modifier
        .padding(bottom = bottomPadding)
        .fillMaxWidth()

    Button(
        onClick = { onClick() },
        modifier = buttonModifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TitleUI(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(bottom = 10.dp, top = 10.dp)
    )
}

@Composable
fun DescriptionUI(desc: String) {
    Text(
        text = desc,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
    )
}