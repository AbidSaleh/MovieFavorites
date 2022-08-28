# MovieFavorites

A RESTful JSON API that supports
1. Adding Movies, users to the system,
2. Searching movies by keywords
3. Adding movies to favorite list
4. Authorization and authentication control for features mentioned above  


    Set Up
---------------------------------------------
    1.1. Install postgres 9.6 or newer version
    create a database

    1.2. replace this values of application.properties in this repository  according to your installation
    -->  spring.datasource.url
    --> spring.datasource.username
    --> spring.datasource.password
    1.3. run the application with following command

        mvn spring-boot:run

    1.4 run this sql commands to insert roles into your created database

    INSERT INTO roles(name) VALUES('ROLE_USER');
    INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
    INSERT INTO roles(name) VALUES('ROLE_ADMIN');
------------------------------------------------
    Testing The APIs

1.  You can test the apis from following swagger ui page
        http://{hostname}:{portname}/swagger-ui.html

        example:
        http://localhost:8080/swagger-ui.html
		
	Important points to access user specific resources: once you sign up, and signin as a user 
		1. authorize using the jwt token contained in responsebody of signin request
		2. dont forget to write Bearer	in front of jwt token with a white space. (as token type is Bearer)


2. Or you can test the api's and use cases according to following guideline

    Register some users with signup API:

    API --> POST /api/auth/signup
    example: register user of username "mod1",
    password "mod1password" with ROLE_MODERATOR and ROLE_USER
    --> request
    curl --location --request POST 'http://localhost:8080/api/auth/signup' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "username": "mod1",
        "email": "mod1@somemail.com",
        "password":"mod1password",
        "role":["mod","user"]
    }'

    --> response body
    {
        "message": "User registered successfully!"
    }
    signup with ROLE_USER
    --> request

    curl --location --request POST 'http://localhost:8080/api/auth/signup' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "username": "{some-username}",
        "email": "{some-user-email-address}",
        "password":"{some-user-password}",
        "role":["user"]
    }'

    --> response body
    {
        "message": "User registered successfully!"
    }

3. add a movie to database, (currently only moderator user can add movies to db)

    step -1: post request to sign in as moderator, (replace the username and password values with your systems registered values)

    --> request
    curl --location --request POST 'http://localhost:8080/api/auth/signin' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "username":"{some-registered-moderator-username}",
        "password":"{some-registered-moderator-password}"
    }'

    --> response body
    {
        "id": {current-moderator-id},
        "username": "{current-moderator-username}",
        "email": "{current-moderator-email}",
        "roles": [
            "ROLE_USER",
            "ROLE_MODERATOR"
        ],
        "accessToken": "current-moderator-jwt",
        "tokenType": "Bearer"
    }

    step-2: post request to add a movie

    --> request
    curl --location --request POST 'http://localhost:8080/movies/' \
    --header 'Authorization: Bearer {current-moderator-jwt}' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "title":"{movie-title}",
        "genre":"{movie-genre}",
        "releaseYear":"{movie-release-year}",
        "description":"{movie-description}"
    }'

    -->response body
    {
        "id": {auto-generated-movie-id},
        "title": "{movie-title}",
        "genre": "{movie-genre}",
        "releaseYear": "{movie-release-year}",
        "description": "{movie-description}"
    }

3. get movie details by id (accessible by anyone)
    API--> GET /movies/:id (required in the code challenge)
    --> request
    curl --location --request GET 'http://localhost:8080/movies/{movie-id}'
    -->response body
    {
        "id": {movie1-id},
        "title": "{movie1-title}",
        "genre": "{movie1-genre}",
        "releaseYear": "{movie1-release-year}",
        "description": "{movie1-description}"
    },
    {
        "id": {movie2-id},
        "title": "{movie2-title}",
        "genre": "{movie2-genre}",
        "releaseYear": "{movie2-release-year}",
        "description": "{movie2-description}"
    }
    ...


4. sign in with username and password

     API --> POST /api/auth/signin

     --> request
     curl --location --request POST 'http://localhost:8080/api/auth/signin' \
     --header 'Content-Type: application/json' \
     --data-raw '{
         "username":"{registered-user's-username}",
         "password":"{registered-user's-password}"
     }'

     -->response body

     {
         "id": {current-user's-id},
         "username": "{current-user's-username}",
         "email": "{current-user's-email}",
         "roles": [
             "ROLE_USER"
         ],
         "tokenType": "Bearer",
         "accessToken": "{user's-current-jwt}"
     }

5. add a movie to users favorite list (accessible only by signed in user)


    step-1: post sign in request to sign in as user,
     on success, response will contain user's-current-jwt

    step-2:   post request to add a movie to favorites
    API -->  POST /favorites/:id (required in the code challenge)

    --> request
    curl --location --request POST 'http://localhost:8080/favorites/{movie_id}' \
    --header 'Authorization: Bearer user's-current-jwt'

    --> response body
    added {movie-title} to favorites !

6. get a list of current users favorite movie
    API --> GET /favorites (required in the code challenge)

    step-1: post sign in request to sign in as user,
         on success, response will contain user's-current-jwt

    --> request
    curl --location --request GET 'http://localhost:8080/favorites' \
    --header 'Authorization: Bearer {user's-current-jwt}'

    --> response
    {
        "id": {movie1-id},
        "title": "{movie1-title}",
        "genre": "{movie1-genre}",
        "releaseYear": "{movie1-release-year}",
        "description": "{movie1-description}"
    },
    {
        "id": {movie2-id},
        "title": "{movie2-title}",
        "genre": "{movie2-genre}",
        "releaseYear": "{movie2-release-year}",
        "description": "{movie2-description}"
    }
    ...


7. search movie by searchKey,

    if searchKey is not empty returns movie list which title contains searchKey
    if searchKey is empty , returns all users favorite list as popular movies

    API--> GET /movies?search={search} (required in the code challenge)

    --> request
    curl --location --request GET 'http://localhost:8080/movies?search={search}'
    --> response body
    {
        "id": {movie1-id},
        "title": "{movie1-title}",
        "genre": "{movie1-genre}",
        "releaseYear": "{movie1-release-year}",
        "description": "{movie1-description}"
    },
    {
        "id": {movie2-id},
        "title": "{movie2-title}",
        "genre": "{movie2-genre}",
        "releaseYear": "{movie2-release-year}",
        "description": "{movie2-description}"
    }
    ...
