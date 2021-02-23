/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.withIrPlugins

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.js.test.BasicIrBoxTest
import org.jetbrains.kotlin.js.test.withIrPlugins.plugins.ReplaceOriginalFunctionsExtension
import org.jetbrains.kotlin.js.test.withIrPlugins.plugins.UseAssignableValueParametersIrExtensions

abstract class AbstractJsWithIrPluginBoxTest(
    private val extensions: List<IrGenerationExtension>
) : BasicIrBoxTest("compiler/testData/", "") {

    override fun setUp() {
        super.setUp()
        extensions.forEach {
            IrGenerationExtension.registerExtension(environment.project, it)
        }
    }
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
abstract class AbstractJsWithReplaceOriginalCallsIrPluginBoxTest :
    AbstractJsWithIrPluginBoxTest(listOf(ReplaceOriginalFunctionsExtension()))

// - at the moment the cross-module test fails (https://youtrack.jetbrains.com/issue/KT-44945)
// - the same-module test works
abstract class AbstractJsWithAssignableValueParametersPluginBoxTest :
    AbstractJsWithIrPluginBoxTest(listOf(UseAssignableValueParametersIrExtensions()))