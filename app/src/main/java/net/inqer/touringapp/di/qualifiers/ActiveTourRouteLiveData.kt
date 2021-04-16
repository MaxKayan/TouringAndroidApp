package net.inqer.touringapp.di.qualifiers

import javax.inject.Qualifier

/** Annotation for an Application Context dependency. */
@Qualifier
@kotlin.annotation.Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class ActiveTourRouteLiveData
