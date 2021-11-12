/*
 * Copyright 2021 Deezer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package deezer.kustom.compiler.js.pattern.`class`

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import deezer.kustom.compiler.js.SealedClassDescriptor
import deezer.kustom.compiler.js.jsExport
import deezer.kustom.compiler.js.jsPackage
import deezer.kustom.compiler.js.mapping.TypeMapping

fun SealedClassDescriptor.transform() = transformSealedClass(this)

fun transformSealedClass(origin: SealedClassDescriptor): FileSpec {
    val originalClass = ClassName(origin.packageName, origin.classSimpleName)

    val jsClassPackage = origin.packageName.jsPackage()
    val jsExportedClass = ClassName(jsClassPackage, origin.classSimpleName)

    // Impossible to get the value from a constructor to another. Ex:
    // class Foo(): Bar(33) // Cannot retrieve 33 as it's only available at runtime
    // So instead we create an empty constructor and all properties are abstract
    val ctorParams = origin.constructorParams.map {
        ParameterSpec(it.name, TypeMapping.exportedType(it.type))
    }
    val ctor = FunSpec.constructorBuilder()
//        .addParameters(ctorParams)
        .build()

    val properties = origin.properties.map { p ->
        val pb = PropertySpec.builder(p.name, TypeMapping.exportedType(p.type))
        /*if (ctorParams.any { it.name == p.name }) {
            pb.initializer(p.name).build()
        } else {*/
        pb.addModifiers(KModifier.ABSTRACT).build()
        // }
    }

    val functions = origin.functions.map {
        val params = it.parameters.map { p ->
            ParameterSpec.builder(p.name, TypeMapping.exportedType(p.type))
                .build()
        }
        FunSpec.builder(it.name)
            .addModifiers(KModifier.ABSTRACT)
            .addParameters(params)
            .returns(TypeMapping.exportedType(it.returnType))
            .build()
    }

    val exportFunSpec = FunSpec.builder("export${origin.classSimpleName}")
        .receiver(origin.asClassName)
        .returns(jsExportedClass)
        .beginControlFlow("return when (this)")
        .also {
            origin.subClasses.forEach { subClass ->
                it.addStatement("is %T -> export${subClass.classSimpleName}()", subClass.asClassName)
            }
        }
        .endControlFlow()
        .build()

    val importFunSpec = FunSpec.builder("import${origin.classSimpleName}")
        .receiver(jsExportedClass)
        .returns(origin.asClassName)
        .beginControlFlow("return when (this)")
        .also {
            origin.subClasses.forEach { subClass ->
                val exportedSubClass = ClassName(subClass.packageName.jsPackage(), subClass.classSimpleName)
                it.addStatement("is %T -> import${subClass.classSimpleName}()", exportedSubClass)
            }
        }
        .endControlFlow()
        .build()

    return FileSpec.builder(jsClassPackage, origin.classSimpleName)
        .addAliasedImport(origin.asClassName, "Common${origin.classSimpleName}")
        .also {
            origin.subClasses.forEach { subClass ->
                it.addAliasedImport(subClass.asClassName, "Common${subClass.classSimpleName}")
            }
        }
        .addType(
            TypeSpec.classBuilder(origin.classSimpleName)
                .addAnnotation(jsExport)
                .addModifiers(KModifier.SEALED)
                .primaryConstructor(ctor)
                .addProperties(properties)
                .addFunctions(functions)
                .build()
        )
        .addFunction(exportFunSpec)
        .addFunction(importFunSpec)
        .build()
}