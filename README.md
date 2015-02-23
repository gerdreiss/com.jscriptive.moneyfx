## MoneyFX - personal finance manager

Uses MongoDB as data store. 

Imports transactions from files downloaded from the respective bank's online services. At the moment, only Barclays Spain file formats are supported.

### Known issues/outstanding implementation:

1. At the moment only one specific user is supported. Some changes are needed to make more flexible and configurable
2. Imported files should be saved to MongoDB's GridFS
3. A settings dialog should be implemented to make MoneyFX more configurable/flexible