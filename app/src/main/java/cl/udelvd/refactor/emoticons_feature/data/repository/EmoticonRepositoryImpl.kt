package cl.udelvd.refactor.emoticons_feature.data.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.emoticons_feature.data.remote.EmoticonRemoteDataSource
import cl.udelvd.refactor.emoticons_feature.domain.model.Emoticon
import cl.udelvd.refactor.emoticons_feature.domain.repository.EmoticonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class EmoticonRepositoryImpl(
    private val emoticonRemoteData: EmoticonRemoteDataSource
) : EmoticonRepository {

    override fun getEmoticons(
        authToken: String,
        language: String
    ): Flow<StatusAPI<List<Emoticon>>> = flow {

        emit(StatusAPI.Loading())

        try {

            val emoticons =
                emoticonRemoteData.getEmoticons(authToken, language)
                    ?.map { it.attributes.toDomain() }


            when {
                !emoticons.isNullOrEmpty() -> emit(StatusAPI.Success(emoticons))
            }

        } catch (e: HttpException) {
            Timber.e(e.message())
        } catch (e: IOException) {
            Timber.e(e.message)
        }
    }.flowOn(Dispatchers.IO)
}