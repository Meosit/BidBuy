# EPAM Final Web Project  
## Общие требования к проекту:
- Приложение реализовать применяя технологии `Servlet` и `JSP`.
- Архитектура приложения должна соответствовать шаблонам __*Layered architecture* и *Model-View-Controller*__. _Controller_ может быть только двух видов: контроллер роли или контроллер приложения.
- Информация о предметной области должна хранится в БД:
	+ данные в базе хранятся на кириллице, рекомендуется применять кодировку *UTF-8*
	+ технология доступа к БД – `JDBC` (только *JDBC*)
	+ для работы с БД в приложении должен быть реализован *__потокобезопасный__ пул соединений*, использовать слово `synchronized` запрещено.
	+ при проектировании БД рекомендуется не использовать более 5-8 таблиц
	+ доступ к данным в приложении осуществлять с использованием шаблона `DAO`.
- Интерфейс приложения должен быть локализован; выбор из двух или более языков: (*английский*, *русский*, и т д.).
- Приложение должно корректно обрабатывать возникающие исключительные ситуации, в том числе вести их логи. В качестве логгера использовать `Log4J` или `SLF4J`.
- Классы и другие сущности приложения должны быть грамотно структурированы по пакетам и иметь отражающую их функциональность название.
- При реализации бизнес-логики приложения следует при необходимости использовать *__шаблоны проектирования__* (например, шаблоны *GoF*: *Factory Method*, *Command*, *Builder*, *Strategy*, *State*, *Observer*, *Singleton*, *Proxy* etc).
- Для хранения пользовательской информации между запросами использовать сессию.
- Для перехвата и корректировки объектов запроса (request) и ответа (response) *применить фильтры*.
- При реализации страниц `JSP` следует использовать теги библиотеки `JSTL`, *использовать скриплеты запрещено*. Обязательным требованием является реализация и использование пользовательских тегов. Просмотр “длинных списков” желательно организовывать в постраничном режиме.
Валидацию входных данных производить на клиенте и на сервере.
- Документацию к проекту необходимо оформить согласно требованиям `JavaDoc`.
- Оформление кода должно соответствовать *Java Code Convention*.
- Разрешается использовать технологию `Maven`.
- Приложение должно содержать 3-4 теста `JUnit`, `Mockito` или `EasyMock`.  

## Общие требования к функциональности проекта:
- __Авторизация (sign in)__ и __выход (sign out)__ в/из системы.
- __Регистрация__ пользователя или добавление артефакта предметной области системы.
- __Просмотр информации__ (например: просмотр всех курсов, имеющихся кредитных карт, счетов и т.д.)
- __Удаление информации__ (например: отмена заказа, медицинского назначения, отказ от курса обучения и т.д.)
- __*Добавление* и *модификация* информации__ (например: создать и отредактировать курс, создать и отредактировать заказ и т.д.)  

## Предметная область разработки:  
### Система Сеть LikeIT.  
##### Исходная постановка задачи:  
> 3.	Система __Cеть LikeIT__. __Пользователь__ создает сообщения c просьбой о помощи в некотором вопросе, отвечает на сообщения других __Пользователей__, корректирует свои сообщения и персональную информацию. За ответ на поставленный вопрос, __Пользователь__ ставит оценку по некоторой шкале. Оценки других __Пользователей__ составляют рейтинг конкретного __Пользователя__. __Администратор__ создает (изменяет, удаляет) темы сообщений, управляет __Пользователями__.

+ Роли системы:
    - __Пользователь__ (User):
      + может создавать вопросы (редактировать, удалять)
      + отвечать на вопросы других пользователей (редактировать, удалять)
      + оценивать ответы других пользователей
      + редактировать собственный профиль
    - __Администратор__ (Admin):
      + может изменять вопросы и ответы других пользователей
      + блокировать/разблокировать пользователей
	
##### Use-Case диаграмма системы:
![Use-Case](https://raw.githubusercontent.com/Meosit/BuyBet/master/additional/use-case.png)
		
__P.S.__ Возможны изменения и правки в предметной области разработки.
