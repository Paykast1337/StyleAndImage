package com.example.styleandimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun clickRecords(view: View) { // Функция перехода на другую страницу приложения по кнопке
        val transitionIntent = Intent(this, Records::class.java) //обозначение activity на которую нужно перейти
        startActivity(transitionIntent) //Запуск обозначенной activity
    }
    fun clickAddRecords(view: View) { // Функция перехода на другую страницу приложения по кнопке
        val transitionIntent = Intent(this, NewRecords::class.java) //обозначение activity на которую нужно перейти
        startActivity(transitionIntent) //Запуск обозначенной activity
    }
    fun clickHistory(view: View) { // Функция перехода на другую страницу приложения по кнопке
        val transitionIntent = Intent(this, History::class.java) //обозначение activity на которую нужно перейти
        startActivity(transitionIntent) //Запуск обозначенной activity
    }
    fun clickPrice(view: View) { // Функция перехода на другую страницу приложения по кнопке
        val transitionIntent = Intent(this, Price::class.java) //обозначение activity на которую нужно перейти
        startActivity(transitionIntent) //Запуск обозначенной activity
    }
}