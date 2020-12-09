## Refactoring & MultiThreading
Выполнены следующие пункты: 
1. [Выделить класс](./src/main/java/ru/netology/Server.java) `Server` с методами для
    - запуска
    - обработки конкретного подключения
1. [Реализовать обработку подключений](./src/main/java/ru/netology/Handler.java) с помощью `ThreadPool`'а 
   (выделите фиксированный на 64 потока и каждое подключение обрабатывайте в потоке из пула)
   
## [Handlers](./src/main/java/ru/netology/task2/Main.java)*
