package com.unique.schedify.post_auth.split_expense.domain.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto.Collaborator
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import javax.inject.Inject

class SaveCollaboratorUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<GroupExpenseResponseDto.Collaborator>, CollaboratorRequestDto> {
    override suspend fun execute(args: CollaboratorRequestDto?): ApiResponseResource<Collaborator> {
        return (args?.let {
            val result = repository.saveCollaborator(
                collaboratorRequestDto = args)
            (if (result.isSuccessful) {
                result.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: ApiResponseResource.Error("")

            } else {
                ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
            })

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class UpdateCollaboratorUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Collaborator>, CollaboratorRequestDto> {
    override suspend fun execute(args: CollaboratorRequestDto?): ApiResponseResource<Collaborator> {
        return (args?.let {
            val result = repository.updateCollaborator(
                collaboratorRequestDto = args)
            (if (result.isSuccessful) {
                result.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: ApiResponseResource.Error("")

            } else {
                ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
            })

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class DeleteCollaboratorUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Collaborator>, CollaboratorRequestDto> {
    override suspend fun execute(args: CollaboratorRequestDto?): ApiResponseResource<Collaborator> {
        return (args?.let {
            val result = repository.deleteCollaborator(
                collaboratorRequestDto = args)
            (if (result.isSuccessful) {
                result.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: ApiResponseResource.Error("")

            } else {
                ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
            })

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}