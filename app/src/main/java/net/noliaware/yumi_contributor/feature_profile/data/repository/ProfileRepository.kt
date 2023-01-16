package net.noliaware.yumi_contributor.feature_profile.data.repository

import kotlinx.coroutines.flow.Flow
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.feature_profile.domain.model.BOSignIn
import net.noliaware.yumi_contributor.feature_profile.domain.model.UserProfile

interface ProfileRepository {
    fun getUserProfile(): Flow<Resource<UserProfile>>
    fun getBackOfficeSignInCode(): Flow<Resource<BOSignIn>>
}