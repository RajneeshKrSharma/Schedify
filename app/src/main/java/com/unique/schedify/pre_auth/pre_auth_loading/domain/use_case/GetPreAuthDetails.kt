package com.unique.schedify.pre_auth.pre_auth_loading.domain.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.pre_auth.pre_auth_loading.data.remote.dto.PreAuthInfoDto
import com.unique.schedify.pre_auth.pre_auth_loading.domain.repository.PreAuthRepository
import javax.inject.Inject

class GetPreAuthDetails @Inject constructor(
    private val repository: PreAuthRepository,
) : ApiUseCase<ApiResponseResource<PreAuthInfoDto>, Unit> {

    override suspend fun execute(args: Unit?): ApiResponseResource<PreAuthInfoDto> {
        return catchWrapper { repository.getPreAuthData() }
    }
}