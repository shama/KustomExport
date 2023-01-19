package sample._class.external

import deezer.kustomexport.KustomExport

@KustomExport
class ExternalClass(private val proxy: ThirdPartyClass) {
    fun getItemOnlyFromKotlin(): Item {
        return Item(0, "Kotlin", 300)
    }
    fun getItemFromJsExport(): ItemWithJsExport {
        return proxy.getItemFromJsExport()
    }
    fun getItemAt(at: Int): Item {
        return proxy.getItemAt(at)
    }
    fun sumAmounts(): Long {
        return proxy.items.fold(0) { sum, item -> sum + item.amount }
    }
    fun listOfIds(): List<Long> {
        return proxy.items.asList().map { it.id }
    }
}

@KustomExport
data class Item(
    val id: Long,
    val name: String,
    val amount: Long
)

@JsExport
data class ItemWithJsExport(
    val id: Long,
    val name: String,
    val amount: Long
)
