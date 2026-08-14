package com.dlsu.medflow.model

import java.io.Serializable

/** Priority flag for a laboratory request, as mentioned under Polymorphism (createRequest overload).  */
enum class Priority(label: String) : Serializable {
    ROUTINE("Routine"),
    URGENT("Urgent"),
    STAT("STAT");

    val label: String?

    init {
        this.label = label
    }
}