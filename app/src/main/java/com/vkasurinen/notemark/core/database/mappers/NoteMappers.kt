package com.vkasurinen.notemark.core.database.mappers

import com.vkasurinen.notemark.core.database.entity.NoteEntity
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.notes.network.NoteDto

fun NoteDto.toDomain(): Note {
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



fun NoteDto.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}


fun Note.toRequest(): NoteDto {
    return NoteDto(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        lastEditedAt = lastEditedAt
    )
}