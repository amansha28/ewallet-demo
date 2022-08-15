Assumptions :

1. User can only deposit money his/her account. Hence, the endpoint /ewallet/credit
    1. Endpoint will be in user-module but transaction will be in transaction-module
    2. This structure is used as it will allow other apis to call the transaction in future if required.
2. User can only withdraw money in 2 scenarios :
    1. User can send it to another wallet (transfer to another wallet).
    2. User can withdraw money from their own wallet.

==========================

Behaviour :

1. A new user can be registered using following api - POST : /ewallet/register 
2. User's balance can be checked using following api - GET : /ewallet/balance/?userId={userId}
3. Money can be deposited/added into user's account using following api - PUT : /ewallet/credit
4. Valid amount of money can be tranferred from one user's account into another's one using api - PUT : /ewallet/transfer/money

======================

Each API Behaviour : 
1) ## POST : /ewallet/register
   1) Successful registration of user : 
      - **_Request :_**
        - `{
          "userId": "aman12",
          "emailId": "amanxyz@gmail.com",
          "name": "Aman",
          "age": 33
          }`
        OR
        - `{
          "userId": "aman123",
          "emailId": "aman123@yahoo",
          "name": "Aman",
          "age": 35
          }`
      - **_Response_ :**
        - `{
          "code": 200,
          "message": "Ok",
          "data": {
          "userId": "aman12",
          "emailId": "amanxyz@gmail.com",
          "name": "Aman",
          "age": 33,
          "walletBalance": 0.0
          }
          }`
   2) Invalid email id
      - **_Request :_**
        - `{
          "userId": "neha456",
          "emailId": "neha456gmail",
          "name": "Neha",
          "age": 30
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "invalid email id",
          "data": null
          }`
   3) Email id null or empty
      - **_Request :_**
        - `{
          "userId": "aman123",
          "emailId": "",
          "name": "Aman",
          "age": 35
          }`
        OR
        - `{
          "userId": "aman123",
          "name": "Aman",
          "age": 35
          }`
      - **_Response :_**
        - `{
          "userId": "aman123",
          "name": "Aman",
          "age": 35
          }`
   4) User already registered
      - **_Request :_**
        - `{
          "userId": "neha4561",
          "emailId": "neha4561@gmail.com",
          "name": "Neha",
          "age": 20
          }`
      - **_Response :_**
        - `{
          "code": 1000,
          "message": "Application error occurred!!",
          "data": null
          }`
   5) User above legal age for sign-up
      - **_Request :_**
        - `{
          "userId": "neha4561",
          "emailId": "neha4561@gmail.com",
          "name": "Neha",
          "age": 15
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "user should be above legal age for signup",
          "data": null
          }`
2) ## GET : /ewallet/balance/?userId={userId}
   1) check balance for valid registered user
      - **_Request :_**
        - `/ewallet/balance/?userId=neha4561`
      - **_Response :_**
        - `{
          "code": 200,
          "message": "Ok",
          "data": {
          "userId": "neha4561",
          "emailId": "neha4561@gmail.com",
          "name": "Neha",
          "age": 20,
          "walletBalance": 0.00
          }
          }`
   2) check balance for Invalid user
      - **_Request :_**
        - `/ewallet/balance/?userId=aman123`
      - **_Response :_**
        - `{
          "code": 1000,
          "message": "Application error occurred!!",
          "data": null
          }`
      
3) ## PUT : /ewallet/credit
   1) add money to user wallet
      - **_Request :_**
        - `{
          "receiver": "aman123",
          "amount": 15
          }`
      - **_Response :_**
        - `{
          "transactionId": "6baa2673-f82d-4062-be3a-8652769d7075",
          "receiver": "aman123",
          "status": "SUCCESS",
          "amount": 15
          }`
   2) Amount transferred should be greater than 0.
      - **_Request :_**
        - `{
          "receiver": "aman123",
          "amount": 0
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "amount should be greater than zero.",
          "data": null
          }`
   3) Invalid receiver Id
       - **_Request :_**
         - `{
           "receiver": "neha45613",
           "amount": 10
           }`
       - **_Response :_**
         - `{
           "code": 404,
           "message": "User not found",
           "data": null
           }`
   4) Receiver should not be null or empty.
      - **_Request :_**
        - `{
          "receiver": "",
          "amount": 10
          }`
         OR
        - `{
          "amount": 10
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "receiver cannot be null or empty",
          "data": null
          }`
     
4) ## PUT : /ewallet/withdraw/money
   1) Successfully transfer money from one to another valid user wallet.
      - **_Request :_**
        - `{
          "sender": "neha4561",
          "receiver": "aman123",
          "amount": 10,
          "purpose": "Groceries"
          }`
      - **_Response :_**
        - `{
          "transactionId": "645c1c23-63d2-4d33-908c-d61343564b12",
          "sender": "neha4561",
          "receiver": "aman123",
          "purpose": "Groceries",
          "status": "SUCCESS",
          "amount": 10,
          "transactionTime": "2022-08-15T16:40:02.179+00:00"
          }`
   2) Amount transferred should be greater than 0.
      - **_Request :_**
        - `{
          "sender": "neha4561",
          "receiver": "aman123",
          "amount": 0,
          "purpose": "Groceries"
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "amount should be greater than zero.",
          "data": null
          }`
   3) Transfer funds more than available funds in wallet.
       - **_Request :_**
         - `{
           "sender":"neha4561",
           "receiver": "aman123",
           "amount": 50,
           "purpose": "Groceries"
           }`
       - **_Response :_**
         - `{
           "code": 1000,
           "message": "Insufficient Balance to perform transaction",
           "data": null
           }`
   4) Sender id cannot be null or empty
      - **_Request :_**
        - `{
          "sender": "",
          "receiver": "aman123",
          "amount": 5,
          "purpose": "Groceries"
          }`
        OR
        - `{
          "receiver": "aman123",
          "amount": 5,
          "purpose": "Groceries"
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "sender cannot be null or empty",
          "data": null
          }`
   5) Receiver id cannot be null or empty
      - **_Request :_**
        - `{
          "sender": "neha4561",
          "receiver": "",
          "amount": 5,
          "purpose": "Groceries"
          }`
        OR
        - `{
          "sender": "neha4561",
          "amount": 5,
          "purpose": "Groceries"
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "receiver cannot be null or empty",
          "data": null
          }`

5) ## PUT : /ewallet/withdraw/self
    1) successfully withdraw money from wallet.
       - **_Request :_**
         - `{
           "receiver": "aman123",
           "amount": 5,
           "purpose": "Groceries"
           }`
       - **_Response :_**
         - `{
           "transactionId": "e0463450-2a1c-4f44-b249-39801c7e6e78",
           "sender": "aman123",
           "receiver": "aman123",
           "purpose": "Groceries",
           "status": "SUCCESS",
           "amount": 5,
           "transactionTime": "2022-08-15T17:12:17.592+00:00"
           }`
   2) Amount withdrawn should be greater than 0.
      - **_Request :_**
        - `{
          "receiver": "aman123",
          "amount": 0,
          "purpose": "Groceries"
          }`
      - **_Response :_**
        - `{
          "code": 400,
          "message": "amount should be greater than zero.",
          "data": null
          }`
   3) Receiver Id should not be null or empty.
      - **_Request :_**
        - `{
          "receiver": "",
          "amount": 0,
          "purpose": "Groceries"
          }`
        OR
        - `{
          "amount": 0,
          "purpose": "Groceries"
          }`
      - **_Response :_**
        - {`
          "code": 400,
          "message": "receiver cannot be null or empty",
          "data": null
          }`
   4) In case purpose is not provided, assign default purpose.
      - **_Request :_**
        - `{
          "receiver": "aman123",
          "amount": 5
          }`
      - **_Response :_**
        - `{
          "transactionId": "4bf30dda-e241-443d-852e-8392ec3b06db",
          "sender": "aman123",
          "receiver": "aman123",
          "purpose": "Self-Withdrawal from wallet",
          "status": "SUCCESS",
          "amount": 5,
          "transactionTime": "2022-08-15T17:17:28.404+00:00"
          }`
   