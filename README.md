Assumptions :

1. User can only top up his/her account. Hence the endpoint /user/{userId}/deposit
    1. Endpoint will be in user-service but transaction will be in transaction-service
    2. This structure is used as it will allow other apis to call the transaction in future if required.
2. User can only withdraw money in 2 scenarios :
    1. User can send it to another wallet.
    2. User can pay to somebody using the wallet -> transfer to another wallet.
3. Not defining the effect of deleting user onto transactions table, due to few reasons :
    1. If a user is deleted, should we delete only those transactions initiated by the user (sender) OR even those
       transaction where the user is a receiver.
    2. Deleting transaction where user is receiver doesn't make sense as it will unnecessary partially delete the
       transaction records of sender.
   