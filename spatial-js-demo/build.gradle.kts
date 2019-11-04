plugins {
    id("scientifik.js")
    //id("kotlin-dce-js")
}

dependencies {
    api(project(":dataforge-vis-spatial"))
    testCompile(kotlin("test-js"))
}

kotlin{
    target {
        browser{
            webpackTask {
                sourceMaps = false
            }
        }
    }
}