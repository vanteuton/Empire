package android.bowz.fr.empire

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Data {

    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null

}

class ReturnMessage {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("errors")
    @Expose
    var errors: List<Any>? = null
    @SerializedName("user")
    @Expose
    var user: User ?= null

}

class Province {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("surface")
    @Expose
    var surface: Int? = null
    @SerializedName("population")
    @Expose
    var population: Int? = null
    @SerializedName("soldiers")
    @Expose
    var soldiers: Int? = null
    @SerializedName("fortifications")
    @Expose
    var fortifications: Int? = null
    @SerializedName("defensive")
    @Expose
    var defensive: Int? = null
    @SerializedName("palace")
    @Expose
    var palace: Int? = null
    @SerializedName("markets")
    @Expose
    var markets: Int? = null
    @SerializedName("mills")
    @Expose
    var mills: Int? = null
    @SerializedName("foundries")
    @Expose
    var foundries: Int? = null
    @SerializedName("shipyards")
    @Expose
    var shipyards: Int? = null
    @SerializedName("user_id")
    @Expose
    var userId: Int? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

}

class Purchase {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    @SerializedName("processed")
    @Expose
    var processed: Int? = null
    @SerializedName("province_id")
    @Expose
    var provinceId: Int? = null
    @SerializedName("upgrade_id")
    @Expose
    var upgradeId: Int? = null
    @SerializedName("amount")
    @Expose
    var amount: Int? = null

}


class User {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    @SerializedName("gold")
    @Expose
    var gold: Int? = null
    @SerializedName("population")
    @Expose
    var population: Int? = null
    @SerializedName("foodStock")
    @Expose
    var foodStock: Int? = null
    @SerializedName("nobles")
    @Expose
    var nobles: Int? = null
    @SerializedName("merchands")
    @Expose
    var merchands: Int? = null
    @SerializedName("surface")
    @Expose
    var surface: Int? = null
    @SerializedName("soldiers")
    @Expose
    var soldiers: Int? = null
    @SerializedName("provinces")
    @Expose
    var provinces: List<Province>? = null

}

class Upgrade {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("gold_cost")
    @Expose
    var goldCost: Int? = null
    @SerializedName("population_cost")
    @Expose
    var populationCost: Int? = null
    @SerializedName("surface")
    @Expose
    var surface: Int? = null

}
