package com.example.core_contracts.interactor

import com.example.core_contracts.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

abstract class LocalUseCase<Domain, in Body> : BaseUseCase<Domain>() {

    abstract fun executeLocal(body: Body? = null): Flow<Domain>

    operator fun invoke(
        scope: CoroutineScope,
        body: Body? = null,
        onResult: (Resource<Domain>) -> Unit = {}
    ) {
        scope.launch(Dispatchers.Main) {
            onResult.invoke(Resource.loading())
            runFlow(executeLocal(body), onResult).collect { domainValue ->
                onResult.invoke(Resource.Success(domainValue))
                onResult.invoke(Resource.loading(false))
            }
        }
    }

    operator fun invoke(body: Body? = null): Flow<Resource<Domain>> = flow {
        emit(Resource.loading())

        runFlow(executeLocal(body)) { failureResource ->
            emit(failureResource)
        }.collect { domainValue ->
            emit(Resource.Success(domainValue))
            emit(Resource.loading(false))
        }
    }
}