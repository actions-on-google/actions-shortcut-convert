package com.google.assistant.actions

import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Fulfillment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ConvertActionsToShortcutsTest {

    @Test
    fun test1() {
        // Programmatically compose the domain objects that would come from actions.xml
        val bii = "actions.intent.GET_BARCODE"
        val deeplink = "myapp://deeplink"

        val actionsRoot = ActionsRoot(
            actions = listOf(
                Action(
                    intentName = bii,
                    fulfillment = Fulfillment(
                        urlTemplate = deeplink
                    )
                ),
            ),
        )

        // Convert them to domain objects that represent shortcuts.xml
        val shortcutsRoot = convertActionsToShortcuts(actionsRoot)

        // Check that capabilities match the source actions
        val capabilities = shortcutsRoot.capabilities
        assertEquals(1, capabilities.size)

        val capability = capabilities[0]
        assertEquals(bii, capability.name)

        val capabilityIntent = capability.intent
        assertNotNull(capabilityIntent)
        assertNotNull(capabilityIntent!!.urlTemplate)
        val urlTemplate = capabilityIntent.urlTemplate ?: return
        assertEquals(deeplink, urlTemplate.value)

        val shortcuts = shortcutsRoot.shortcuts
        assertEquals(0, shortcuts.size)
    }
}
