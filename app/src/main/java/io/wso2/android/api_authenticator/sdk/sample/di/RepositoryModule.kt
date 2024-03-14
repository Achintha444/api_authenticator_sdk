package io.wso2.android.api_authenticator.sdk.sample.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.wso2.android.api_authenticator.sdk.sample.data.impl.repository.AuthenticationProviderRepositoryImpl
import io.wso2.android.api_authenticator.sdk.sample.data.impl.repository.AuthenticationRepositoryImpl
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

    @Binds
    @Singleton
    abstract fun bindAuthenticationProviderRepository(
        authenticationProviderRepositoryImpl: AuthenticationProviderRepositoryImpl
    ): AuthenticationProviderRepository
}