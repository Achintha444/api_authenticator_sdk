package io.wso2.android.api_authenticator.sdk.models.meta_data

abstract class AuthenticatorTypeMetaData(
    /**
     * I18n key related to the authenticator type
     */
    open val i18nKey: String?,
    /**
     * Prompt type
     */
    open val promptType: String?,
    /**
     * Params related to the authenticator type
     */
    open val params: ArrayList<AuthenticatorTypeParam>?
) {
    open class AuthenticatorTypeParam(
        open val param: String?,
        open val type: String?,
        open val order: Int?,
        open val i18nKey: String?,
        open val displayName: String?,
        open val confidential: Boolean?
    )
}
