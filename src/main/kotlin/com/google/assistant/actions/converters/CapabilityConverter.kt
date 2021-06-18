package com.google.assistant.actions.converters

import com.google.assistant.actions.*
import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Fulfillment
import com.google.assistant.actions.model.actions.ParameterMapping
import com.google.assistant.actions.model.shortcuts.*

fun convertActionsToCapabilities(actions: ActionsRoot): List<Capability> {
    return actions.actions.map { action -> convertActionToCapability(action) }
}

private fun convertActionToCapability(action: Action): Capability {
    val capabilityIntents = mutableListOf<CapabilityIntent>()
    val slices = mutableListOf<Slice>()
    var shortcutFulfillments = mutableListOf<ShortcutFulfillment>()
    action.fulfillments.forEach { fulfillment ->
        val urlTemplate = fulfillment.urlTemplate
        if (fulfillment.fulfillmentMode == SLICE_FULFILLMENT) {
            slices.add(createSliceFromFulfillment(fulfillment, action))
        } else if (!urlTemplate.isNullOrEmpty() && urlTemplate == AT_URL_FULFILLMENT) {
            shortcutFulfillments = createShortcutFulfillmentsFromAction(action)
        } else {
            capabilityIntents.add(createCapabilityIntentFromFulfillment(fulfillment, action))
        }
    }
    return Capability(
        name = action.intentName,
        queryPatterns = action.queryPatterns,
        intents = capabilityIntents,
        slices = slices,
        shortcutFulfillments = shortcutFulfillments,
    )
}

private fun createShortcutFulfillmentsFromAction(action: Action): MutableList<ShortcutFulfillment> {
    val shortcutFulfillments: MutableList<ShortcutFulfillment> = mutableListOf()
    // Different shortcuts can be bound to different parameters for direct shortcut fulfillment.
    // Hence create a <shortcut-fulfillment> block per parameter.
    action.parameters.forEach { parameter ->
        val paramName = parameter.name
        shortcutFulfillments.add(
            ShortcutFulfillment(
                parameter = Parameter(
                    name = if (!paramName.isNullOrEmpty()) paramName else "YOUR_PARAMETER_NAME"
                )
            )
        )
    }
    return shortcutFulfillments
}

private fun createCapabilityIntentFromFulfillment(
    fulfillment: Fulfillment,
    action: Action
): CapabilityIntent {
    val urlTemplate = fulfillment.urlTemplate
    return CapabilityIntent(
        action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
        urlTemplate = if (!urlTemplate.isNullOrEmpty()) UrlTemplate(urlTemplate) else null,
        parameter = fulfillment.parameterMappings.map { parameterMapping ->
            convertParameterMappingToParameter(
                parameterMapping,
                action.intentName ?: DEFAULT_INTENT_NAME,
                action.parameters
            )
        }
    )
}

private fun createSliceFromFulfillment(fulfillment: Fulfillment, action: Action): Slice {
    val urlTemplate = fulfillment.urlTemplate
    return Slice(
        urlTemplate = if (!urlTemplate.isNullOrEmpty()) UrlTemplate(urlTemplate) else null,
        parameter = fulfillment.parameterMappings.map { parameterMapping ->
            convertParameterMappingToParameter(
                parameterMapping,
                action.intentName ?: DEFAULT_INTENT_NAME,
                action.parameters
            )
        })
}

/**
 * Converts a ParameterMapping to a Parameter given <parameter>s in the <action>.
 */
private fun convertParameterMappingToParameter(
    parameterMapping: ParameterMapping,
    intentName: String,
    actionParameters: List<com.google.assistant.actions.model.actions.Parameter>
): Parameter {
    return Parameter(
        // TODO(tanub): Cross-check examples for intentParameter name
        name = parameterMapping.intentParameter,
        key = parameterMapping.urlParameter,
        mimeType = resolveMimeType(intentName, parameterMapping, actionParameters),
        required = parameterMapping.required,
        shortcutMatchRequired = parameterMapping.entityMatchRequired,
    )
}

private fun resolveMimeType(
    intentName: String,
    parameterMapping: ParameterMapping,
    actionParameters: List<com.google.assistant.actions.model.actions.Parameter>
): String? {
    return if (intentName.startsWith(ACTIONS_BUILT_IN_INTENT_RESERVED_NAMESPACE)) { // BII
        BII_INTENT_PARAMETER_MIME_TYPE
    } else {
        // Custom intent
        val actionParameter =
            resolveActionParameterForIntentParameterName(
                parameterMapping.intentParameter ?: DEFAULT_INTENT_PARAMETER,
                actionParameters
            )
        if (!actionParameter?.entitySetReference?.entitySetId.isNullOrEmpty())
            return null
        else {
            actionParameter?.type ?: "YOUR_MIME_TYPE"
        }
    }
}

private fun resolveActionParameterForIntentParameterName(
    parameterName: String,
    actionParameters: List<com.google.assistant.actions.model.actions.Parameter>
): com.google.assistant.actions.model.actions.Parameter? {
    return actionParameters.find { parameter -> parameter.name == parameterName }
}
