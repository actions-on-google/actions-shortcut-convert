package com.google.assistant.actions

import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.*

class CapabilityConverter {

    fun convertActionsToCapabilities(actions: ActionsRoot): List<Capability> {
        return actions.actions.map { action -> convertActionToCapability(action) }
    }

    private fun convertActionToCapability(action: Action): Capability {
        return Capability(
            name = action.intentName,
            intents = action.fulfillments.map { fulfillment ->
                CapabilityIntent(
                    action = Constants.ANDROID_ACTION_VIEW_DEFEAULT_INTENT,
                    urlTemplate = UrlTemplate(fulfillment.urlTemplate!!),
                    parameter = fulfillment.parameterMappings.map { parameterMapping ->
                        Parameter(
                            name = parameterMapping.intentParameter,
                            key = parameterMapping.urlParameter,
                            mimeType = if (action.intentName!!.startsWith(Constants.ACTIONS_BUILT_IN_INTENT_RESERVED_NAMESPACE)) Constants.BII_INTENT_PARAMETER_MIME_TYPE else "",
                            required = parameterMapping.required,
                            shortcutMatchRequired = parameterMapping.entityMatchRequired,
                        )
                    }
                )
            }
        )
    }
}