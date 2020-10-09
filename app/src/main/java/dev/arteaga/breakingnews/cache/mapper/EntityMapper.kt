package com.arteaga.breaking.news.cache.mapper


interface EntityMapper<E, D> {
    fun mapToEntity(domain: D): E
    fun mapFromEntity(entity: E): D
}