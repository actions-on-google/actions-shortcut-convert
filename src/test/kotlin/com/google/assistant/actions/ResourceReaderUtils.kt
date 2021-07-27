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

package com.google.assistant.actions

import com.google.assistant.actions.marshall.readActions
import com.google.assistant.actions.marshall.readShortcuts
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.ShortcutsRoot

class ResourceReaderUtils {

    fun readActionsResource(actionsFilePath: String): ActionsRoot {
        val actionsStream = this.javaClass.getResourceAsStream(actionsFilePath)
        return readActions(actionsStream)
    }

    fun readShortcutsResource(shortcutsFilePath: String): ShortcutsRoot {
        val shortcutsStream = this.javaClass.getResourceAsStream(shortcutsFilePath)
        return readShortcuts(shortcutsStream)
    }
}
