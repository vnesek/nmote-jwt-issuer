JWT token issuer with delegated authentification (google, facebook, twitter)

### TODO
- add support for account merging (API endpoint)
- refresh tokens
- correct OAuth2 fragment parameter passing
- disable users
- API for add/delete/list roleForEmail for users
- mongo based AppRepository
- JPA based repositories
- password based auth for users


### Samples

Fetch access token with client credentials from command line:

export TOKEN=`curl -s -u postman:ignored "http://localhost:8080/oauth/token?grant_type=client_credentials"|jq .access_token`