package com.orbatao.app

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.google.firebase.auth.FirebaseAuth
import com.orbatao.app.ui.login.LoginScreen
import com.orbatao.app.ui.login.PhoneAuthScreen
import com.orbatao.app.ui.main.MainScreen
import com.orbatao.app.ui.profile.ProfileScreen

@Composable
fun MainNavigation() {
  val currentUser = FirebaseAuth.getInstance().currentUser
  val startDestination = if (currentUser != null) Main else Login
  val backStack = rememberNavBackStack(startDestination)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Login> {
          LoginScreen(
            onLoginSuccess = {
              backStack.add(Main)
            },
            onPhoneClick = {
              backStack.add(PhoneAuth)
            }
          )
        }
        entry<PhoneAuth> {
          PhoneAuthScreen(
            onAuthSuccess = {
              backStack.add(Main)
            }
          )
        }
        entry<Main> {
          MainScreen(
            onProfileClick = {
              backStack.add(Profile)
            },
            onLogout = {
              backStack.add(Login)
            }
          )
        }
        entry<Profile> {
          ProfileScreen(
            onBackClick = {
              backStack.removeLastOrNull()
            },
            onLogout = {
              backStack.add(Login)
            }
          )
        }
      },
  )
}
