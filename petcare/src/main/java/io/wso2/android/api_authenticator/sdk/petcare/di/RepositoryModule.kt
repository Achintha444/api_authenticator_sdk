package io.wso2.android.api_authenticator.sdk.petcare.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.wso2.android.api_authenticator.sdk.petcare.features.login.domain.repository.AsgardeoAuthRepository
import io.wso2.android.api_authenticator.sdk.petcare.features.login.impl.repository.AsgardeoAuthRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAsgardeoAuthRepository(
        asgardeoAuthRepositoryImpl: AsgardeoAuthRepositoryImpl
    ): AsgardeoAuthRepository
}
