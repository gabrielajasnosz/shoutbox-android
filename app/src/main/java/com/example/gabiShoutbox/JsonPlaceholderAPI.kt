package com.example.gabiShoutbox

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.*

interface JsonPlaceholderAPI {
    @GET("shoutbox/messages")
    fun getMessageArray(): Call<Array<Message>?>?

    @POST("shoutbox/message")
    fun createPost(@Body MyMessage: Message): Call<Message>

    @PUT("shoutbox/message/{id}")
    fun createPut(
        @Path("id") id: String,
        @Body exampleItem: Message
    ): Call<Message>
}