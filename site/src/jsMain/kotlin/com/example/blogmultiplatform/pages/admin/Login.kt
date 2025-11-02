package com.example.blogmultiplatform.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.shared.JsTheme
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.navigation.admin_signup_route
import com.example.blogmultiplatform.styles.LoginInputStyle
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.Res
import com.example.blogmultiplatform.util.checkUserExistence
import com.example.blogmultiplatform.util.ensureProfileExists
import com.example.blogmultiplatform.util.noBorder
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.set

@Page
@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()
    var errorText by remember { mutableStateOf(" ") }
    // New: role selection
    var selectedRole by remember { mutableStateOf("client") }
    // Show/hide password
    var showPassword by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().styleModifier { property("min-height", "100vh"); property("display","flex"); property("align-items","center"); property("justify-content","center") }, contentAlignment = Alignment.Center) {
        // Centered form card (auto width, constrained by max-width so it wraps content)
        Box(modifier = Modifier.styleModifier { property("width", "auto"); property("max-width","900px"); property("background", "linear-gradient(180deg,#ffffff, #fbfbff)"); property("border-radius","14px"); property("box-shadow","0 20px 50px rgba(16,24,40,0.08)"); property("padding","28px"); property("margin","0 auto") }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.styleModifier { property("width","100%") }) {
                // Logo + title
                Image(modifier = Modifier.margin(bottom = 8.px).width(84.px), src = Res.Image.logo, alt = "Logo")
                SpanText(modifier = Modifier.fontFamily(FONT_FAMILY).fontSize(20.px).fontWeight(FontWeight.Bold).margin(bottom = 18.px), text = "Welcome back")

                // Inputs
                // container ensures inputs stretch to fit card width

                Input(
                    type = InputType.Text,
                    attrs = LoginInputStyle.toModifier()
                        .id(Id.usernameInput)
                        .margin(bottom = 12.px)
                        .width(100.percent)
                        .height(52.px)
                        .padding(leftRight = 18.px)
                        .styleModifier { property("border-radius","10px"); property("box-shadow","inset 0 1px 2px rgba(16,24,40,0.04)") }
                        .backgroundColor(Colors.White)
                        .fontFamily(FONT_FAMILY)
                        .fontSize(14.px)
                        .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                        .toAttrs { attr("placeholder", "Username") }
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.margin(bottom = 12.px)) {
                    val pwType = if (showPassword) InputType.Text else InputType.Password
                    Input(
                        type = pwType,
                        attrs = LoginInputStyle.toModifier()
                            .id(Id.passwordInput)
                            .width(100.percent)
                            .height(52.px)
                            .padding(leftRight = 18.px)
                            .styleModifier { property("border-radius","10px"); property("box-shadow","inset 0 1px 2px rgba(16,24,40,0.04)") }
                            .backgroundColor(Colors.White)
                            .fontFamily(FONT_FAMILY)
                            .fontSize(14.px)
                            .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                            .toAttrs { attr("placeholder", "Password") }
                    )
                    Box(modifier = Modifier.width(8.px))
                    Button(attrs = Modifier.onClick { showPassword = !showPassword }.toAttrs {
                        attr("style", "background:transparent; border:none; color:${JsTheme.Primary.rgb}; cursor:pointer; padding:6px; display:flex; align-items:center; justify-content:center;")
                    }) {
                        Box(modifier = Modifier.id("loginPwIcon").width(18.px).height(18.px))
                    }
                }

                // Role toggle pills
                Row(modifier = Modifier.margin(bottom = 16.px), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Button(attrs = { onClick { selectedRole = "client" }; attr("style","background:${if (selectedRole=="client") JsTheme.Primary.rgb else "transparent"}; color:${if (selectedRole=="client") "white" else JsTheme.Primary.rgb}; border:1px solid ${JsTheme.Primary.rgb}; padding:8px 14px; border-radius:999px; margin-right:8px;") }) { SpanText(text = "Client") }
                    Button(attrs = { onClick { selectedRole = "developer" }; attr("style","background:${if (selectedRole=="developer") JsTheme.Primary.rgb else "transparent"}; color:${if (selectedRole=="developer") "white" else JsTheme.Primary.rgb}; border:1px solid ${JsTheme.Primary.rgb}; padding:8px 14px; border-radius:999px;") }) { SpanText(text = "Developer") }
                }

                // Sign in button
                Button(attrs = Modifier
                    .margin(bottom = 12.px)
                    .width(100.percent)
                    .height(50.px)
                    .backgroundColor(JsTheme.Primary.rgb)
                    .color(Colors.White)
                    .borderRadius(r = 8.px)
                    .fontFamily(FONT_FAMILY)
                    .fontWeight(FontWeight.Medium)
                    .fontSize(15.px)
                    .noBorder()
                    .cursor(Cursor.Pointer)
                    .onClick {
                        scope.launch {
                            val username = (document.getElementById(Id.usernameInput) as HTMLInputElement).value
                            val password = (document.getElementById(Id.passwordInput) as HTMLInputElement).value
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                val user = checkUserExistence(user = User(username = username, password = password, role = selectedRole))
                                if (user != null) {
                                    rememberLoggedIn(remember = true, user = user)
                                    // Ensure profile exists for this user and populate profile-related localStorage
                                    try {
                                        val profile = ensureProfileExists(user.username)
                                        if (profile != null) {
                                            localStorage.setItem("displayName", profile.displayName ?: "")
                                            localStorage.setItem("bio", profile.bio ?: "")
                                            localStorage.setItem("avatarUrl", profile.avatarUrl ?: "")
                                            localStorage.setItem("role", profile.role ?: user.role)
                                        }
                                    } catch (_: Throwable) {
                                        // ignore network/profile errors
                                    }
                                    // If the logged-in user is a developer, send them to the public homepage.
                                    // Keep existing behavior for clients (no change).
                                    if (user.role == "developer") {
                                        context.router.navigateTo(Screen.HomePage.route)
                                    } else {
                                        context.router.navigateTo(Screen.AdminHome.route)
                                    }
                                } else {
                                    errorText = "The user doesn't exist."
                                    delay(3000)
                                    errorText = " "
                                }
                            } else {
                                errorText = "Input fields are empty."
                                delay(3000)
                                errorText = " "
                            }
                        }
                    }
                    .toAttrs()) {
                    SpanText(text = "Sign in")
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    SpanText(modifier = Modifier.color(Colors.Gray), text = "Don't have account? ")
                    SpanText(modifier = Modifier.color(JsTheme.Primary.rgb).fontWeight(FontWeight.Bold).cursor(Cursor.Pointer).onClick { context.router.navigateTo(admin_signup_route) }, text = "Create Account")
                }

                SpanText(modifier = Modifier.margin(top = 12.px).fontFamily(FONT_FAMILY).color(Colors.Red).textAlign(TextAlign.Center), text = errorText)
            }
        }
    }

    // Inject SVG icon & animate on toggle for password show/hide
    LaunchedEffect(showPassword) {
        try {
            val id = "loginPwIcon"
            val el = document.getElementById(id)
            if (el != null) {
                val svgOpen = """
                    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z" stroke="${JsTheme.Primary.rgb}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                      <circle cx="12" cy="12" r="3" stroke="${JsTheme.Primary.rgb}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                """.trimIndent()
                val svgOff = """
                    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a21.38 21.38 0 0 1 5.06-4.94" stroke="${JsTheme.Primary.rgb}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M1 1l22 22" stroke="${JsTheme.Primary.rgb}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                """.trimIndent()
                el.innerHTML = if (showPassword) svgOff else svgOpen
                try {
                    val ed = el.asDynamic().style
                    ed.transition = "transform 160ms ease"
                    ed.transform = "scale(1.12)"
                    window.setTimeout({ ed.transform = "scale(1)" }, 160)
                } catch (_: Throwable) {}
            }
        } catch (_: Throwable) {}
    }
}

private fun rememberLoggedIn(
    remember: Boolean,
    user: UserWithoutPassword? = null
) {
    localStorage["remember"] = remember.toString()
    if (user != null) {
        localStorage["userId"] = user._id
        localStorage["username"] = user.username
        localStorage["role"] = user.role
        // Also persist profile fields so Profile page displays the logged-in user's profile
        // UserWithoutPassword may include displayName and avatarUrl; store them (or empty) to avoid showing stale data.
        try {
            localStorage.setItem("displayName", user.displayName ?: "")
            // bio may not be included in UserWithoutPassword; default to empty
            localStorage.setItem("bio", "")
            localStorage.setItem("avatarUrl", user.avatarUrl ?: "")
        } catch (_: Throwable) {
            // ignore storage errors
        }
    }
}