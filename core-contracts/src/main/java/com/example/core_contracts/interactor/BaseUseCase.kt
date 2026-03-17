package com.example.core_contracts.interactor

import com.example.core_contracts.exceptions.ExceptionCatcher
import com.example.core_contracts.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * Base abstraction for all Use Cases.
 *
 * Provides a common mechanism to catch exceptions via [ExceptionCatcher] and
 * ensures the execution runs on [Dispatchers.IO].
 */
abstract class BaseUseCase<Domain> {

    /**
     * Central flow runner that maps exceptions thrown in the inner flow,
     * forwards a Failure resource, and signals the end of a loading state.
     *
     * @param requestExecution The cold flow containing the data source logic.
     * @param onResult Callback to emit Failure or Loading states to the UI.
     */
    protected open fun <M> runFlow(
        requestExecution: Flow<M>,
        onResult: suspend (Resource<Domain>) -> Unit = {}
    ): Flow<M> = requestExecution
        .catch { throwable ->
            val mapped = ExceptionCatcher.map(throwable)
            onResult.invoke(Resource.Failure(mapped))
            onResult.invoke(Resource.loading(false))
        }.flowOn(Dispatchers.IO)
}