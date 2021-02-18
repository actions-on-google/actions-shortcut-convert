package com.google.assistant.actions.marshall

import com.google.assistant.actions.model.actions.ActionsRoot
import java.io.InputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.UnmarshalException

@Throws(JAXBException::class, UnmarshalException::class)
fun readActions(inStream: InputStream): ActionsRoot {
    val jc = JAXBContext.newInstance(ActionsRoot::class.java)
    return jc.createUnmarshaller().unmarshal(inStream) as ActionsRoot
}
