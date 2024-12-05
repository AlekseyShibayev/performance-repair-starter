## Spring Boot Starter

## Что это и зачем?
Стартер предоставляет бин EntityFinder, интерфейс-фасад,
который является надстройкой над Criteria API и Spring Data Jpa,
и предназначен для выполнения select запросов.

## У меня есть Spring Data Jpa, зачем мне что-то ещё?
Spring Data Jpa - хороша, первое знакомство с findByName(name) выглядит как магия.
Однако, с увеличением сложности приложения, можно столкнуться с несколькими проблемами:

### 1. Свалка в @Repository и нет DDD.
В репозиториях становится слишком много похожих методов. 
Если необходим новый метод, отличающийся от предыдущих чем-то незначительным - необходимо добавить еще один метод. 
Под незначительным я имею ввиду: предикат, read-only, limit, fetch, возвращаемый тип (One/Optional/List/Page/Slice), сортировка, блокировка, прочее. 
В итоге страдает переиспользование и DDD.

### 2. Проблемы @Query.
Нет limit в не нативной @Query, если ваш hibernate < 6.
 
### 3. Проблемы с пагинацией.
Spring Data Jpa предлагает использовать Specification для предикатов и Pageable для сортировки и пагинации.
А так же предоставляет JpaSpecificationExecutor который может вернуть Page.
Бездумное использование Page может привести к проблемам.

### 4. Проблема начинающих оптимизаторов.
Кто-то на вашем проекте захочет сделать read-only. 
Отличный способ сэкономить ресурсы, избежав hibernate dirty checking.
А если @Entity содержит jsonb, то обязательный, иначе получишь update в конце транзакции.

Есть несколько способов это сделать:
1. Хинтом для методов маркнутых @Query.
 ```java
@QueryHints(value = {@QueryHint(name = "org.hibernate.readOnly", value = "true")})
```
см. пункт "Свалка в @Repository".

2. @Transaction(read-only)

Псевдокод:
```java
@Transaction(read-only)
public User findUser(String login) {
    return userRepository.findByLogin()
        .orElseThrow(() -> new UserNotFoundException())
}
```
И рано или поздно вы найдете вот такой код. 
Лишняя прокся - мелочь.
Проблема в том, что выставленного выше try catch не достаточно, ваша оригинальная транзакция уже rollback.

### 5. @EntityGraph
Если @Entity не в вашем проекте, это проблема.
В остальных случаях см. пункт "Свалка в @Repository".

# Хватит это терпеть.
Я взял интерфейсы и классы Spring Data Jpa и CriteriaAPI и расширил их написав EntityFinder.

От Spring Data Jpa я взял:
1. Pageable для пагинации и сортировки.
2. Specification для предикатов и DDD.
3. Slice для постраничной выборки.

Так выглядит POJO, который можно дать в EntityFinder, чтобы выполнить select.
```java
class CommonQuery<E> {
    Class<E> classGenericType;
    Specification<E> specification;
    Ordering<E> ordering;
    Pageable pageable;
    boolean readOnly;
    Integer maximumExecutionTime;
    DynamicEntityGraph dynamicEntityGraph;
```

Дополнительно я сделал поддержку:
1. Read-only.
2. Ordering, по факту это Specification, но для фазы сортировок, если он есть, то pageable.sort игнорируется. Используется если необходима сортировка по колонке приджойненой таблицы.
3. Можно задать timeout.
4. Поддержка entity graph любой глубины с адекватным апи.

# Примеры использования:
Допустим у вас есть граф сущностей:

                     A
                   /  \
                  B    C
                /  \                
               D    E  

### 1. Аналог findAll.
```java
var query = new CommonQuery<>(A.class);
List<A> list = entityFinder.findAsList(query);
```

### 2. Страничка для фронта, кейс Slice для infinite scroll.
Обычно в этом кейсе приходит Pageable, и предикат в виде POJO или String query.
Необходимо подготовить Specification самостоятельно.
```java
var query = new CommonQuery<>(A.class)
        .setSpecification(specification)
        .setPageable(pageable)
        .readOnly;
Slice<A> slice = entityFinder.findAsSlice(query);
```

### 3. Тоже самое, но для маппинга в view POJO нужны будут ещё и B. Допустим A и B - one-to-one связь. 
Здесь удобно использовать динамический ентити граф (метод with - shortcut к нему) и metamodel.
```java
var query = new CommonQuery<>(A.class)
        .setSpecification(specification)
        .setPageable(pageable)
        .with(A_.B)
        .readOnly;
Slice<A> list = entityFinder.findAsSlice(query);
```
Передали в маппер list, маппим A и B на POJO view без N+1.

### 4. Тоже самое, но для маппинга во view нужны будут B, С, D, E. 
Допустим у них у всех связь - one-to-one. Методом with необходимо рассказать какие ветки тянуть. 
```java
var query = new CommonQuery<>(A.class)
        .setSpecification(specification)
        .setPageable(pageable)
        .with(A_.С)
        .with(A_.B, B_.D)
        .with(A_.B, B_.E)
        .readOnly;
Slice<A> list = entityFinder.findAsSlice(query);
``` 

### 5. Кейсы с one-to-many и many-to-many.
Если есть связь ?-to-many, то ResultSet, будет выглядеть так:
```text
A_1 B_1
A_1 B_2
A_2 B_3
A_2 B_4
```
А это значит что будут проблемы у кейсов с fetch, sort, limit.
В таком случае необходимо сделать два запроса.
Первым выбрать root сущность (A), чтобы ResultSet имел одну строку на одну root сущность.
Вторым запросом догрузить ветки.
Например для A-B 1:M, B-D 1:1, B-E 1:1 псевдокод будет выглядеть так: 
```java
var aQuery = new CommonQuery<>(A.class)
        .setSpecification(specification)
        .setPageable(pageable)
        .readOnly;
Slice<A> list = entityFinder.findAsSlice(aQuery);

var aIds = list.stream...;

var bQuery = new CommonQuery<>(B.class)
        .setSpecification(BSpecification.idsIn(aIds))
        .with(B_.D)
        .with(B_.E)
        .readOnly;
List<B> list = entityFinder.findAllAsList(query);
``` 
Далее сложим List B в мапу A.id против List B и работаем с мапой в маппере.

### 6. Кейс с сортировкой по колонке не root таблицы.
Тут на помощь придет Ordering. 
Псевдокод примера A-B 1:1.
```java
var query = new CommonQuery<>(A.class)
    .setSpecification(specification)
    .setPageable(pageable)
    .setOrdering((root, cq, cb) -> приджойнить к руту B, сделать cb.order по нужной колонке)
    .readOnly;
Slice<A> list = entityFinder.findAsSlice(query);
``` 

### 7. Кейс с шедулерами.
Не открываем транзакцию, делаем запрос, который возвращает root id. Native, JPQL, Specification - как удобней.
Бежим по результату, подгружаем граф, делаем работу.

```java
@Transactional
public void run(Long id){
        var query=new CommonQuery<>(A.class)
            .setSpecification(ASpecification.idEq(id))
            .with(A_.B)
            .with(A_.С)
            .with(A_.B, B_.D)
            .with(A_.B, B_.E)
        List<B> list=entityFinder.findAllAsList(query);
        
        ... 
}
```
p.s.
Я где-то потерял метод для One @Entity, скорее всего он был удален, т.к. не использовался.
Для одной сущности в некоторых случаях можно делать граф по больше чем 1 коллекции, т.к. тогда декартово произведение строк может быть не критичным. 

## Мысли в конце.
EntityFinder расширяет Spring Data Jpa и позволяет строить динамические запросы не только с использованием Specification и Pageable.
В вашем распоряжении fetch, limit, read-only, timeout, list/slice.
EntityFinder пропагандирует чаще использовать спецификации, больше спецификаций - больше переиспользования, быстрее разработка.
Когда-нибудь у меня дойдут руки вывернуть апи наизнанку и сделать fluent interface типо QueryDsl.

