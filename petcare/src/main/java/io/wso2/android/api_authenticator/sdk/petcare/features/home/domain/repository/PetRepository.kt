package io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.repository

import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.models.Pet

interface PetRepository {
    fun getPets(): List<Pet>
}
