package com.google.assistant.actions.marshall

import com.google.assistant.actions.model.shortcuts.ShortcutsRoot
import java.io.InputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.UnmarshalException

@Throws(JAXBException::class, UnmarshalException::class)
fun readShortcuts(inStream: InputStream): ShortcutsRoot {
    val jc = JAXBContext.newInstance(ShortcutsRoot::class.java)
    return jc.createUnmarshaller().unmarshal(inStream) as ShortcutsRoot
}
