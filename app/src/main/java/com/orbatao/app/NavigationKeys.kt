package com.orbatao.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Login : NavKey
@Serializable data object PhoneAuth : NavKey
@Serializable data object Main : NavKey
@Serializable data object Profile : NavKey
