package io.wso2.android.api_authenticator.sdk.models.autheniticator.meta_data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Meta data related to the authenticator
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class AuthenticatorMetaData(
    /**
     * I18n key related to the authenticator
     */
    open val i18nKey: String? = null,
    /**
     * Prompt type
     */
    open val promptType: String? = null,
    /**
     * Params related to the authenticator
     */
    open val params: ArrayList<AuthenticatorParam>? = null,
    /**
     * Additional data related to the authenticator
     */
    open val additionalData: AuthenticatorAdditionalData? = null
) {
    /**
     * Parameters related to the authenticator
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class AuthenticatorParam(
        open val param: String? = null,
        open val type: String? = null,
        open val order: Int? = null,
        open val i18nKey: String? = null,
        open val displayName: String? = null,
        open val confidential: Boolean? = null
    )

    /**
     * Additional data related to the authenticator
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class AuthenticatorAdditionalData(
        open val nonce: String? = null,
        open val clientId: String? = null,
        open val scope: String? = null,
        open val challengeData: String? = null,
        open val state: String? = null,
        open val redirectUrl: String? = null
    )

    fun toJsonString(): String {
        return JsonUtil.getJsonString(this)
    }
}
