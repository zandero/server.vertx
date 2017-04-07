# server.vertx
Simple template for a REST serving vert.x server

## Compile
```
mvn clean install
```

## Run
```
java -jar target/server.vertx-1.0.jar
```

or

```
java -jar target/server.vertx-1.0.jar --port 8080
```


## Test 
```
GET http://localhost:4444/echo
```

### Security
 
A dummy session service knows only two users:
* admin (with an Admin role) and
* user (with a User role)

(both have password set to "password")


#### Log in with 'user' or 'admin'
```
GET http://localhost:4444/login?username=user&password=password

GET http://localhost:4444/login?username=admin&password=password
```

#### Read session id
The response will return a session in the `X-SessionId` cookie.
Call the following REST with `X-SessionId` header:


#### Make call to 'private' RESTs
```
GET http://localhost:4444/info
```
Only **admin** has access to following REST API:
```
GET http://localhost:4444/private
```