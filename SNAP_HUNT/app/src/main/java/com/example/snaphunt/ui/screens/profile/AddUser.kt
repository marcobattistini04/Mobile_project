package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState


@Composable
fun AddUser(navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    Scaffold(
        topBar = { AppBar(title = "Add User", navigationController) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {/*TO DO*/}
            ) {
                Icon(Icons.Outlined.Check, "Add User")
            }
        },
    ) {
            contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { /*TO DO*/},
                label = { Text("NickName")},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {/*TO DO*/}) {
                        Icon(Icons.Outlined.MyLocation, "Current Location")
                    }
                }
            )
            OutlinedTextField(
                value = "",
                onValueChange = {/*TO DO*/},
                label = {Text("Password")},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {/* TO DO */},
                label = { Text("Confirm Password")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(24.dp))
        }
    }
}