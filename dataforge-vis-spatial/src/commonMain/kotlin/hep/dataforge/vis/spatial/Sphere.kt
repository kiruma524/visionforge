package hep.dataforge.vis.spatial

import hep.dataforge.meta.EmptyMeta
import hep.dataforge.meta.Meta
import hep.dataforge.vis.common.VisualGroup
import hep.dataforge.vis.common.DisplayLeaf
import hep.dataforge.vis.common.VisualObject
import hep.dataforge.vis.common.double
import kotlin.math.PI

class Sphere(parent: VisualObject?, meta: Meta) : DisplayLeaf(parent, meta) {
    var radius by double(50.0)
    var phiStart by double(0.0)
    var phi by double(2 * PI)
    var thetaStart by double(0.0)
    var theta by double(PI)
}

fun VisualGroup.sphere(meta: Meta = EmptyMeta, action: Sphere.() -> Unit = {}) =
    Sphere(this, meta).apply(action).also { add(it) }

fun VisualGroup.sphere(
    radius: Number,
    phi: Number = 2 * PI,
    theta: Number = PI,
    meta: Meta = EmptyMeta,
    action: Sphere.() -> Unit = {}
) = Sphere(this, meta).apply(action).apply {
    this.radius = radius.toDouble()
    this.phi = phi.toDouble()
    this.theta = theta.toDouble()
}.also { add(it) }