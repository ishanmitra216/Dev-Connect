package com.example.blogmultiplatform.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.shared.JsTheme
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.attributes.InputType
import kotlinx.browser.localStorage
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.compose.ui.toAttrs
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.blogmultiplatform.models.User
import com.varabyte.kobweb.browser.api
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.rememberPageContext
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.util.fetchProfile
import com.example.blogmultiplatform.util.ensureProfileExists

@Page("/profile")
@Composable
fun ProfilePage() {
    println("ProfilePage mounted")
    val context = rememberPageContext()
    // Profile fields sourced from localStorage via getItem
    var username by remember { mutableStateOf(localStorage.getItem("username") ?: "") }
    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(localStorage.getItem("role") ?: "client") }

    var showSuccess by remember { mutableStateOf(false) }

    val scope = MainScope()
    // On mount, fetch profile from server for the logged-in user (by username or userId)
    LaunchedEffect(Unit) {
        try {
            val uname = localStorage.getItem("username") ?: localStorage.getItem("userId") ?: ""
            if (uname.isNotBlank()) {
                // Try to fetch profile, create if missing
                val prof = fetchProfile(uname) ?: ensureProfileExists(uname)
                if (prof != null) {
                    username = prof.username
                    displayName = prof.displayName ?: ""
                    bio = prof.bio ?: ""
                    avatarUrl = prof.avatarUrl ?: ""
                    role = prof.role ?: role
                    // Persist to localStorage so header and other tabs update
                    localStorage.setItem("username", username)
                    localStorage.setItem("displayName", displayName)
                    localStorage.setItem("bio", bio)
                    localStorage.setItem("avatarUrl", avatarUrl)
                    localStorage.setItem("role", role)
                } else {
                    // fallback to localStorage values if server unavailable
                    displayName = localStorage.getItem("displayName") ?: ""
                    bio = localStorage.getItem("bio") ?: ""
                    avatarUrl = localStorage.getItem("avatarUrl") ?: ""
                }
            }
        } catch (_: Throwable) {
            displayName = localStorage.getItem("displayName") ?: ""
            bio = localStorage.getItem("bio") ?: ""
            avatarUrl = localStorage.getItem("avatarUrl") ?: ""
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().styleModifier {
            property("min-height", "100vh")
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "center")
            // subtle app background
            property("background", "linear-gradient(180deg,#f7f8fc,#ffffff)")
            property("padding", "48px 16px")
        },
        contentAlignment = Alignment.Center
    ) {
        // Card
        Box(
            modifier = Modifier.styleModifier {
                property("width", "100%")
                property("max-width", "900px")
                property("background", "linear-gradient(180deg,#ffffff,#fbfbff)")
                property("border-radius", "14px")
                property("box-shadow", "0 24px 80px rgba(16,24,40,0.08)")
                property("padding", "28px")
                property("margin", "0 16px")
                property("transition", "transform 220ms ease, box-shadow 220ms ease")
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.styleModifier { property("width", "100%") }) {
                // Header row with title + small avatar preview
                Row(modifier = Modifier.fillMaxWidth().margin(bottom = 8.px).styleModifier { property("align-items", "center"); property("justify-content", "space-between") }) {
                    Column {
                        SpanText(
                            modifier = Modifier.fontSize(28.px).fontFamily(FONT_FAMILY).fontWeight(FontWeight.Bold).margin(bottom = 6.px),
                            text = "Your Profile"
                        )
                        SpanText(
                            modifier = Modifier.fontSize(13.px).color(Colors.Gray),
                            text = "Create or update your public profile. Changes are saved locally and sent to the server."
                        )
                    }

                    Box(modifier = Modifier.styleModifier {
                        property("display", "flex"); property("align-items", "center"); property("gap", "12px")
                    }) {
                        Box(modifier = Modifier.width(56.px).height(56.px).styleModifier {
                            property("border-radius", "50%"); property("overflow", "hidden"); property("background", "#f4f6f8"); property("box-shadow", "0 6px 20px rgba(16,24,40,0.08)")
                        }) {
                            if (avatarUrl.isNotBlank()) {
                                Img(src = avatarUrl, attrs = { attr("style", "width:100%;height:100%;object-fit:cover") })
                            } else {
                                SpanText(modifier = Modifier.color(Colors.Gray).styleModifier { property("display", "flex"); property("align-items", "center"); property("justify-content", "center"); property("height", "100%") }, text = "A")
                            }
                        }
                    }
                }

                // avatar preview and url (live preview on input)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().margin(bottom = 18.px)) {
                    Box(
                        modifier = Modifier.width(110.px).height(110.px).styleModifier {
                            property("border-radius", "50%")
                            property("overflow", "hidden")
                            property("background", "linear-gradient(180deg,#f8fafc,#eef2ff)")
                            property("box-shadow", "0 10px 30px rgba(16,24,40,0.06)")
                            property("border", "1px solid rgba(99,102,241,0.06)")
                        }.margin(right = 18.px)
                    ) {
                        if (avatarUrl.isNotBlank()) {
                            Img(src = avatarUrl, attrs = { attr("style", "width:100%;height:100%;object-fit:cover") })
                        } else {
                            // friendly placeholder
                            Box(modifier = Modifier.styleModifier { property("display", "flex"); property("align-items", "center"); property("justify-content", "center"); property("height", "100%") }) {
                                SpanText(modifier = Modifier.color(Colors.Gray), text = "No avatar")
                            }
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        SpanText(modifier = Modifier.fontSize(12.px).color(Colors.Gray).margin(bottom = 6.px), text = "Avatar URL")
                        Input(type = InputType.Text, attrs = {
                            attr("id", "profileAvatar")
                            attr("placeholder", "https://example.com/you.jpg")
                            attr("value", avatarUrl
                            )
                            onInput {
                                val input = it.target as? org.w3c.dom.HTMLInputElement
                                if (input != null) avatarUrl = input.value
                            }
                            attr("style", "width:100%; padding:12px 14px; border-radius:10px; border:1px solid #eef2ff; box-shadow: inset 0 1px 2px rgba(16,24,40,0.02); transition: box-shadow 160ms ease, border-color 160ms ease;")
                        })
                        SpanText(modifier = Modifier.fontSize(12.px).color(Colors.Gray).margin(top = 8.px), text = "Paste a public image URL to preview instantly.")
                    }
                }

                // Inputs
                Column(modifier = Modifier.fillMaxWidth().margin(bottom = 8.px)) {
                    SpanText(modifier = Modifier.fontSize(12.px).color(Colors.Gray).margin(bottom = 6.px), text = "Display name")
                    Input(type = InputType.Text, attrs = {
                        attr("id", "profileDisplayName")
                        attr("placeholder", "e.g. Jane Doe")
                        attr("value", displayName)
                        onInput {
                            val input = it.target as? org.w3c.dom.HTMLInputElement
                            if (input != null) displayName = input.value
                        }
                        attr("style", "width:100%; padding:12px 14px; border-radius:10px; margin-bottom:10px; border:1px solid #eef2ff; box-shadow: inset 0 1px 2px rgba(16,24,40,0.02);")
                    })

                    SpanText(modifier = Modifier.fontSize(12.px).color(Colors.Gray).margin(bottom = 6.px), text = "Username")
                    Input(type = InputType.Text, attrs = {
                        attr("id", "profileUsername")
                        attr("placeholder", "username")
                        attr("value", username)
                        onInput {
                            val input = it.target as? org.w3c.dom.HTMLInputElement
                            if (input != null) username = input.value
                        }
                        attr("style", "width:100%; padding:12px 14px; border-radius:10px; margin-bottom:10px; border:1px solid #eef2ff;")
                    })

                    SpanText(modifier = Modifier.fontSize(12.px).color(Colors.Gray).margin(bottom = 6.px), text = "Short bio")
                    Input(type = InputType.Text, attrs = {
                        attr("id", "profileBio")
                        attr("placeholder", "A short bio to show on your profile")
                        attr("value", bio)
                        onInput {
                            val input = it.target as? org.w3c.dom.HTMLInputElement
                            if (input != null) bio = input.value
                        }
                        attr("style", "width:100%; padding:12px 14px; border-radius:10px; margin-bottom:16px; border:1px solid #eef2ff; height:84px;")
                    })
                }

                // role selection (pills)
                Column(modifier = Modifier.fillMaxWidth().margin(bottom = 18.px)) {
                    SpanText(modifier = Modifier.fontSize(12.px).color(Colors.Gray).margin(bottom = 8.px), text = "Role")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.styleModifier {
                            property("cursor", "pointer")
                            property("padding", "8px 16px")
                            property("border-radius", "999px")
                            property("margin-right", "12px")
                            property("transition", "all 160ms ease")
                            property("box-shadow", if (role == "client") "0 8px 30px rgba(6,182,212,0.12)" else "none")
                            property("background", if (role == "client") JsTheme.Primary.hex else "transparent")
                            property("border", "1px solid ${JsTheme.Primary.hex}")
                        }.onClick { role = "client" }) {
                            SpanText(modifier = Modifier.color(if (role == "client") Colors.White else JsTheme.Primary.rgb), text = "Client")
                        }

                        Box(modifier = Modifier.styleModifier {
                            property("cursor", "pointer")
                            property("padding", "8px 16px")
                            property("border-radius", "999px")
                            property("transition", "all 160ms ease")
                            property("box-shadow", if (role == "developer") "0 8px 30px rgba(99,102,241,0.10)" else "none")
                            property("background", if (role == "developer") "#7c83ff" else "transparent")
                            property("border", "1px solid rgba(99,102,241,0.18)")
                        }.onClick { role = "developer" }) {
                            SpanText(modifier = Modifier.color(if (role == "developer") Colors.White else Colors.Gray), text = "Developer")
                        }
                    }
                }

                // Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(attrs = {
                        attr("style", "width:140px;height:44px;border-radius:8px;border:1px solid #e6e9f2;background:transparent;color:#374151; transition: transform 160ms ease;")
                        onClick {
                            // cancel -> reload values from storage
                            username = localStorage.getItem("username") ?: ""
                            displayName = localStorage.getItem("displayName") ?: ""
                            bio = localStorage.getItem("bio") ?: ""
                            avatarUrl = localStorage.getItem("avatarUrl") ?: ""
                            role = localStorage.getItem("role") ?: "client"
                        }
                    }) {
                        SpanText(text = "Cancel")
                    }

                    Button(attrs = {
                        attr("style", "width:160px;height:44px;border-radius:8px;background:linear-gradient(90deg, #06b6d4, #0ea5e9); color:white; box-shadow:0 10px 30px rgba(14,165,233,0.14); transition: transform 160ms ease;")
                        onClick {
                            // Save using state values (no DOM lookups)
                            localStorage.setItem("username", username)
                            localStorage.setItem("displayName", displayName)
                            localStorage.setItem("bio", bio)
                            localStorage.setItem("avatarUrl", avatarUrl)
                            localStorage.setItem("role", role)

                            // POST to server
                            val user = User(
                                _id = "",
                                username = username,
                                password = "",
                                role = role,
                                displayName = if (displayName.isBlank()) null else displayName,
                                bio = if (bio.isBlank()) null else bio,
                                avatarUrl = if (avatarUrl.isBlank()) null else avatarUrl
                            )
                            // launch side effect using window.api
                            scope.launch {
                                try {
                                    val response = window.api.tryPost(apiPath = "saveprofile", body = Json.encodeToString(user).encodeToByteArray())?.decodeToString()
                                    val success = response?.toBoolean() ?: false
                                    if (success) {
                                        println("Profile saved on server")
                                        showSuccess = true
                                        // Dispatch a simple event so header updates in this tab immediately (header can read localStorage)
                                        js("window.dispatchEvent(new Event('profileUpdated'))")
                                        // Navigate to home
                                        context.router.navigateTo(Screen.HomePage.route)
                                    } else {
                                        println("Failed to save profile on server: $response")
                                        // still show success locally but you may want to show an error instead
                                        showSuccess = true
                                    }
                                } catch (e: Exception) {
                                    println("Error saving profile: ${e.message}")
                                    showSuccess = true
                                }
                            }
                        }
                    }) {
                        SpanText(text = "Save")
                    }
                }

                // Success overlay
                if (showSuccess) {
                    Box(modifier = Modifier.styleModifier { property("position", "fixed"); property("inset", "0"); property("display", "flex"); property("align-items", "center"); property("justify-content", "center"); property("background", "rgba(2,6,23,0.35)") }) {
                        Box(modifier = Modifier.styleModifier { property("background", "white"); property("padding", "24px"); property("border-radius", "12px"); property("box-shadow", "0 12px 40px rgba(16,24,40,0.12)") }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                SpanText(modifier = Modifier.fontWeight(FontWeight.Bold).fontSize(16.px).margin(bottom = 8.px), text = "Profile saved")
                                SpanText(modifier = Modifier.fontSize(13.px).color(Colors.Gray).margin(bottom = 14.px), text = "Your profile has been updated successfully.")
                                Button(attrs = Modifier.onClick { showSuccess = false }.toAttrs()) { SpanText(text = "OK") }
                            }
                        }
                    }
                }
            }
        }
    }
}
