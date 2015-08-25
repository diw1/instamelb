---
title: API Reference

language_tabs:
  - shell
  - java

toc_footers:
  - <a href='#'>Sign Up for a Developer Key</a>
  - <a href='http://github.com/tripit/slate'>Documentation Powered by Slate</a>

includes:
  - errors

search: true
---

# Introduction

Welcome to the Instamelb API! This is a shameless copy of Instagram with a twist.

Language bindings in Shell and Java. Java docs incomplete at this point in time. Refer to shell tab for curl syntax.
Code examples are viewable in thee dark area to the right, and you can switch the programming language of the examples with the tabs in the top right.

All responses are in JSON.

Pagination is a stretch goal. Is not specified at this moment.

# Authentication

Basic HTTP Authentication is used in the Instamelb API. 
Credentials are passed as username:password in all routes.
Server is stateless, we DO NOT maintain session information.
Hence, username:password must be passed at every route.

> Authorization is as such:

```shell
curl -u USERNAME:PASSWORD\ 
    http://instamelb.pinkpineapple.me/
```

```java
N/A
```

<aside class="notice">
You must replace <code>USERNAME</code> and <code>PASSWORD</code> with your respective credentials.
</aside>

# register

## POST /register

```shell
curl -H "Content-Type: application/json" -X POST \
    -d '{
            "username": "NEW_USERNAME",
            "email": "USER@NEW_EMAIL.COM",
            "password": "NEW_PASSWORD"
        }'\ 
    http://instamelb.pinkpineapple.me/register
```

```java
N/A
```

> On successful registration, the above command returns JSON structured like this:

```json
{
    "registered": true
}
```

> In event of failed registration, we return:

```json
{
    "registered": false,
    "error": "Email already registered"
}
```

This endpoint registers a new user in the system.

### HTTP Request

`GET http://instamelb.pinkpineapple.me/register`

### Body Parameters
Parameter | Description
--------- | -----------
username | Username of account.
email | Email to be registered.
password | Password of account.

# userfeed

## GET /userfeed

```shell
curl -X GET -u USERNAME:PASSWORD\
    http://instamelb.pinkpineapple.me/feed
```

```java
N/A
```

> The above command returns JSON structured like this:

```json
{
    "feed": [
        {
            "feed_id": 53,
            "image_url": "http://images.pinkpineapple.me/images/53.png",
            "image_caption": "Picture 53",
            "comments": [
                {
                    "user_id": 1,
                    "username": "Mr. Wallace",
                    "user_image_url": "http://images.pinkpineapple.me/user/1.png",
                    "comment": "Nice Pic!"
                }
            ],
            "likes": []
        }
    ]
}
```

This endpoint retrieves the user feed of a user. Subject to pagination. See Query arguments.
Able to retrieve particular feed item by passing URL Parameter :feed_id.

### HTTP Request

`GET http://instamelb.pinkpineapple.me/userfeed/:feed_id?`

### URL Parameters

Parameter | Description
--------- | -----------
feed_id | *OPTIONAL* The ID of a particular feed to retrieve. Note that returned feed is still in "feed" array.

### Query Parameters
Parameter | Default | Description
--------- | ------- | -----------
sort_by | Time | One of: Time, Location. Sorts by specified parameter.

## PUT /userfeed

```shell
curl -X PUT -u USERNAME:PASSWORD\
    -d '{
        "comment": "This picture sucks!"
        "like": false
    }'\ 
    http://instamelb.pinkpineappleme/userfeed/53
```

```java
N/A
```

> On successful comment of feed item, the updated feed object is returned.

```json
{
    "feed_id": 53,
    "image_url": "http://images.pinkpineapple.me/images/53.png",
    "image_caption": "Picture 53",
    "comments": [
        {
            "user_id": 1,
            "username": "Mr. Wallace",
            "user_image_url": "http://images.pinkpineapple.me/user/1.png",
            "comment": "Nice Pic!"
        },
        {
            "user_id": 2,
            "username": "USERNAME",
            "user_image_url": "http://images.pinkpineapple.me/user/2.png",
            "comment": "This picture sucks!"
        }
    ],
    "likes": [
        {
            "user_id": 2,
            "username": "USERNAME",
            "like": false
        }
    ]
}
```

This route can be used to comment or like a feed item.

### HTTP Request
`PUT http://instamelb.pinkpineapple.me/userfeed/:feed_id`

### URL Parameters
Parameter | Description
--------- | -----------
feed_id | The ID of a particular feed to comment on.

### Body Parameters
Parameter | Description
--------- | -----------
comment | Text string comment.
like | Boolean indicating a `like`

# activityfeed

Same as userfeed.. TBD

# discover

## GET /discover 

```shell
curl -X GET -u USERNAME:PASSWORD\ 
    http://instamelb.pinkpineapple.me/discover?suggested=false

```

```java
N/A
```

> On successful search, results are returned in JSON structure:

```json
{
    "search": [
        {
            "user_id": 1,
            "username": "Mr. Wallace"
        }
    ]
}
```

This route is used to discover other users. Subject to pagination.

### HTTP Request
`PUT http://instamelb.pinkpineapple.me/discover`

### Query Parameters
Parameter | Default | Description
--------- | ------- | -----------
search | null | *OPTIONAL* Search string. Null returns everything. Subject to pagination of course.
suggested | true | *OPTIONAL* Boolean to indicate that user wishes to view suggested users.


# profile

## GET /profile

```shell
curl -X GET -u USERNAME:PASSWORD\ 
    http://instamelb.pinkpineapple.me/profile/2
```

> On successful profile information request:

```json
{
    "user_id": 2,
    "username": "USERNAME",
    "user_image_url": "http://images.pinkpineapple.me/user/2.png",
    "total_photo": 10,
    "total_follower": 4,
    "total_following": 1
}
```

Gets profile information for a user (or current user)

### HTTP Request
`GET http://instamelb.pinkpineapple.me/profile/:user_id?`

### URL Parameters
Parameters | Description
---------- | -----------
user_id | *OPTIONAL* ID of a user. No specified id defaults to current authenticated user.

## GET /profile/photo

```shell
curl -X GET -u USERNAME:PASSWORD\ 
    http://instamelb.pinkpineapple.me/profile/photo
```

```java
N/A
```

> On sucessful gallery request, returns JSON of:

```json
{
    "photos": [
        {
            "photo_id": 1,
            "photo_url": "http://images.pinkpineapple.me/images/1.png",
            "photo_caption": "One"
        },
        {
            "photo_id": 2,
            "photo_url": "http://images.pinkpineapple.me/images/2.png",
            "photo_caption": "Two"
        }
    ]
}
```

Gets all photos of a user. Subject to pagination.

### HTTP Request
`GET http://instamelb.pinkpineapple.me/profile/photo/:user_id?`

### URL Parameters
Parameters | Description
---------- | -----------
user_id | *OPTIONAL* ID of a user. No specified id defaults to current authenticated user.

## GET /profile/follower

Similar to /profile/photo

## GET /profile/following

Similar to /profile/photo

# photo

## POST /photo

```shell
curl -X POST -u USERNAME:PASSWORD\ 
    -d '{
        "image": "ABSM32Dd32kjd39akjk2poiwdkn"
    }',
    http://instamelb.pinkpineapple.me/photo
```

```java
N/A
```

> On successful upload, JSON returned:

```json
{
    "uploaded": true
}
```

Posts a photo.

### HTTP Request
`POST http://instamelb.pinkpineapple.me/photo`

### Body Parameters
Parameters | Description
---------- | -----------
image | BASE64 String Representation of an image.

