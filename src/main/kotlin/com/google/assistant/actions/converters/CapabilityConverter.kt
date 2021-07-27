// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.assistant.actions.converters

import com.google.assistant.actions.*
import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Fulfillment
import com.google.assistant.actions.model.actions.ParameterMapping
import com.google.assistant.actions.model.shortcuts.*

fun convertActionsToCapabilities(actionsRoot: ActionsRoot): List<Capability> {
    return actionsRoot.actions.map { action -> convertActionToCapability(action) }
}

private fun convertActionToCapability(action: Action): Capability {
    val capabilityIntents = mutableListOf<CapabilityIntent>()
    val slices = mutableListOf<Slice>()
    var shortcutFulfillments = mutableListOf<ShortcutFulfillment>()
    action.fulfillments.forEach { fulfillment ->
        val urlTemplate = fulfillment.urlTemplate
        if (fulfillment.fulfillmentMode == SLICE_FULFILLMENT) {
            slices.add(createSliceFromFulfillment(fulfillment, action))
        } else if (isAtUrlFulfillment(urlTemplate) && usesInlineInventory(action.parameters)) {
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
                    name = if (!paramName.isNullOrEmpty()) paramName else DEFAULT_PARAMETER_NAME
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
    val extras = mutableListOf<Extra>()
    // Currently only requiredForegroundActivity is whitelisted for <extra>
    if (!fulfillment.requiredForegroundActivity.isNullOrEmpty()) {
        extras.add(
            Extra(
                key = REQUIRED_FOREGROUND_ACTIVITY_KEY,
                value = fulfillment.requiredForegroundActivity
            )
        )
    }
    return CapabilityIntent(
        action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
        urlTemplate  = resolveUrlTemplateForFulfillmentType(urlTemplate),
        parameter = createParametersForFulfillment(fulfillment, action),
        extras = extras
    )
}

private fun resolveUrlTemplateForFulfillmentType(urlTemplate: String?) : UrlTemplate? {
    if (isAtUrlFulfillment(urlTemplate) || urlTemplate.isNullOrEmpty())
        return null
    return UrlTemplate(urlTemplate)
}

private fun createParametersForFulfillment(
    fulfillment: Fulfillment,
    action: Action
): List<Parameter> {
    val parameters = mutableListOf<Parameter>()
    val actionParameters = action.parameters
    fulfillment.parameterMappings.map { parameterMapping ->
        parameters.add(convertParameterMappingToParameter(
            parameterMapping,
            action.intentName ?: DEFAULT_INTENT_NAME,
            actionParameters
        ))}
    if (isAtUrlFulfillment(fulfillment.urlTemplate) && usesWebInventory(actionParameters)){
        parameters.add(createParameterForWebInventory(actionParameters))
    }
    return parameters
}

private fun createParameterForWebInventory(
    actionParameters: List<com.google.assistant.actions.model.actions.Parameter>): Parameter {
    val actionParamName = actionParameters.firstOrNull()?.name
    return Parameter(
        name = if (!actionParamName.isNullOrEmpty()) actionParamName else DEFAULT_PARAMETER_NAME,
        data = Data(pathPattern = actionParameters.firstOrNull()?.entitySetReference?.urlFilter)
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
            actionParameter?.type ?: DEFAULT_MIME_TYPE
        }
    }
}

private fun resolveActionParameterForIntentParameterName(
    parameterName: String,
    actionParameters: List<com.google.assistant.actions.model.actions.Parameter>
): com.google.assistant.actions.model.actions.Parameter? {
    return actionParameters.find { parameter -> parameter.name == parameterName }
}

private fun isAtUrlFulfillment(urlTemplate : String?) : Boolean {
    return !urlTemplate.isNullOrEmpty() && urlTemplate == AT_URL_FULFILLMENT
}

/**
 * Only one parameter supported for inline inventory with {@url} fulfillment
 */
private fun usesInlineInventory(
    parameters: List<com.google.assistant.actions.model.actions.Parameter>
): Boolean {
    return !parameters.firstOrNull()?.entitySetReference?.entitySetId.isNullOrEmpty()
}

/**
 * Only one parameter supported for web inventory with {@url} fulfillment
 */
private fun usesWebInventory(
    parameters: List<com.google.assistant.actions.model.actions.Parameter>
): Boolean {
    return !parameters.firstOrNull()?.entitySetReference?.urlFilter.isNullOrEmpty()
}
