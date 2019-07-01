package com.hb.coop.data.api.request.vbd

import com.google.gson.annotations.SerializedName

data class AutoSuggestSearchParams(
        @SerializedName("Keyword")
        private val keyword: String
)