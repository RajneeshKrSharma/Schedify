package com.unique.schedify.pre_auth.pre_auth_loading.data.repository

import com.unique.schedify.pre_auth.pre_auth_loading.data.remote.PreAuthApis
import com.unique.schedify.pre_auth.pre_auth_loading.data.remote.dto.PreAuthInfoDto
import com.unique.schedify.pre_auth.pre_auth_loading.domain.repository.PreAuthRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreAuthRepositoryImpl @Inject constructor(
    private val api: PreAuthApis,
): PreAuthRepository {
    override suspend fun getPreAuthData(): Response<PreAuthInfoDto> {
        return api.getPreAppDetails()
    }
}