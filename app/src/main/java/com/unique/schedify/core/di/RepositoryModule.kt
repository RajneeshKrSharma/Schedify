package com.unique.schedify.core.di

import com.unique.schedify.auth.login.data.repository.LoginRepository
import com.unique.schedify.auth.login.domain.repository.LoginRepositoryImpl
import com.unique.schedify.post_auth.split_expense.data.repository.SplitExpenseRepositoryImpl
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import com.unique.schedify.pre_auth.pre_auth_loading.data.repository.PreAuthRepositoryImpl
import com.unique.schedify.pre_auth.pre_auth_loading.domain.repository.PreAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreAuthRepository(
        preRepositoryImpl: PreAuthRepositoryImpl
    ): PreAuthRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindSplitExpenseRepository(
        splitExpenseRepository: SplitExpenseRepositoryImpl
    ): SplitExpenseRepository


}