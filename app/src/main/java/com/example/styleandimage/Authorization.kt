package com.example.styleandimage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.styleandimage.DataClasses.DataClassKlient
import com.example.styleandimage.SupportFunctions.SharedPreference
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.concurrent.thread

class Authorization : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        ifRemember()
        //val transitionIntent = Intent(this, proveroka::class.java) //обозначение activity на которую нужно перейти
        //startActivity(transitionIntent) //Запуск обозначенной activity
    }

    fun ifRemember(){
        val phone = findViewById<EditText>(R.id.phoneAuthorizationEditText) // обозначение EditText, для дальнейшего использования
        val password = findViewById<EditText>(R.id.passwordAuthorizationEditText) // обозначение EditText, для дальнейшего использования
        val checkBox = findViewById<CheckBox>(R.id.saveDataAuthorizationCheckBox) // обозначение CheckBox, для дальнейшего использования

        val sharedPreference = SharedPreference(this) //переменная для определения БД для маленького объема данных
        if (sharedPreference.getValueString("phone") != null && sharedPreference.getValueString("password") != null) { //если в мини БД значение phone не пусто, то
            phone.setText(sharedPreference.getValueString("phone")!!) // Установить в поле ввода телефона значение phone из БД
            password.setText(sharedPreference.getValueString("password")!!) // Установить в поле ввода телефона значение phone из БД
            checkBox.isChecked = true // акстивировать CheckBox
        }
    }

    fun clickAuthorization(view: View) { //функция добавление телефона в мини БД и при некоторых условиях переход на другую страницу приложения
        val phone = findViewById<EditText>(R.id.phoneAuthorizationEditText) // обозначение EditText, для дальнейшего использования
        val password = findViewById<EditText>(R.id.passwordAuthorizationEditText) // обозначение EditText, для дальнейшего использования
        val checkBox = findViewById<CheckBox>(R.id.saveDataAuthorizationCheckBox) // обозначение CheckBox, для дальнейшего использования
        val Error = findViewById<TextView>(R.id.ErrorAuthorizationTextView) // обозначение TextView, для дальнейшего использования
        val sharedPreference = SharedPreference(this) //переменная для определения БД для маленького объема данных (обозначается второй раз так как это другая функция)

        Error.text = ""
        var errorstatus = true

        sharedPreference.saveStr("phone", phone.editableText.toString(), "password", "")

        if (phone.text.toString().trim().length == 11) { //проверка строки телефона на длину
            val listData = connectKlient()
            for (klient in listData)
            {
                if (phone.text.toString() == klient.number.toString() && password.text.toString() == klient.password)
                {

                    if (checkBox.isChecked()) { // проверка галочки, если стоит, то
                        sharedPreference.saveStr("phone", phone.editableText.toString(), "password", password.editableText.toString())// добавление в мини БД в атрибут phone значение номера телефона из editPhone
                    } else { // иначе
                        sharedPreference.removeValue("password") //Очистка мини БД
                    }
                    errorstatus = false
                    val transitionIntent = Intent(this, Main::class.java) //обозначение activity на которую нужно перейти
                    startActivity(transitionIntent) //Запуск обозначенной activity
                }
            }
        }
        else{ // иначе (если длина телефона < 11, то
            errorstatus = true
        }
        if (errorstatus == true){
            Error.text = "Ошибка! Данные введены не корректно!"
        }
    }

    fun connectKlient() :MutableList<DataClassKlient>{ //Функция записи результатов функции подключения к БД
        val listKlient: MutableList<DataClassKlient> = mutableListOf() //создание списка
        thread { //Создание потока
            try { //попробовать
                val user = "fan651sh_sai" //Имя пользователя БД
                val pass = "Dobro1337//" //Пароль к БД
                val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai" //Строка подключения (Библиотека_для_подключения_к_БД://ип:порт/Имя_бд)

                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass) // подключение к БД
                val search = "SELECT * FROM klient;"//Составленеи запроса к БД к определенной таблице
                val searchQuery = connection.prepareStatement(search+";")  // выборка всей таблицы
                val result = searchQuery.executeQuery() // Запись выборки БД в переменную result

                while (result.next()) { //цикл пока есть записи в result (построчно)
                    val id: Int = result.getInt(1) // запись значения id в переменную
                    val name: String = result.getString(2) // запись значения фио мастера в переменную
                    val number: Long = result.getLong(3) // запись значения телефона в переменную
                    val password: String = result.getString(4) // запись значения фио мастера в переменную
                    val stringKlient = DataClassKlient(id, name, number, password) //Добавление переменной типа DataClassEmployee с значениями из result
                    listKlient.add(stringKlient) //Добавление переменной в список
                }
            } catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join() //Отвечает за приостановку основного потока до выполнения этого
        return listKlient //Вернуть список с значениями
    }

    fun clickRegistration(view: View){
        val transitionIntent = Intent(this, Registration::class.java) //обозначение activity на которую нужно перейти
        startActivity(transitionIntent) //Запуск обозначенной activity
    }
}

