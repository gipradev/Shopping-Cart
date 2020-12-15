package com.gipra.vicibshoppy.VicibShoppy.server

import org.json.JSONObject

interface ServiceInterface {
    fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
}