package com.orbatao.app.ui.login

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

@Composable
fun PhoneAuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var phoneNumber by remember { mutableStateOf("+91") }
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    val callbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-retrieval or instant validation succeeded
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onAuthSuccess()
                        } else {
                            Toast.makeText(context, "Sign in failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isSendingOtp = false
                Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationIdSent: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                isSendingOtp = false
                verificationId = verificationIdSent
                resendToken = token
                Toast.makeText(context, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Phone Sign-In",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (verificationId.isEmpty()) "Enter your phone number with country code (+91)" else "Enter the OTP code sent to your phone",
                fontSize = 14.sp,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (verificationId.isEmpty()) {
                // Phone Number input
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number", color = Color(0xFFECEFF1)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF81D4FA),
                        unfocusedBorderColor = Color(0xFF90A4AE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length >= 10) {
                            isSendingOtp = true
                            val options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(context as Activity)
                                .setCallbacks(callbacks)
                                .build()
                            PhoneAuthProvider.verifyPhoneNumber(options)
                        } else {
                            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isSendingOtp,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81D4FA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isSendingOtp) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Send OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // OTP Code input
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text("OTP Code", color = Color(0xFFECEFF1)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF81D4FA),
                        unfocusedBorderColor = Color(0xFF90A4AE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otpCode.length == 6) {
                            isVerifying = true
                            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    isVerifying = false
                                    if (task.isSuccessful) {
                                        onAuthSuccess()
                                    } else {
                                        Toast.makeText(context, "Invalid OTP: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isVerifying,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81D4FA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Verify OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        verificationId = ""
                        otpCode = ""
                    }
                ) {
                    Text("Change Phone Number", color = Color(0xFF81D4FA))
                }
            }
        }
    }
}
