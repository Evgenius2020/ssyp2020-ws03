# ssyp2020-ws03: Игра “Call of Anoro€”  ![Deploy](https://github.com/Evgenius2020/ssyp2020-ws03/workflows/Deploy/badge.svg?branch=master)     
### Многопользовательский шутер с видом сверху
----------
### Работа с проектом
1. Получение исполняемых файлов клиента и сервера
    * Сборка проекта
        1. склонировать репозиторий
        1. перейти в папку “Call of Anoro€”
        1. выполнить команды ./gradlew client:packageJvmFatJar и ./gradlew server:assembleShadowDist
        1. Взять файлы клиента и сервера из папок client/build/libs и server/build/libs
    * Скачивание последней версии по ссылке https://github.com/Evgenius2020/ssyp2020-ws03/releases
2. Запуск
    * Игра на глобальном сервере: выполнить команду java -jar client-all-4.10.1.jar
    * Игра на локальном сервере
        1. Запустить сервер: java -jar server-all.jar
        2. Запустить клиент: java -jar client-all-4.10.1.jar \<server ip\>
