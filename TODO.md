1. Dates are being stored as Strings. Make them real dates and correlate with correct DateTime format in Mongo.
2. Add lastActivity (when the user last used the app) and lastUpdated (when the user last updated their profile) to the profiles doc storage in Mongo.
3. Jenkins hangs and never exits from the job when it starts up Play in Prod mode (Play is working and just keeps listening). Find way to spawn process outside of Jenkins and to kill when pulling changes/restart.
4. Integrate Berkshelf for Chef recipe dependency management
5. Add option for provisioning and deploying on AWS EC2
6. Script creation of admin account/credentials, plus application account/credenticals for Mongo.
7. Add static analysis via Sonar or something else: http://community.opscode.com/cookbooks/sonar