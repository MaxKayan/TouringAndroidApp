package net.inqer.touringapp.di.qualifiers

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import javax.inject.Qualifier

/** Annotation for an Application Context dependency. */
@Qualifier
@Target(ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD)
annotation class ActiveTourRouteFlow()
