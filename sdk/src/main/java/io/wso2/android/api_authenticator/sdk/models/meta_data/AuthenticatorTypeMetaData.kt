package io.wso2.android.api_authenticator.sdk.models.meta_data

import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Meta data related to the authenticator type
 */
open class AuthenticatorTypeMetaData(
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
    open val params: ArrayList<AuthenticatorTypeParam>?,
    /**
     * Additional data related to the authenticator type
     */
    open val additionalData: AuthenticatorTypeAdditionalData?
) {
    /**
     * Parameters related to the authenticator type
     */
    open class AuthenticatorTypeParam(
        open val param: String?,
        open val type: String?,
        open val order: Int?,
        open val i18nKey: String?,
        open val displayName: String?,
        open val confidential: Boolean?
    )

    /**
     * Additional data related to the authenticator type
     */
    open class AuthenticatorTypeAdditionalData

    override fun toString(): String {
        return JsonUtil.getJsonString(this)
    }
}
