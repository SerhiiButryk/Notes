/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    ) {  innerPadding ->

        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        val settingsItems = uiState.value.items

        SettingsListUI(innerPadding, settingsItems)
    }
}

class SettingItem(val imageVector: ImageVector,
                  val onClick: () -> Unit,
                  val titleString: String,
                  val subTitleString: String)

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
            .fillMaxWidth(),
        shape = RoundedCornerShape(percent = 20)
    ) {

        Row(
            modifier = Modifier
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Icon(
                modifier = Modifier
                    .padding(end = 18.dp),
                imageVector = item.imageVector,
                contentDescription = ""
            )

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

@Preview(showBackground = true)
@Composable
private fun SettingsUIPreview() {
    SettingsUI(viewModel = SettingsViewModel())
}