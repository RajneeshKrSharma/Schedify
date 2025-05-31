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
            catchWrapper {  repository.saveCollaborator(
                collaboratorRequestDto = args) }

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class UpdateCollaboratorUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Collaborator>, CollaboratorRequestDto> {
    override suspend fun execute(args: CollaboratorRequestDto?): ApiResponseResource<Collaborator> {
        return (args?.let {
            catchWrapper { repository.updateCollaborator(
                collaboratorRequestDto = args) }

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class DeleteCollaboratorUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Collaborator>, CollaboratorRequestDto> {
    override suspend fun execute(args: CollaboratorRequestDto?): ApiResponseResource<Collaborator> {
        return (args?.let {
            catchWrapper { repository.deleteCollaborator(
                collaboratorRequestDto = args) }
        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}