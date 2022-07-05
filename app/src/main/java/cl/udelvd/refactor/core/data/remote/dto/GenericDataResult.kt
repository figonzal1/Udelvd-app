package cl.udelvd.refactor.core.data.remote.dto

import androidx.annotation.Keep

/**
 * {
 *   "type": ,
 *   "id": ,
 *   "attributes": {
 *       DTO<T>
 *    }
 * }
 */
@Keep
data class GenericDataResult<T>(
    val type: String,
    val id: String,
    val attributes: T
)