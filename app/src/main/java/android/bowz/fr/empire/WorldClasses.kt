package android.bowz.fr.empire


data class Data(
        var accessToken: String? = null
)

data class ReturnMessage(
        var status: Boolean? = null,
        var data: Data? = null,
        var message: String? = null,
        var errors: List<Any>? = null,
        var user: User? = null
)

data class Province(
        var id: Int? = null,
        var name: String? = null,
        var surface: Int? = null,
        var population: Int? = null,
        var soldiers: Int? = null,
        var fortifications: Int? = null,
        var defensive: Int? = null,
        var palace: Int? = null,
        var markets: Int? = null,
        var mills: Int? = null,
        var foundries: Int? = null,
        var shipyards: Int? = null,
        var userId: Int? = null,
        var createdAt: Any? = null,
        var updatedAt: String? = null
)

class Purchase(
        var id: Int? = null,
        var createdAt: String? = null,
        var updatedAt: String? = null,
        var processed: Int? = null,
        var provinceId: Int? = null,
        var upgradeId: Int? = null,
        var amount: Int? = null
)


data class User(
        var id: Int? = null,
        val name: String? = null,
        var email: String? = null,
        var createdAt: String? = null,
        var updatedAt: String? = null,
        var gold: Int? = null,
        var population: Int? = null,
        var foodStock: Int? = null,
        var nobles: Int? = null,
        var merchands: Int? = null,
        var surface: Int? = null,
        var soldiers: Int? = null,
        var provinces: List<Province>? = null,
        var map : List<map_item>? = null
)

data class Upgrade(
        var id: Int? = null,
        var createdAt: Any? = null,
        var updatedAt: Any? = null,
        var name: String? = null,
        var goldCost: Int? = null,
        var populationCost: Int? = null,
        var surface: Int? = null
)

data class map_item(
        var id: Int? = null,
        var king_name : String? = null,
        var name: String? = null
)




