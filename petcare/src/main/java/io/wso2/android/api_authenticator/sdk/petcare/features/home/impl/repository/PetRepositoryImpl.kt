package io.wso2.android.api_authenticator.sdk.petcare.features.home.impl.repository

import io.wso2.android.api_authenticator.sdk.petcare.R
import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.models.Pet
import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.repository.PetRepository
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor() : PetRepository {
    override fun getPets(): List<Pet> = listOf(
        Pet(
            "Bella",
            R.drawable.pet_1,
            "Cat - Persian",
            "Next appointment on 29/04/24"
        ),
        Pet(
            "Charlie",
            R.drawable.pet_2,
            "Rabbit - Holland Lop",
            "Next appointment on 19/06/24"
        ),
        Pet(
            "Luna",
            R.drawable.pet_3,
            "Dog - Golden Retriever",
            "Next appointment on 04/05/24"
        ),
        Pet(
            "Max",
            R.drawable.pet_4,
            "Hamster - Syrian",
            "Next appointment on 01/06/24"
        ),
        Pet(
            "Oliver",
            R.drawable.pet_5,
            "Dog - Poddle",
            "Next appointment on 29/04/24"
        ),
        Pet(
            "Lucy",
            R.drawable.person_dog_login,
            "Dog - Beagle",
            "Next appointment on 05/08/24"
        )
    )
}
