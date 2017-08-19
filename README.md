Server-side Spring Boot application for video streaming.

Functionality Summary:
- Indexes files in directories specified in application.properties
- Downloads and stores metadata (year/rating/poster) based on filename
- Metadata is stored in SQL database and cached for performance
- Endpoints serve up metadata based on subdirectory query and incorporate pagination
- Specified directories are monitored for new video files
- New video files are converted to natively castable format (if not already the correct format)
- Endpoint for video streaming supports seeking

There is an example application.properties file in the standalone module for help with configuration.
