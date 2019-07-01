package com.hb.coop.data.entity

interface Region {

    fun getId(): Int

    fun getName(): String

    fun getParent(): Region?

    fun getGasoline(): Int

    fun getMedical(): Int

}