package com.vkasurinen.notemark.core.database.mappers

import com.vkasurinen.notemark.core.database.entity.NoteEntity
import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import com.vkasurinen.notemark.notes.data.requests.NoteResponse
import com.vkasurinen.notemark.notes.domain.Note

fun NoteResponse.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}


fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}



fun NoteResponse.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}


fun Note.toRequest(): NoteRequest {
    return NoteRequest(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}



