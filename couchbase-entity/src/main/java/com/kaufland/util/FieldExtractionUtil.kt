package com.kaufland.util

import kaufland.com.coachbasebinderapi.BaseModel
import kaufland.com.coachbasebinderapi.BasedOn
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

import kaufland.com.coachbasebinderapi.Field
import javax.management.Query

object FieldExtractionUtil {

    fun typeMirror(annotation: Field): TypeMirror {
        try {
            annotation.type
            throw Exception("Expected to get a MirroredTypeException")
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror
        }
    }

    fun typeMirror(annotation: BasedOn): TypeMirror {
        try {
            annotation.value
            throw Exception("Expected to get a MirroredTypeException")
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror
        }
    }
}
