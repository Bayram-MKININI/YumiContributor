package net.noliaware.yumi_contributor.commun.util

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakReferenceDelegate<T>(initialValue: T? = null) : ReadWriteProperty<Any, T?> {

    private var reference = WeakReference(initialValue)

    override fun getValue(thisRef: Any, property: KProperty<*>): T? = reference.get()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        reference = WeakReference(value)
    }
}

fun <T> weak(obj: T? = null) = WeakReferenceDelegate(obj)