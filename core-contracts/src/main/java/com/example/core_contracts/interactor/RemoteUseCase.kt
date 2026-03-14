package com.example.core_contracts.interactor

import com.example.core_contracts.exceptions.ExceptionCatcher
import com.example.core_contracts.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Remote abstracted use case.
 *
 * This class preserves the callback based invoke to return Resource of domain.
 */
abstract class RemoteUseCase<Domain, in Body> {

    protected abstract fun executeRemoteDS(body: Body? = null): Flow<Domain>

    /**
     * Callback based entry point.
     *
     * @param scope CoroutineScope in which the call will be launched (typically viewModelScope)
     * @param body optional input
     * @param multipleInvoke when false -> emit loading(true) before execution and loading(false) after;
     *                       when true  -> skip those loading emissions
     * @param onResult callback receiving Resource.Progress / Resource.Failure / Resource.Success
     */
    operator fun invoke(
        scope: CoroutineScope,
        body: Body? = null,
        multipleInvoke: Boolean = false,
        onResult: (Resource<Domain>) -> Unit = {}
    ) {
        scope.launch(Dispatchers.Main) {
            if (!multipleInvoke) onResult.invoke(Resource.loading())

            runFlow(executeRemoteDS(body), onResult).collect { domainValue ->
                onResult.invoke(Resource.success(domainValue))

                if (!multipleInvoke) onResult.invoke(Resource.loading(false))
            }
        }
    }

    /**
     * Central flow runner that maps exceptions thrown in the inner flow using ExceptionCatcher,
     * then uses onResult to forward a Failure and a loading(false) signal.
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