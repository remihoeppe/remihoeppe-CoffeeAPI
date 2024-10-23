# Coffee API

Our goal is to create an API that sends data about coffee roasters back to clients.

## Stories

### 1. Getting a list of all roasters

As an API client, I should be able to get a list of all the roasters in this API DB.
This list should include roasters names, websites (... and more later).

-> When client sends a GET request to `/roasters` endpoint, they should be send a JSON array containing all the roasters
in the database with the appropriate information.

### 2. Getting more information about a roaster

As an API client, I should be able to query for more information about a specific roaster.

-> when a client sends a GET request to `/roaster/{id}` or `/roasters/{name}`, it should be send a JSON object
containing more information about the specified roaster.

### 3. Adding a new roaster

As a client of the API, I should be able to add a new roaster to the API DB. To create a new roaster I will need to
supply, at the very least, a valid roaster name and website URL.

-> When a client sends a POST request to `/roasters`, accompanied with valid data, it should create a new roaster

### 4. Deleting a roaster

As a client of the API, I should be able to delete a roaster from the API DB.

-> When a client sends a DELETE request to `/roasters/{name}`, the corresponding roaster should be deleted and a 204 should be returned.
If no roaster can be found a 404 should be returned.

### 4. All CRUD should happen on a local DB

As a client of the API, I want the data I read from and write to, to be stored in a persistent way. For the time being a local Postgres DB will do.

-> A local DB should be set up and a connection to it created within the application.
All CRUD method should be refactored to be working with that local DB.

## Package

```
./gradlew distZip
```
