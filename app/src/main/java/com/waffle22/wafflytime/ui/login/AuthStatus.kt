package com.waffle22.wafflytime.ui.login

enum class LoginStatus {
    StandBy, LoginOk, LoginFailed, Error_500 , Corruption
}

enum class SignUpStatus {
    StandBy, SignUpOk, SignUpConflict, Error_500, Corruption
}

enum class SignUpEmailStatus {
    StandBy, RequestOk, VerifyOk, BadRequest, Conflict, Error_500, Corruption
}