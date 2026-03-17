package com.example.core_contracts.interactor

import com.example.core_contracts.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Remote abstracted use case.
 *
 * This class preserves the callback based invoke to return Resource of domain.
 */
abstract class RemoteUseCase<Domain, in Body> : BaseUseCase<Domain>() {

    /**
     * Executes the remote data source logic.
     */
    abstract fun executeRemoteDS(body: Body? = null): Flow<Domain>

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
}