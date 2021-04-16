package net.inqer.touringapp.di.qualifiers

import javax.inject.Qualifier

/** Annotation for an Application Context dependency. */
@kotlin.annotation.Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Qualifier
annotation class ActiveTourRouteFlow()
