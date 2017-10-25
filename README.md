Server-side Spring Boot application for video streaming.

Functionality Summary:
- Indexes files in directories specified in application.properties
- Downloads and stores metadata (year/rating/poster) based on filename
- Metadata is stored in SQL database and cached for performance
- Endpoints serve up metadata based on subdirectory query and incorporate pagination
- Endpoint for video streaming supports seeking
- An observable directory monitor kicks off the following for new files:
  - Clears file list cache
  - Converts file to natively castable format (if not already the correct format)
  - Sends a push notification to known Android clients to notify them of the new video

There is an example application.properties file in the standalone module for help with configuration.
