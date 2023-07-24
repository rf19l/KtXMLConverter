package com.rf.foster.ktxml.models.kotlin_resource

sealed class KotlinResource {
    abstract val name: String
}
data class KotlinColorResource(override val name: String, val value: String) : KotlinResource()
data class KotlinDimenResource(override val name: String, val value: String, val unit: String) : KotlinResource()
data class KotlinStyleResource(override val name: String, val items: List<KotlinResource>) : KotlinResource()
data class KotlinLiteralResource(override val name: String, val value: String) : KotlinResource()