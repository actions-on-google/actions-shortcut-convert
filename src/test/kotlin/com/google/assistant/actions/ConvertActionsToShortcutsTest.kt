package com.google.assistant.actions

import com.google.assistant.actions.model.actions.*
import com.google.assistant.actions.model.shortcuts.*
import com.google.assistant.actions.model.shortcuts.Parameter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
The tests in this file each:
1. Programmatically compose an ActionsRoot object that represents some actions.xml input.
2. Programmatically compose a ShortcutsRoot object that represents the expected shortcuts.xml output after conversion.
2. Convert the ActionsRoot to a ShortcutsRoot object using convertActionsToShortcuts.
4. Compare that result of the conversion is the same as expected.
 */

private const val BII =  "actions.intent.GET_BARCODE"
private const val DEEPLINK = "myapp://DEEPLINK"
private const val INTENT_PARAMETER_1 = "myIntentParameter1"
private const val URL_PARAMETER_1 = "myUrlParameter1"
private const val PARAMETER_REQUIRED = "true"
private const val SHORTCUT_ENABLED_FALSE = "false"

private const val ENTITY_SET_ID = "myEntitySetId"
private const val ENTITY_ID_1 = "entityId1"
private const val ENTITY_1_NAME = "entity1Name"
private const val ENTITY_1_URL = "entity1Url"
private const val ENTITY_1_ALTERNATE_NAME = "entity1AltName"

private const val ENTITY_ID_2 = "entityId2"
private const val ENTITY_2_NAME = "entity2Name"
private const val ENTITY_2_URL = "entity2Url"
private const val ENTITY_2_ALTERNATE_NAME = "entity2AltName"


class ConvertActionsToShortcutsTest {

    @Test
    fun test_basicCapability_withDeeplink() {
        val actionsRoot = ActionsRoot(
            actions = listOf(
                Action(
                    intentName = BII,
                    fulfillments = listOf(
                        Fulfillment(
                            urlTemplate = DEEPLINK
                        )
                    )
                ),
            ),
        )

        val shortcutsRoot = convertActionsToShortcuts(actionsRoot)

        val shortcuts = shortcutsRoot.shortcuts
        assertEquals(0, shortcuts.size)

        val expectedShortcutsRoot = ShortcutsRoot(
            capabilities = listOf(
                Capability(
                    name = BII,
                    intents = listOf(
                        CapabilityIntent(
                            action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
                            urlTemplate = UrlTemplate(value = DEEPLINK)
                        )
                    )
                )
            )
        )

        assertEquals(expectedShortcutsRoot, shortcutsRoot)
    }

    @Test
    fun test_simpleCapability() {
        val actionsRoot = ActionsRoot(
            actions = listOf(
                Action(
                    intentName = BII,
                    fulfillments = listOf(
                        Fulfillment(
                            urlTemplate = DEEPLINK,
                            parameterMappings = listOf(
                                ParameterMapping(
                                    intentParameter = INTENT_PARAMETER_1,
                                    urlParameter = URL_PARAMETER_1,
                                    required = PARAMETER_REQUIRED
                                )
                            )
                        )
                    )
                ),
            ),
        )

        val expectedShortcutsRoot = ShortcutsRoot(
            capabilities = listOf(
                Capability(
                    name = BII,
                    intents = listOf(
                        CapabilityIntent(
                            action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
                            urlTemplate = UrlTemplate(value = DEEPLINK),
                            parameter = listOf(
                                Parameter(
                                    name = INTENT_PARAMETER_1,
                                    key = URL_PARAMETER_1,
                                    mimeType = BII_INTENT_PARAMETER_MIME_TYPE,
                                    required = PARAMETER_REQUIRED
                                )
                            )
                        )
                    )
                )
            )
        )

        val shortcutsRoot = convertActionsToShortcuts(actionsRoot)

        assertEquals(expectedShortcutsRoot, shortcutsRoot)
    }

    @Test
    fun test_simpleCapability_withShortcuts() {
        val actionsRoot = ActionsRoot(
            actions = listOf(
                Action(
                    intentName = BII,
                    parameters = listOf(
                        com.google.assistant.actions.model.actions.Parameter(
                            name = INTENT_PARAMETER_1,
                            entitySetReference = EntitySetReference(entitySetId = ENTITY_SET_ID)
                        )
                    ),
                    fulfillments = listOf(
                        Fulfillment(
                            urlTemplate = DEEPLINK,
                            parameterMappings = listOf(
                                ParameterMapping(
                                    intentParameter = INTENT_PARAMETER_1,
                                    urlParameter = URL_PARAMETER_1,
                                    required = PARAMETER_REQUIRED
                                )
                            )
                        )
                    ),
                    entitySets = listOf(
                        EntitySet(
                            entitySetId = ENTITY_SET_ID,
                            entities = listOf(
                                Entity(
                                    identifier = ENTITY_ID_1,
                                    name = ENTITY_1_NAME,
                                    url = ENTITY_1_URL,
                                    alternateName = ENTITY_1_ALTERNATE_NAME
                                ),
                                Entity(
                                    identifier = ENTITY_ID_2,
                                    name = ENTITY_2_NAME,
                                    url = ENTITY_2_URL,
                                    alternateName = ENTITY_2_ALTERNATE_NAME
                                ),
                            )
                        )
                    )
                ),
            ),
        )

        val expectedShortcutsRoot = ShortcutsRoot(
            capabilities = listOf(
                Capability(
                    name = BII,
                    intents = listOf(
                        CapabilityIntent(
                            action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
                            urlTemplate = UrlTemplate(value = DEEPLINK),
                            parameter = listOf(
                                Parameter(
                                    name = INTENT_PARAMETER_1,
                                    key = URL_PARAMETER_1,
                                    mimeType = BII_INTENT_PARAMETER_MIME_TYPE,
                                    required = PARAMETER_REQUIRED
                                )
                            )
                        )
                    )
                )
            ),
            shortcuts = listOf(
                Shortcut(
                    shortcutId = ENTITY_ID_1,
                    shortcutShortLabel = DEFAULT_SHORTCUT_SHORT_LABEL,
                    enabled = SHORTCUT_ENABLED_FALSE,
                    intents = listOf(
                        ShortcutIntent(
                            action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
                            data = ENTITY_1_URL
                        )
                    ),
                    capabilityBindings = listOf(
                        CapabilityBinding(
                            key = BII,
                            parameterBinding = listOf(
                                ParameterBinding(
                                    key = INTENT_PARAMETER_1,
                                    value = ENTITY_1_NAME
                                )
                            )
                        )
                    )
                ),
                Shortcut(
                    shortcutId = ENTITY_ID_2,
                    shortcutShortLabel = DEFAULT_SHORTCUT_SHORT_LABEL,
                    enabled = SHORTCUT_ENABLED_FALSE,
                    intents = listOf(
                        ShortcutIntent(
                            action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
                            data = ENTITY_2_URL
                        )
                    ),
                    capabilityBindings = listOf(
                        CapabilityBinding(
                            key = BII,
                            parameterBinding = listOf(
                                ParameterBinding(
                                    key = INTENT_PARAMETER_1,
                                    value = ENTITY_2_NAME
                                )
                            )
                        )
                    )
                )
            )
        )

        val shortcutsRoot = convertActionsToShortcuts(actionsRoot)

        assertEquals(expectedShortcutsRoot, shortcutsRoot)
    }
}
