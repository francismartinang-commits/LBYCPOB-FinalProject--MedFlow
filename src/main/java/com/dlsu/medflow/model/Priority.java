package com.dlsu.medflow.model;

import java.io.Serializable;

/** Priority flag for a laboratory request, as mentioned under Polymorphism (createRequest overload). */
public enum Priority implements Serializable {
    ROUTINE("Routine"),
    URGENT("Urgent"),
    STAT("STAT");


