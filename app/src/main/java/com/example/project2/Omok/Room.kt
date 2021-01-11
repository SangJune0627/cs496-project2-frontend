package com.example.project2.Omok

data class Room (var roomnumber: Int, var user1: User, var user2: User?, var state: String)

data class User (val id: String, val name: String)