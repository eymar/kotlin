/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.checkers.generator.diagnostics

import org.jetbrains.kotlin.diagnostics.Severity
import kotlin.reflect.KType

data class DiagnosticData(
    val severity: Severity,
    val name: String,
    val sourceElementType: KType,
    val psiType: KType,
    val parameters: List<DiagnosticParameter>,
    val positioningStrategy: PositioningStrategy,
    val group: String?,
)

data class DiagnosticParameter(
    val name: String,
    val type: KType
)

enum class PositioningStrategy(private val strategy: String) {
    DEFAULT("DEFAULT"),
    VAL_OR_VAR_NODE("VAL_OR_VAR_NODE"),
    SECONDARY_CONSTRUCTOR_DELEGATION_CALL("SECONDARY_CONSTRUCTOR_DELEGATION_CALL"),
    DECLARATION_NAME("DECLARATION_NAME"),
    DECLARATION_SIGNATURE("DECLARATION_SIGNATURE"),
    DECLARATION_SIGNATURE_OR_DEFAULT("DECLARATION_SIGNATURE_OR_DEFAULT"),
    VISIBILITY_MODIFIER("VISIBILITY_MODIFIER"),
    MODALITY_MODIFIER("MODALITY_MODIFIER"),
    OPERATOR("OPERATOR"),
    PARAMETER_DEFAULT_VALUE("PARAMETER_DEFAULT_VALUE"),
    PARAMETER_VARARG_MODIFIER("PARAMETER_VARARG_MODIFIER"),
    DECLARATION_RETURN_TYPE("DECLARATION_RETURN_TYPE"),
    OVERRIDE_MODIFIER("OVERRIDE_MODIFIER"),
    DOT_BY_SELECTOR("DOT_BY_SELECTOR"),
    OPEN_MODIFIER("OPEN_MODIFIER"),
    VARIANCE_MODIFIER("VARIANCE_MODIFIER"),

    ;

    val expressionToCreate get() = "SourceElementPositioningStrategies.$strategy"

    companion object {
        const val importToAdd = "org.jetbrains.kotlin.fir.analysis.diagnostics.SourceElementPositioningStrategies"
    }
}


fun DiagnosticData.hasDefaultPositioningStrategy(): Boolean =
    positioningStrategy == PositioningStrategy.DEFAULT

data class DiagnosticList(
    val diagnostics: List<DiagnosticData>,
)