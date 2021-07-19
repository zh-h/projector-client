/*
 * MIT License
 *
 * Copyright (c) 2019-2021 JetBrains s.r.o.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  `maven-publish`
  jacoco
  java
}

jacoco {
  toolVersion = "0.8.7"
}

val kotlinVersion: String by project
val serializationVersion: String by project

tasks.withType<JacocoReport> {
  dependsOn("jvmTest")
  group = "Reporting"
  description = "Generate Jacoco coverage reports"
  val coverageSourceDirs = arrayOf(
    "commonMain/src",
    "jvmMain/src"
  )
  val classFiles = File("${buildDir}/classes/kotlin/jvm/")
    .walkBottomUp()
    .toSet()
  classDirectories.setFrom(classFiles)
  sourceDirectories.setFrom(files(coverageSourceDirs))
  additionalSourceDirs.setFrom(files(coverageSourceDirs))
  executionData
    .setFrom(files("${buildDir}/jacoco/jvmTest.exec"))
  reports {
    xml.isEnabled = true
    xml.destination = file(layout.buildDirectory.dir("../../JacocoReports/jacocoReportCommon.xml"))
    csv.required.set(false)
    html.outputLocation.set(layout.buildDirectory.dir("jacocoHtmlProjectorClient"))
  }
}

kotlin {
  js {
    browser()
  }
  jvm()

  sourceSets {
    all {
      languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
    }

    val commonMain by getting {
      dependencies {
        api(kotlin("reflect", kotlinVersion))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")
      }
    }

    val commonTest by getting {
      dependencies {
        api(kotlin("test", kotlinVersion))
      }
    }

    val jsMain by getting {
    }

    val jsTest by getting {
    }

    val jvmMain by getting {
    }

    val jvmTest by getting {
    }
  }
}
