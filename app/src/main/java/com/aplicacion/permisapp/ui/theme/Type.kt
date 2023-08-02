package com.aplicacion.permisapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aplicacion.permisapp.R

// Set of Material typography styles to start with
val poppinsFamily = FontFamily(
    Font(R.font.poppins, FontWeight.Normal)
)

val iniciarSesionTextStyle = TextStyle(
    fontFamily = poppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 38.sp,
    letterSpacing = 0.5.sp,

    )

val helpText = TextStyle(
    fontFamily = poppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    letterSpacing = 0.5.sp,
    color = gris3
)
val btnMenuText = TextStyle(
    fontFamily = poppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    letterSpacing = 0.5.sp,
    color = verde1
)
val helloTxt = TextStyle(
    fontFamily = poppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    letterSpacing = 0.5.sp,
    color = white
)
val userdefault = TextStyle(
    fontFamily = poppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    letterSpacing = 0.5.sp,
    color = white
)

val headerText = TextStyle(
    fontFamily = poppinsFamily,
    fontSize = 13.sp,
    letterSpacing = 0.5.sp,
)

val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Other default text styles to override

)