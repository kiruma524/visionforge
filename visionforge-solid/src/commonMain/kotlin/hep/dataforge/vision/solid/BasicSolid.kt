package hep.dataforge.vision.solid

import hep.dataforge.meta.Meta
import hep.dataforge.meta.descriptors.NodeDescriptor
import hep.dataforge.meta.float
import hep.dataforge.meta.get
import hep.dataforge.meta.node
import hep.dataforge.vision.Vision
import hep.dataforge.vision.VisionBase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("solid")
public open class BasicSolid : VisionBase(), Solid {
    override val descriptor: NodeDescriptor get() = Solid.descriptor

    override var position: Point3D? = null

    override var rotation: Point3D? = null

    override var scale: Point3D? = null

    override fun update(change: Vision) {
        fun Meta.toVector(default: Float = 0f) = Point3D(
            this[Solid.X_KEY].float ?: default,
            this[Solid.Y_KEY].float ?: default,
            this[Solid.Z_KEY].float ?: default
        )

        change.properties[Solid.POSITION_KEY].node?.toVector()?.let { position = it }
        change.properties[Solid.ROTATION].node?.toVector()?.let { rotation = it }
        change.properties[Solid.SCALE_KEY].node?.toVector(1f)?.let { scale = it }
        super.update(change)
    }
}