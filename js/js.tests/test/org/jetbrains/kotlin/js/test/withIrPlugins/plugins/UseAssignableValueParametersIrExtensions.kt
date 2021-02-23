/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.withIrPlugins.plugins

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.copyTo
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrSetValueImpl
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrValueParameterSymbol
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class UseAssignableValueParametersIrExtensions : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(AssignValueParametersInFunctionBodyTransformer(pluginContext))
    }
}

private class AssignValueParametersInFunctionBodyTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (declaration.name.asString() != "target") {
            return super.visitSimpleFunction(declaration)
        }

        declaration.valueParameters = declaration.valueParameters.map {
            it.copyTo(declaration, isAssignable = true)
        }

        // reference new value parameters in body
        declaration.body?.transformChildrenVoid(
            object : IrElementTransformerVoid() {
                override fun visitGetValue(expression: IrGetValue): IrExpression {
                    if (expression.symbol is IrValueParameterSymbol && expression.symbol.owner.parent == declaration) {
                        return super.visitGetValue(
                            IrGetValueImpl(
                                expression.startOffset,
                                expression.endOffset,
                                expression.type,
                                declaration.valueParameters[0].symbol,
                                origin = expression.origin
                            )
                        )
                    }

                    return super.visitGetValue(expression)
                }
            }
        )


        // update body to mutate parameters
        declaration.body = declaration.body?.let { body ->
            IrBlockBodyImpl(
                body.startOffset,
                body.endOffset
            ) {
                // just set it to const string
                declaration.valueParameters.forEach {
                    statements.add(
                        IrSetValueImpl(
                            UNDEFINED_OFFSET,
                            UNDEFINED_OFFSET,
                            pluginContext.irBuiltIns.stringType,
                            it.symbol,
                            "generated".toIrConst(pluginContext.irBuiltIns.stringType),
                            null
                        )
                    )
                }
                statements.addAll(body.statements)
            }
        }

        return super.visitSimpleFunction(declaration)
    }
}