package com.sys1yagi.rxandroid.tools

import de.greenrobot.event.EventBus
import groovy.transform.CompileStatic

@CompileStatic
class GlobalBus {
    static EventBus INSTANCE = new EventBus()

    public static register(o) {
        INSTANCE.register(o)
    }

    public static unregister(o) {
        INSTANCE.unregister(o)
    }

    public static post(o) {
        INSTANCE.post(o)
    }
}