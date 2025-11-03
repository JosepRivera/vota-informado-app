package com.rivera.votainformado.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rivera.votainformado.ui.theme.InstitutionalBlue
import com.rivera.votainformado.ui.theme.NeutralMedium
import com.rivera.votainformado.ui.theme.NeutralWhite

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            val navItems = listOf(
                Triple("home", Icons.Outlined.Home, "Inicio"),
                Triple("resultados", Icons.Outlined.Poll, "Resultados"),
                Triple("votar", Icons.Outlined.HowToVote, "Votar"),
                Triple("comparar", Icons.AutoMirrored.Outlined.CompareArrows, "Comparar")
            )

            navItems.forEach { (route, icon, label) ->
                val selected = currentRoute == route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(route) },
                    modifier = Modifier.padding(vertical = 4.dp),
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(if (selected) 48.dp else 40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .then(
                                    if (selected)
                                        Modifier.background(
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    InstitutionalBlue,
                                                    InstitutionalBlue.copy(alpha = 0.85f)
                                                )
                                            )
                                        )
                                    else Modifier.background(Color.Transparent)
                                ),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) Color.White else NeutralMedium,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (selected) InstitutionalBlue else NeutralMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = InstitutionalBlue,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = NeutralMedium,
                        unselectedTextColor = NeutralMedium
                    )
                )
            }
        }
    }
}

