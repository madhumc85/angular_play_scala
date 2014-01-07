1. Dates are being stored as Strings. Make them real dates and correlate with correct DateTime format in Mongo.
2. Add lastActivity (when the user last used the app) and lastUpdated (when the user last updated their profile) to the profiles doc storage in Mongo.
3. Integrate Berkshelf for Chef recipe dependency management
4. Add option for provisioning and deploying on AWS EC2
5. Script creation of admin account/credentials, plus application account/credenticals for Mongo.
6. Add static analysis via Sonar or something else: http://community.opscode.com/cookbooks/sonar