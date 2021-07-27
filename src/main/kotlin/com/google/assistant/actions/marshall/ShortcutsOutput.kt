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

package com.google.assistant.actions.marshall

import com.google.assistant.actions.model.shortcuts.AndroidNamespaceMapper
import com.google.assistant.actions.model.shortcuts.ShortcutsRoot
import java.io.OutputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.MarshalException
import javax.xml.bind.Marshaller

@Throws(JAXBException::class, MarshalException::class)
fun writeShortcuts(shortcutsRoot: ShortcutsRoot, outStream: OutputStream) {
    val jc = JAXBContext.newInstance(ShortcutsRoot::class.java)
    val marshaller = jc.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", AndroidNamespaceMapper())
    marshaller.marshal(shortcutsRoot, outStream)
}
