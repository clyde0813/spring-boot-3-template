
# Spring Boot 3 Template

* 내가 쓰려고 만든 "새로 프로젝트 할때마다 만들기 귀찮은 부분들 구현한 템플릿" 프로젝트
* 예외처리, 인증&권한 부분 구현

## Gradle

- Java 21
- Spring Boot 3.6.1
- JJWT 0.12.6
- Spring Boot Security
- JPA
- Swagger
- Lombok


## Features

- Rest API
- JWT Authorization & Authentication
- Kakao Oauth2
- Custom Exception & Global Exception Handler


## API Reference

### Sign Up
```http
  POST /auth/singup
```
password는 BCrypt 해싱 알고리즘으로 암호화됨

~~이런 당연한걸 Meta는 안했다고?! 뿌슝빠쓩삐쓩~~

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. username |
| `password` | `string` | **Required**. password |

### Login
```http
  POST /auth/login
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. username |
| `password` | `string` | **Required**. password |

### Token validate
토큰에 담긴 정보 확인겸 넣음
```http
  Get /auth/validate
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `token` | `string` | **Required**. |

### Token Refresh
```http
  Get /auth/refresh
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `refreshToken` | `string` | **Required**. |

### Kakao Oauth
카카오 클라이언트에서 넘어온 1회용 인증 code를 넣으면 로그인 or 회원가입
```http
  Get /auth/oauth/kakao
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `code` | `string` | **Required**. |

### Custom Exception Test
400 FUCK!을 뱉어냅니다.
```http
  Get /test/exception/custom
```

## application.properties

* 프로젝트안에 properties가 없습니다.
* ~~나만 yaml보다 properties가 편한가?~~
* Question Mark가 있는 부분을 수정하삼!

```properties
spring.application.name=spring-boot

server.port=8080

spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.password=????
spring.datasource.username=????
spring.datasource.url=jdbc:mariadb://?.?.?.?:????/????

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=false

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.display-request-duration=true

jwt.secret.key="????"
jwt.refresh.expiration=604800000
jwt.access.expiration=900000

kakao.client_id=????
kakao.redirect_uri=????
```
## Acknowledgements
무지성 모르겠다! 말고 공식문서 읽기를 습관화합시다!
- [Kakao API](https://developers.kakao.com/docs/latest/ko/index)
- [jjwt](https://github.com/jwtk/jjwt)

