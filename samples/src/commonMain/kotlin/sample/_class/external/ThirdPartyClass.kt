@file:JsModule("./ThirdPartyClass")
@file:JsNonModule

package sample._class.external

external class ThirdPartyClass {
    val items: Array<Item> = definedExternally
    fun getItemAt(at: Int): Item = definedExternally
    fun getItemFromJsExport(): ItemWithJsExport = definedExternally
}
