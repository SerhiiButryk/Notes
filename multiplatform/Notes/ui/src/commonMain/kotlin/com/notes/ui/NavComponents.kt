package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
@Composable
fun createNavBackStack(default: NavKey, vararg elements: KClass<out NavKey>): NavBackStack<NavKey> {
    val navConfig = remember {
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    for (element in elements) {
                        @Suppress("UNCHECKED_CAST")
                        subclass(element as KClass<NavKey>, element.serializer())
                    }
                }
            }
        }
    }
    return rememberNavBackStack(navConfig, default)
}