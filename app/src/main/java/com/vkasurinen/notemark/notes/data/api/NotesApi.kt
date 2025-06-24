package com.vkasurinen.notemark.notes.data.api

import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import com.vkasurinen.notemark.notes.data.requests.NoteResponse
import com.vkasurinen.notemark.notes.data.requests.PaginatedNotesResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class NotesApi(
    private val httpClient: HttpClient
) {

    suspend fun createNote(request: NoteRequest): NoteResponse {
        return httpClient.post("/api/notes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateNote(request: NoteRequest): NoteResponse {
        return httpClient.put("/api/notes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getNotes(page: Int, size: Int): PaginatedNotesResponse {
        return httpClient.get("/api/notes") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun deleteNote(id: String) {
        httpClient.delete("/api/notes/$id")
    }
}