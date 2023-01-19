import { sample } from "@kustom/Samples"

type Item = sample._class.external.js.Item
type ItemWithJsExport = sample._class.external.ItemWithJsExport

export default class ThirdPartyClass {
    constructor(public items: Item[]) {}
    getItemAt(at: number): Item {
        return this.items[at]
    }
    getItemFromJsExport(): ItemWithJsExport {
        return new sample._class.external.ItemWithJsExport(1337, "JsExport", 1234)
    }
}
