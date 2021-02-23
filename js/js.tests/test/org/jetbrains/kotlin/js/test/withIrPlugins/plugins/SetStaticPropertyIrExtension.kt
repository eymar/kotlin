/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.withIrPlugins.plugins

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.impl.IrGetFieldImpl
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class SetStaticPropertyIrExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(SetStaticPropertyTransformation(pluginContext))
    }
}

private class SetStaticPropertyTransformation(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitProperty(declaration: IrProperty): IrStatement {
        if (declaration.name.asString() != "target") {
            return super.visitProperty(declaration)
        }

        declaration.backingField = declaration.backingField?.let { field ->
            val newField = pluginContext.irFactory.buildField {
                updateFrom(field)
                name = field.name
                isStatic = true
            }.also { newField ->
                newField.correspondingPropertySymbol = field.correspondingPropertySymbol
                newField.initializer = field.initializer
                newField.parent = field.parent
            }

            declaration.getter?.transformChildrenVoid(
                object : IrElementTransformerVoid() {
                    override fun visitGetField(expression: IrGetField): IrExpression {
                        return IrGetFieldImpl(
                            expression.startOffset,
                            expression.endOffset,
                            newField.symbol,
                            expression.type,
                            expression.origin,
                            expression.superQualifierSymbol
                        ).also {
                            it.receiver = expression.receiver
                        }
                    }
                },
            )

            newField
        }

        return super.visitProperty(declaration)
    }
} 