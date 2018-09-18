# Sudden DAO

Quick and easy use of JPA with a DAO so instant it will shock you.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/es.eisig/sudden-dao.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22es.eisig%22%20AND%20a:%22sudden-dao%22)
[![pipeline status](https://gitlab.mccollum.enterprises/smccollum/genericentityejb/badges/master/pipeline.svg)](http://gitlab.mccollum.enterprises/smccollum/genericentityejb/pipelines)

A DAO (Data Access Object) is almost always needed in a java project. Extend the `eisiges.sudden_dao.GenericPersistenceManager` class and that's it.

## Use Cases

1. Query a database for a list of all objects whose values match an object's non-null object member variables

```java
Rock keyRock = new Rock();
keyRock.setColor(Color.BLUE);
List<Rock> blueRocks = rockManager.getMatching(keyRock);
```

2. Retrieve an object's primary key

```java
rockManager.getId(myObject);
```

## Getting started

1. Add sudden-dao to your project

Maven:
```xml
		<dependency>
			<groupId>es.eisig</groupId>
			<artifactId>sudden-dao</artifactId>
			<version>1.0.0</version>
		</dependency>
```

Gradle:
```groovy
repositories {
	mavenCentral()
}

dependencies {
	implementation 'es.eisig:sudden-dao:1.0.0'
}
```

2. Create a class extending `eisiges.sudden_dao.GenericPersistenceManager`

3. Create an empty constructor which calls `super(Class<T> entityClass)`

4. (optional) add additional methods

```java
@Local // javax.ejb.Local
public class UserBean extends GenericPersistenceManager<MyUser, Long> { // MyUser: entity being managed, Long: type of primary key
	public UserBean(){
		super(MyUser.class);
	}

	public TypedQuery<MyUser> getByBirthdateAsc() {
		return this.find().sortBy(MyUser_.birthdate).ascending().build();
	}
}
```

The following functions are exposed by GenericPersistenceManager:

```java
boolean containsKey(K)

T persist(T)

K getId(T)

T save(T)

T get(K)

void remove(T)

void saveAll(Collection<T>)

void removeAll(Collection<T>)

List<T> getAll()

boolean isTableEmpty()

List<T> getMatching(T)
```

