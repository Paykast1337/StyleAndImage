<?

$host = "f0728693.xsph.ru"; // Адрес нашего MySQL сервера
$user = "f0728693_SaI"; // Имя пользователя
$pass = "123456mp"; // Пароль
$base = "f0728693_SaI"; // Название базы данных

$action = "none"; // Значение действия по умолчанию

if (isset($_GET['action'])) $action = $_GET['action']; // Если в GET запросе передается параметр action, то записать его в переменную $action

if ($action == "install") // Если передали команду "install" (установка таблицы)
{
    $conn = mysql_connect($host, $user, $pass) or die("Could not connect"); // Попытка соединиться с MySQL сервером
    //mysql_create_db($base); // Создаем базу данных если она не создана, то уберите кавычки в начале строки
    mysql_select_db($base) or die("Can't select database."); // Выбираем нашу базу данных
    $result = mysql_db_query($base, "CREATE TABLE ".$base.".`scores` (`id` INTEGER UNSIGNED NOT NULL DEFAULT NULL AUTO_INCREMENT, `username` TEXT NOT NULL, `points` INT NOT NULL, PRIMARY KEY(`id`));"); // Передаем команду на создание таблицы
    echo "Database created!"; // Выводим сообщение, что создание прошло успешно
}

if ($action == "get")
{
    $conn = mysql_connect($host, $user, $pass) or die("Could not connect"); // Попытка соединиться с MySQL сервером
    mysql_select_db($base) or die("Can't select database."); // Выбираем нашу базу данных
    $result = mysql_db_query($base, "SELECT `Name`, `View`, `Price` FROM `Price` ORDER BY 3 DESC LIMIT 10;"); // Делаем запрос на выдачу таблицы результатов
    while($row = mysql_fetch_array($result)) // Цикл пока есть значения в нашем сполученном списке строк
    {
        $name = $row['Name']; // Отделяем имя пользователя
        $view = $row['View']; // Отделяем количество очков
        $price = $row['Price']; // Отделяем количество очков
        echo $name."|".$view."|".$price."|"; // Выводим на экран имя и очки разделяя их символом |
    }
}

if ($action == "save") // Если передали команду "save" (запись рекорда в таблицу)
{
    $name = $_GET['Name']; // извлекаем имя пользователя
    $view = $_GET['View']; // извлекаем количество очков
    $price = $_GET['Price']; // извлекаем количество очков
     $conn = mysql_connect($host, $user, $pass) or die("Could not connect"); // Попытка соединиться с MySQL сервером
    mysql_select_db($base) or die("Can't select database."); // Выбираем нашу базу данных
    $result = mysql_db_query($base, "INSERT INTO `Price` VALUES (0, '".$name."', ".$view.", "$price");"); // Добавляем запись в таблицу
    if ($result != false) echo "Saved!"; // Выводим сообщение, что добавление записи прошло успешно
    else echo "Save error"; // Ошибка записи
}

?>