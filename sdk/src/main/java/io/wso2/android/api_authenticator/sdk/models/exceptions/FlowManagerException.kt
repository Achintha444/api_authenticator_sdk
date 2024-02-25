package io.wso2.android.api_authenticator.sdk.models.exceptions

/**
 * Exception to be thrown to the exception related to [FlowManager]
 */
internal class FlowManagerException (
    override val message: String?
): Exception(message) {
    companion object {
        /**
         * Flow manager exception TAG
         */
        const val FLOW_MANAGER_EXCEPTION = "FlowManager Exception"

        /**
         * Message to be shown when authentication is not completed
         */
        const val AUTHENTICATION_NOT_COMPLETED = "Authentication is not completed. Response returned FAIL_INCOMPLETE"
    }

    override fun toString(): String {
        return "$FLOW_MANAGER_EXCEPTION: $message"
    }
}