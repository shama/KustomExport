import { runTest } from "../../shared_ts/RunTest"
import { assertEquals } from "../../shared_ts/Assert"
import { sample } from "@kustom/Samples"
import ThirdPartyClass from "./ThirdPartyClass"

const Item = sample._class.external.js.Item
const ExternalClass = sample._class.external.js.ExternalClass

runTest("ExternalClass", () : void => {
    const thirdPartyClass = new ThirdPartyClass(
        [
            new Item(1, "One", 42),
            new Item(2, "Two", 100),
        ]
    )

    const externalClass = new ExternalClass(thirdPartyClass)

    const itemFromKotlin = externalClass.getItemOnlyFromKotlin()
    assertEquals(itemFromKotlin.id, 0, "item id from kotlin should be 0")
    assertEquals(itemFromKotlin.name, "Kotlin", "item name from kotlin should be Kotlin")

    const itemFromJsExport = externalClass.getItemFromJsExport()
    assertEquals(itemFromJsExport.id, 1337, "item id from JsExport should be 1337")
    assertEquals(itemFromJsExport.name, "JsExport", "item name from JsExport should be JsExport")

    const item1 = externalClass.getItemAt(0)
    assertEquals(item1.id, 1, "first id should be 1")
    assertEquals(item1.name, "One", "first name should be One")

    const item2 = externalClass.getItemAt(1)
    assertEquals(item2.id, 2, "second id should be 2")
    assertEquals(item2.name, "Two", "second name should be Two")

    const sum = externalClass.sumAmounts()
    assertEquals(sum, 142, "should be 142 total")

    const listOfIds = externalClass.listOfIds()
    assertEquals(listOfIds, [1,2], "should be an array of ids")
})