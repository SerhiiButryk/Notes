/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ButtonUI(
    text: String,
    @SuppressLint("ModifierParameter") modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
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
        modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUI(
    hint: String,
    hasBackButton: Boolean = false,
    menuOptionsList: List<MenuOptions> = emptyList(),
    backAction: (() -> Unit)? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {

        var query by remember { mutableStateOf("") }

        DockedSearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = query,
            onQueryChange = {
                query = it
            },
            onSearch = { },
            active = false,
            onActiveChange = {
            },
            placeholder = { Text(text = hint) },
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Back button
                    if (hasBackButton) {
                        IconButton(onClick = { backAction?.invoke() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    }

                    // Search button
                    IconButton(onClick = { /* Do smth */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
            },
            trailingIcon = {
                Menu(menuOptionsList)
            }
        ) {
            /* Empty */
        }
    }
}

class MenuOptions(
    val textId: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val addDivider: Boolean = false
)

@Composable
fun Menu(menuOptionsList: List<MenuOptions>) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {

        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (option in menuOptionsList) {

                DropdownMenuItem(
                    text = { Text(text = stringResource(id = option.textId), style = MaterialTheme.typography.bodySmall) },
                    onClick = { option.onClick() },
                    leadingIcon = {
                        Icon(option.icon, contentDescription = null)
                    }
                )

                if (option.addDivider) {
                    HorizontalDivider()
                }
            }
        }
    }
}