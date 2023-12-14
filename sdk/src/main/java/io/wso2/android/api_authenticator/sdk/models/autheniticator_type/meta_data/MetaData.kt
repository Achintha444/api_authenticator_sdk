package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data

open class MetaData(
    open val i18nKey: String?,
    open val promptType: String?,
    open val params: ArrayList<Param>?,
    open val additionalData: Any?
) {
    open class Param(
        open val param: String?,
        open val type: String?,
        open val order: Int?,
        open val i18nKey: String?,
        open val displayName: String?,
        open val confidential: Boolean?
    )
}
