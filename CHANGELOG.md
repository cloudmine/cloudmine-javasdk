HEAD
=====

Release 0.5
=====
* Updated social login to allow scope parameter, and other parameters.
* Added Social Graph querying through CMWebService
* Added registering and unregistering for CloudMine push notifications.
* Fixed a bug with geo points.

Release 0.4.1
=====
* Update error message for missing CMApiCredentials to be more clear on what the fix is

Release 0.4
=====
* Support social log in functionality
* Bug fix - support GeoPoint objects that use 'lng' for longitude key

Release 0.3.2
=====
* Remove TypedCMObjectResponse and Callback, replace with methods directly on CMObjectResponse for retrieving typed CMObjects
* Add support for retrieving CMGeoPoints distance information in a query
* Add support for retrieving CMFileMetaData, and renamed fileName to fileId
* Add type information to all Callback methods to prevent ClassCastExceptions
* Bug fix - if an incorrect type of Callback is passed to onCompletion, call onFailure with a ClassCastException, instead of silently failing
* Bug fix - if an object is loaded that is missing the __id__ field, set it from the keyed value instead of just failing
* Bug fix - don't serialize empty ACLs on CMObjects, so that Application level objects don't have an ACL field
