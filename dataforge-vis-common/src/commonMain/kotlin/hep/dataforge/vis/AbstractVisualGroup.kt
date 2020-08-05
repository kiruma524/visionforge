package hep.dataforge.vis

import hep.dataforge.meta.MetaItem
import hep.dataforge.names.Name
import hep.dataforge.names.NameToken
import hep.dataforge.names.asName
import hep.dataforge.names.isEmpty
import kotlinx.serialization.Transient


/**
 * Abstract implementation of mutable group of [VisualObject]
 */
abstract class AbstractVisualGroup : AbstractVisualObject(), MutableVisualGroup {

    //protected abstract val _children: MutableMap<NameToken, T>

    /**
     * A map of top level named children
     */
    abstract override val children: Map<NameToken, VisualObject>

    abstract override var styleSheet: StyleSheet?
        protected set

    /**
     * Update or create stylesheet
     */
    fun styleSheet(block: StyleSheet.() -> Unit) {
        val res = styleSheet ?: StyleSheet(this).also { styleSheet = it }
        res.block()
    }

    override fun propertyChanged(name: Name, before: MetaItem<*>?, after: MetaItem<*>?) {
        super.propertyChanged(name, before, after)
        for(obj in this) {
            obj.propertyChanged(name, before, after)
        }
    }

    // TODO Consider renaming to `StructureChangeListener` (singular)
    private data class StructureChangeListeners(val owner: Any?, val callback: (Name, VisualObject?) -> Unit)

    @Transient
    private val structureChangeListeners = HashSet<StructureChangeListeners>()

    /**
     * Add listener for children change
     */
    override fun onChildrenChange(owner: Any?, action: (Name, VisualObject?) -> Unit) {
        structureChangeListeners.add(
            StructureChangeListeners(
                owner,
                action
            )
        )
    }

    /**
     * Remove children change listener
     */
    override fun removeChildrenChangeListener(owner: Any?) {
        structureChangeListeners.removeAll { it.owner === owner }
    }

    /**
     * Propagate children change event upwards
     */
    protected fun childrenChanged(name: Name, child: VisualObject?) {
        structureChangeListeners.forEach { it.callback(name, child) }
    }

    /**
     * Remove a child with given name token
     */
    protected abstract fun removeChild(token: NameToken)

    /**
     * Add, remove or replace child with given name
     */
    protected abstract fun setChild(token: NameToken, child: VisualObject)

    /**
     * Add a static child. Statics could not be found by name, removed or replaced
     */
    protected open fun addStatic(child: VisualObject) =
        set(NameToken("@static(${child.hashCode()})").asName(), child)

    protected abstract fun createGroup(): AbstractVisualGroup

    /**
     * Set this node as parent for given node
     */
    protected fun attach(child: VisualObject) {
        if (child.parent == null) {
            child.parent = this
        } else if (child.parent !== this) {
            error("Can't reassign existing parent for $child")
        }
    }

    /**
     * Recursively create a child group
     */
    private fun createGroups(name: Name): AbstractVisualGroup {
        return when {
            name.isEmpty() -> error("Should be unreachable")
            name.length == 1 -> {
                val token = name.first()!!
                when (val current = children[token]) {
                    null -> createGroup().also { child ->
                        attach(child)
                        setChild(token, child)
                    }
                    is AbstractVisualGroup -> current
                    else -> error("Can't create group with name $name because it exists and not a group")
                }
            }
            else -> createGroups(name.first()!!.asName()).createGroups(name.cutFirst())
        }
    }

    /**
     * Add named or unnamed child to the group. If key is null the child is considered unnamed. Both key and value are not
     * allowed to be null in the same time. If name is present and [child] is null, the appropriate element is removed.
     */
    override fun set(name: Name, child: VisualObject?): Unit {
        when {
            name.isEmpty() -> {
                if (child != null) {
                    addStatic(child)
                }
            }
            name.length == 1 -> {
                val token = name.first()!!
                if (child == null) {
                    removeChild(token)
                } else {
                    attach(child)
                    setChild(token, child)
                }
            }
            else -> {
                //TODO add safety check
                val parent = (get(name.cutLast()) as? MutableVisualGroup) ?: createGroups(name.cutLast())
                parent[name.last()!!.asName()] = child
            }
        }
        childrenChanged(name, child)
    }

}