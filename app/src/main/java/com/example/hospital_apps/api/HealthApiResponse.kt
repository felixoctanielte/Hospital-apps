package com.example.hospital_apps.api

data class HealthApiResponse(
    val Result: ResultData?
)

data class ResultData(
    val Resources: ResourceList?
)

data class ResourceList(
    val Resource: List<ResourceItem>?
)

data class ResourceItem(
    val Title: String?
)
