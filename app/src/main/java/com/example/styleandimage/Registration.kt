package com.example.styleandimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.styleandimage.DataClasses.DataClassKlient
import java.sql.DriverManager
import kotlin.concurrent.thread

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }

    fun clickRegistration(view: View) {
        val error = findViewById<TextView>(R.id.errorRegistrationTextView)
        val name= findViewById<EditText>(R.id.nameRegistrationEditText)
        val number= findViewById<EditText>(R.id.phoneRegistrationEditText)
        val password= findViewById<EditText>(R.id.passwordRegistrationEditText)
        val listData = connectKlient()
        var check = true
        error.text = ""

        for (klient in listData){
            if (klient.number.toString() == number.text.toString()){
                check = false
            }
        }
        if (check == true){
            klientAddContent("klient", name.text.toString(), number.text.toString(), password.text.toString())
            error.text = "Аккаунт успешно создан"
        }
        else{
            error.text = "Ошибка! Такой аккаунт уже существует"
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

    fun klientAddContent(nameTable: String, name: String, number: String, password: String){
        thread { //Создание потока
            try { //попробовать
                val user = "fan651sh_sai" //Имя пользователя БД
                val pass = "Dobro1337//" //Пароль к БД
                val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai" //Строка подключения (Библиотека_для_подключения_к_БД://ип:порт/Имя_бд)

                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass)
                val numberToLong = number.toLong()
                val insert ="INSERT INTO $nameTable (id, name, number, password) VALUES " +
                        "(NULL, '$name', '$numberToLong', '$password');"
                val querynIsertAccount = connection.prepareStatement(insert)
                querynIsertAccount.execute()
            }
            catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join()
    }
}