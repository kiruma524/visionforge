package hep.dataforge.vision.solid

import hep.dataforge.meta.Meta
import hep.dataforge.meta.update
import hep.dataforge.names.NameToken
import hep.dataforge.vision.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public enum class CompositeType {
    UNION,
    INTERSECT,
    SUBTRACT
}

@Serializable
@SerialName("solid.composite")
public class Composite(
    public val compositeType: CompositeType,
    public val first: Solid,
    public val second: Solid,
) : SolidBase(), Solid, VisionGroup {

    init {
        first.parent = this
        second.parent = this
    }

    override val children: Map<NameToken, Vision>
        get() = mapOf(NameToken("first") to first, NameToken("second") to second)
}

@VisionBuilder
public inline fun VisionContainerBuilder<Solid>.composite(
    type: CompositeType,
    name: String = "",
    builder: SolidGroup.() -> Unit,
): Composite {
    val group = SolidGroup().apply(builder)
    val children = group.children.values.filterIsInstance<Solid>()
    if (children.size != 2) error("Composite requires exactly two children")
    return Composite(type, children[0], children[1]).also {
        it.configure { update(group.properties ?: Meta.EMPTY) }
        if (group.position != null) {
            it.position = group.position
        }
        if (group.rotation != null) {
            it.rotation = group.rotation
        }
        if (group.scale != null) {
            it.scale = group.scale
        }
        set(name, it)
    }
}

@VisionBuilder
public inline fun VisionContainerBuilder<Solid>.union(name: String = "", builder: SolidGroup.() -> Unit): Composite =
    composite(CompositeType.UNION, name, builder = builder)

@VisionBuilder
public inline fun VisionContainerBuilder<Solid>.subtract(name: String = "", builder: SolidGroup.() -> Unit): Composite =
    composite(CompositeType.SUBTRACT, name, builder = builder)

@VisionBuilder
public inline fun VisionContainerBuilder<Solid>.intersect(
    name: String = "",
    builder: SolidGroup.() -> Unit,
): Composite =
    composite(CompositeType.INTERSECT, name, builder = builder)