package hep.dataforge.vis.spatial

import hep.dataforge.vis.spatial.Visual3DPlugin.Companion.json
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {
    @ImplicitReflectionSerializer
    @Test
    fun testCubeSerialization(){
        val cube = Box(100f,100f,100f).apply{
            color(222)
        }
        val string  = json.stringify(Box.serializer(),cube)
        println(string)
        val newCube = json.parse(Box.serializer(),string)
        assertEquals(cube.toMeta(),newCube.toMeta())
    }
}