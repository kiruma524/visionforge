package space.kscience.visionforge.gdml.demo

import kotlinx.browser.window
import kotlinx.css.height
import kotlinx.css.vh
import org.w3c.files.FileReader
import org.w3c.files.get
import react.*
import react.dom.h1
import ringui.grid.ringCol
import ringui.grid.ringGrid
import ringui.grid.ringRow
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.fetch
import space.kscience.dataforge.names.Name
import space.kscience.gdml.Gdml
import space.kscience.gdml.decodeFromString
import space.kscience.visionforge.bootstrap.nameCrumbs
import space.kscience.visionforge.gdml.toVision
import space.kscience.visionforge.react.ThreeCanvasComponent
import space.kscience.visionforge.react.flexColumn
import space.kscience.visionforge.ring.ringThreeControls
import space.kscience.visionforge.solid.Solid
import space.kscience.visionforge.solid.Solids
import space.kscience.visionforge.solid.specifications.Canvas3DOptions
import styled.css

external interface GDMLAppProps : RProps {
    var context: Context
    var vision: Solid?
    var selected: Name?
}

@JsExport
val GDMLApp = functionalComponent<GDMLAppProps>("GDMLApp") { props ->
    var selected by useState { props.selected }
    var vision: Solid? by useState { props.vision }

    val onSelect: (Name?) -> Unit = {
        selected = it
    }

    val options = useMemo {
        Canvas3DOptions.invoke {
            this.onSelect = onSelect
        }
    }

    val visionManager = useMemo(props.context) { props.context.fetch(Solids).visionManager }

    fun loadData(name: String, data: String) {
        val parsedVision = when {
            name.endsWith(".gdml") || name.endsWith(".xml") -> {
                val gdml = Gdml.decodeFromString(data)
                gdml.toVision()
            }
            name.endsWith(".json") -> visionManager.decodeFromString(data)
            else -> {
                window.alert("File extension is not recognized: $name")
                error("File extension is not recognized: $name")
            }
        }

        vision = parsedVision as? Solid ?: error("Parsed vision is not a solid")
    }


    ringGrid {
        ringRow {
            ringCol {
                attrs {
                    lg = 9
                }
                flexColumn {
                    css {
                        height = 100.vh
                    }
                    h1 { +"GDML/JSON loader demo" }
                    //canvas

                    child(ThreeCanvasComponent) {
                        attrs {
                            this.context = props.context
                            this.solid = vision
                            this.selected = selected
                            this.options = options
                        }
                    }
                }

            }
            ringCol {
                attrs {
                    lg = 3
                }
                flexColumn {
                    css {
                        height = 100.vh
                    }
                    fileDrop("(drag file here)") { files ->
                        val file = files?.get(0)
                        if (file != null) {

                            FileReader().apply {
                                onload = {
                                    val string = result as String
                                    loadData(file.name, string)
                                }
                                readAsText(file)
                            }
                        }
                    }
                    nameCrumbs(selected, "World", onSelect)
                    ringThreeControls(options, vision, selected, onSelect)
                }
            }
        }
    }
}

