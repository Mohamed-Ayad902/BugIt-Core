package com.example.core_contracts.utils

abstract class IMapper<Dto, Domain> {

    // dto to domain mapping is required to be implemented
    abstract fun dtoToDomain(dto: Dto): Domain

    open fun domainToDto(domain: Domain): Dto =
        throw NotImplementedError("Calling method that has empty body, You forgot to override and implement this method: domainToDto")

    // dto,domain list mapping support
    fun dtoToDomain(dtoList: List<Dto>?): List<Domain> = (dtoList ?: emptyList()).map(::dtoToDomain)
    fun domainToDto(domainList: List<Domain>?): List<Dto> =
        (domainList ?: emptyList()).map(::domainToDto)
}