package com.example.snaphunt.presentation.sign_in

import com.example.snaphunt.data.user.UserLogInData

data class SignInResult(
    val data: UserLogInData?,
    val errorMessage: String?
)

