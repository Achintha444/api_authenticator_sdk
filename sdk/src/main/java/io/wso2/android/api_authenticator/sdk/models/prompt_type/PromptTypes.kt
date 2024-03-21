package io.wso2.android.api_authenticator.sdk.models.prompt_type

/**
 * Enum class for prompt types
 *
 * @property promptType Prompt type value
 */
enum class PromptTypes(val promptType: String) {
    /**
     * User prompt type
     */
    USER_PROMPT("USER_PROMPT"),
    /**
     * Redirection prompt type
     */
    REDIRECTION_PROMPT("REDIRECTION_PROMPT"),
}