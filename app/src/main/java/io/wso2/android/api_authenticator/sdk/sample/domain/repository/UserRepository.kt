package io.wso2.android.api_authenticator.sdk.sample.domain.repository

import io.wso2.android.api_authenticator.sdk.sample.domain.model.UserDetails

interface UserRepository {
    suspend fun getUserDetails(accessToken: String): UserDetails?
}