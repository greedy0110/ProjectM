package com.example.a.lockquizekotlin.DBContract

import android.content.Context

//- readAll(): ListOf<CategoryEntry>
//- readOne(id: Int): CategoryEntry
//
//- writeOne(entry: CategoryEntry): Boolean
//
//- searchOne(id: Int): Boolean
//
//- deleteOne(id: Int): Boolean

// T 엔트리에 대한 DB 인터페이스
interface IDB<T> {
    fun readAll(context: Context): List<T>
    fun readOne(context: Context,id: Int): T?
    fun writeOne(context: Context,entry: T): Boolean
    fun searchOne(context: Context,id: Int): Boolean
    fun deleteOne(context: Context,id: Int): Boolean
}