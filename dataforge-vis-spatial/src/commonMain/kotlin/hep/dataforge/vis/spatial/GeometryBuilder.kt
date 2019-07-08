package hep.dataforge.vis.spatial

import hep.dataforge.meta.*
import hep.dataforge.vis.common.VisualObject

data class Point2D(val x: Number, val y: Number) : MetaRepr {
    override fun toMeta(): Meta = buildMeta {
        "x" to x
        "y" to y
    }

    companion object{
        fun from(meta: Meta): Point2D{
            return Point2D(meta["x"].number ?: 0, meta["y"].number ?: 0)
        }
    }
}

data class Point3D(val x: Number, val y: Number, val z: Number) : MetaRepr {
    override fun toMeta(): Meta = buildMeta {
        "x" to x
        "y" to y
        "z" to z
    }

    companion object{
        fun from(meta: Meta): Point3D{
            return Point3D(meta["x"].number ?: 0, meta["y"].number ?: 0, meta["y"].number ?: 0)
        }

        val zero = Point3D(0,0,0)
    }
}

/**
 * @param T the type of resulting geometry
 */
interface GeometryBuilder<T : Any> {
    /**
     * Add a face to 3D model. If one of the vertices is not present in the current geometry model list of vertices,
     * it is added automatically.
     *
     * @param normal optional external normal to the face
     * @param meta optional additional platform-specific parameters like color or texture index
     */
    fun face(vertex1: Point3D, vertex2: Point3D, vertex3: Point3D, normal: Point3D?, meta: Meta = EmptyMeta)

    fun build(): T
}

fun GeometryBuilder<*>.face4(
    vertex1: Point3D,
    vertex2: Point3D,
    vertex3: Point3D,
    vertex4: Point3D,
    normal: Point3D? = null,
    meta: Meta = EmptyMeta
) {
    face(vertex1, vertex2, vertex3, normal, meta)
    face(vertex1, vertex3, vertex4, normal, meta)
}

interface Shape : VisualObject {
    fun <T : Any> toGeometry(geometryBuilder: GeometryBuilder<T>)
}