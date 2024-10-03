/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.state_holders.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun SettingsUI(viewModel: SettingsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_item),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        val settingsItems = uiState.value.items

        SettingsListUI(innerPadding, settingsItems)

        val dialogState = uiState.value.dialogState
        if (dialogState.canOpen()) {
            if (dialogState is SettingsViewModel.ListDialogUIState) {
                OptionsListDialog(dialogState = dialogState)
            } else if (dialogState is SettingsViewModel.TextInputDialogUIState) {
                TextInputDialog(dialogState = dialogState)
            }
        }
    }
}

class SettingItem(
    val imageVector: ImageVector? = null,
    val onClick: () -> Unit = {},
    val titleString: String,
    val subTitleString: String,
    val hasSwitch: Boolean = false
)

@Composable
fun SettingsListUI(innerPadding: PaddingValues, settingsItems: List<SettingItem>) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxHeight()
            .padding(innerPadding)
    ) {

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        for (item in settingsItems) {

            item {

                SettingsItemUI(item = item)

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun SettingsItemUI(item: SettingItem) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .padding(start = 20.dp, end = 10.dp)
            .fillMaxWidth()
            .clickable { item.onClick() },
        shape = RoundedCornerShape(percent = 20)
    ) {

        Row(
            modifier = Modifier
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            if (item.hasSwitch) {
                var checked by remember { mutableStateOf(true) }

                Switch(
                    modifier = Modifier
                        .padding(end = 18.dp),
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    }
                )
            } else {

                Icon(
                    modifier = Modifier
                        .padding(end = 18.dp),
                    imageVector = item.imageVector!!,
                    contentDescription = ""
                )
            }

            Column {

                Text(
                    fontSize = 20.sp,
                    text = item.titleString,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = item.subTitleString,
                    style = MaterialTheme.typography.bodyMedium
                )

            }

        }
    }
}

@Composable
fun OptionsListDialog(dialogState: DialogUIState) {

    val state = dialogState as SettingsViewModel.ListDialogUIState

    Dialog(onDismissRequest = {
        state.onCancel()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        )
        {
            Text(
                modifier = Modifier.padding(18.dp),
                text = stringResource(dialogState.title),
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {

                val options = state.listOptions

                for ((index, option) in options.withIndex()) {

                    item(index) {

                        Row(
                            modifier = Modifier.padding(
                                start = 18.dp,
                                end = 18.dp,
                                top = 6.dp,
                                bottom = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            var selected by remember { mutableStateOf(false) }

                            RadioButton(selected = selected, onClick = {
                                selected = !selected
                            })

                            Text(
                                text = stringResource(id = option.text),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }
                }

                item {
                    Button(
                        onClick = { state.onSelected(0) },
                        modifier = Modifier.padding(bottom = 10.dp, start = 18.dp)
                    ) {
                        Text(
                            text = stringResource(id = state.positiveBtn),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun TextInputDialog(dialogState: DialogUIState) {

    val state = dialogState as SettingsViewModel.TextInputDialogUIState

    Dialog(onDismissRequest = {
        state.onCancel()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        )
        {
            Text(
                modifier = Modifier.padding(18.dp),
                text = stringResource(dialogState.title),
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                modifier = Modifier.padding(start = 18.dp, end = 18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                item {

                    PasswordFieldUI(label = stringResource(state.firstLabel),
                        hint = stringResource(state.firstHint),
                        initValue = { state.inputFirst }) { newText ->
                        state.inputFirst = newText
                    }

                    if (dialogState.hasSecondInput) {
                        PasswordFieldUI(label = stringResource(state.secondLabel),
                            hint = stringResource(state.secondHint),
                            initValue = { state.inputSecond }) { newText ->
                            state.inputSecond = newText
                        }
                    }

                    Button(
                        onClick = { state.onConfirm() },
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = state.positiveBtn),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsUIPreview() {
    SettingsUI(viewModel = SettingsViewModel())
}