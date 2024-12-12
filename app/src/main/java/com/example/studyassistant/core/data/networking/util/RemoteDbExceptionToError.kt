package com.example.studyassistant.core.data.networking.util

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.google.firebase.firestore.FirebaseFirestoreException

//fun mapFirestoreExceptionToError(exception: Exception): Result.Error<RemoteDbError> {
//    return when (exception) {
//        is FirebaseFirestoreException -> {
//            when (exception.code) {
//                FirebaseFirestoreException.Code.CANCELLED -> Result.Error(RemoteDbError.OPERATION_CANCELLED)
//                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(RemoteDbError.UNKNOWN_ERROR)
//                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(RemoteDbError.INVALID_ARGUMENT)
//                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(RemoteDbError.TIMEOUT)
//                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(RemoteDbError.DOCUMENT_NOT_FOUND)
//                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(RemoteDbError.DUPLICATE_DOCUMENT)
//                FirebaseFirestoreException.Code.PERMISSION_DENIED -> Result.Error(RemoteDbError.PERMISSION_DENIED)
//                FirebaseFirestoreException.Code.UNAUTHENTICATED -> Result.Error(RemoteDbError.UNAUTHENTICATED)
//                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(RemoteDbError.RESOURCE_EXHAUSTED)
//                FirebaseFirestoreException.Code.FAILED_PRECONDITION -> Result.Error(RemoteDbError.FAILED_PRECONDITION)
//                FirebaseFirestoreException.Code.ABORTED -> Result.Error(RemoteDbError.OPERATION_ABORTED)
//                FirebaseFirestoreException.Code.OUT_OF_RANGE -> Result.Error(RemoteDbError.OUT_OF_RANGE)
//                FirebaseFirestoreException.Code.UNIMPLEMENTED -> Result.Error(RemoteDbError.UNIMPLEMENTED)
//                FirebaseFirestoreException.Code.INTERNAL -> Result.Error(RemoteDbError.INTERNAL_ERROR)
//                FirebaseFirestoreException.Code.UNAVAILABLE -> Result.Error(RemoteDbError.SERVICE_UNAVAILABLE)
//                FirebaseFirestoreException.Code.DATA_LOSS -> Result.Error(RemoteDbError.DATA_LOSS)
//                else -> Result.Error(RemoteDbError.UNKNOWN_ERROR)
//            }
//        }
//        else -> Result.Error(RemoteDbError.UNKNOWN_ERROR) // Catch non-Firestore exceptions
//    }
//}
