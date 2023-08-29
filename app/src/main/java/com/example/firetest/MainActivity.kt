package com.example.firetest

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.firetest.databinding.ActivityMainBinding
import com.example.firetest.ui.theme.EmailLoginScreen
import com.example.firetest.ui.theme.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import java.lang.reflect.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaseScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(loginViewModel: LoginViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val backstackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = NavigationEnum.fromRoute(backstackEntry.value?.destination?.route, loginViewModel.isLoggedIn)
    Scaffold(
        topBar = { TopBar(navController, currentScreen) }
    ) {
        NavigateBetweenScreen(navController)
    }
}

@Composable
fun TopBar(navController: NavHostController, currentScreen: NavigationEnum) {
    TopAppBar(
        title = { Text(text = stringResource(currentScreen.title)) },
        // Avoid going back to previous screen after login/logout click
        navigationIcon = {
            if (currentScreen != NavigationEnum.Welcome
                && currentScreen != NavigationEnum.Login
            ) {
                NavigateBackButton(navController)
            }
        }
    )
}

@Composable
fun NavigateBackButton(navController: NavHostController) {
    IconButton(onClick = { navController.popBackStack() },
        modifier = Modifier.semantics { contentDescription = "back button" }
    ) {
        Icon(Icons.Filled.ArrowBack, stringResource(R.string.back_icon))
    }
}

@Composable
fun NavigateBetweenScreen(navController: NavHostController, loginViewModel: LoginViewModel = hiltViewModel()) {
    val startDestination = if (loginViewModel.isLoggedIn.value) NavigationEnum.Welcome.name else NavigationEnum.Login.name

    NavHost(navController = navController, startDestination = startDestination) {
        loginPage(this, navController, loginViewModel)
        emailLoginPage(this, loginViewModel)
        welcomePage(this, loginViewModel)
    }
}

fun loginPage(builder: NavGraphBuilder, navController: NavHostController, loginViewModel: LoginViewModel) {
    builder.composable(route = NavigationEnum.Login.name) {
        LoginScreen(
            emailLoginClick = { navController.navigate(NavigationEnum.EmailLogin.name) },
            viewModel = loginViewModel
        )
    }
}

fun emailLoginPage(builder: NavGraphBuilder, loginViewModel: LoginViewModel) {
    builder.composable(route = NavigationEnum.EmailLogin.name) {
        EmailLoginScreen(loginViewModel)
    }
}

//fun welcomePage(builder: NavGraphBuilder, loginViewModel: LoginViewModel) {
//    builder.composable(route = NavigationEnum.Welcome.name) {
//        WelcomeScreen(loginViewModel)
//    }
//}