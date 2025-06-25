package com.vkasurinen.notemark.notes.data.api

import com.vkasurinen.notemark.core.database.mappers.toDomain
import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import com.vkasurinen.notemark.notes.data.requests.NoteResponse
import com.vkasurinen.notemark.notes.data.requests.PaginatedNotesResponse
import com.vkasurinen.notemark.notes.domain.Note
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import timber.log.Timber

class NotesApi(
    private val httpClient: HttpClient
) {

    suspend fun createNote(request: NoteRequest): NoteResponse {
        try {
            Timber.d("createNote() - Sending POST request with body: $request")
            val response = httpClient.post("https://notemark.pl-coding.com/api/notes") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<NoteResponse>()
            Timber.d("createNote() - Received response: $response")
            return response
        } catch (e: Exception) {
            Timber.e(e, "createNote() - Error during POST request")
            throw e
        }
    }


    suspend fun updateNote(request: NoteRequest): NoteResponse {
        try {
            Timber.tag("NotesApi").d("updateNote() - Sending PUT request with body: $request")
            val response = httpClient.put("https://notemark.pl-coding.com/api/notes") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<NoteResponse>()
            Timber.tag("NotesApi").d("updateNote() - Received response: $response")
            return response
        } catch (e: Exception) {
            Timber.tag("NotesApi").e(e, "updateNote() - Error during PUT request")
            throw e
        }
    }

    suspend fun getNotes(page: Int, size: Int): PaginatedNotesResponse {
        return try {
            Timber.d("getNotes(paginated) - page: $page, size: $size")
            val response = httpClient.get("https://notemark.pl-coding.com/api/notes") {
                parameter("page", page)
                parameter("size", size)
            }.body<PaginatedNotesResponse>().also {
                Timber.d("Paginated response: $it")
            }

            // Workaround: If empty but total > 0, try non-paginated
            if (response.notes.isEmpty() && response.total > 0) {
                Timber.w("Pagination bug detected, falling back to non-paginated request")
                getNotesUnpaginated()
            } else {
                response
            }
        } catch (e: Exception) {
            Timber.e(e, "getNotes(paginated) failed")
            throw e
        }
    }

    private suspend fun getNotesUnpaginated(): PaginatedNotesResponse {
        return try {
            Timber.d("getNotesUnpaginated() - trying without pagination")
            httpClient.get("https://notemark.pl-coding.com/api/notes")
                .body<PaginatedNotesResponse>().also {
                    Timber.d("Unpaginated response: $it")
                }
        } catch (e: Exception) {
            Timber.e(e, "getNotesUnpaginated() failed")
            throw e
        }
    }


    suspend fun deleteNote(id: String) {
        try {
            Timber.tag("NotesApi").d("deleteNote() - Sending DELETE request for note ID: $id")
            httpClient.delete("https://notemark.pl-coding.com/api/notes/$id")
            Timber.tag("NotesApi").d("deleteNote() - Successfully deleted note ID: $id")
        } catch (e: Exception) {
            Timber.tag("NotesApi").e(e, "deleteNote() - Error during DELETE request")
            throw e
        }
    }
}