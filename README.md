This is an example Play 2.6 server written in Scala using ScalaJS compiled 
output as the web client's script.

This is split into three sbt projects with separate dependencies.

 - `server` - The Play server, with a POST route for the protobuf
 request / response. The main template includes the script from `web` and
 the CSRF token for making AJAX requests. The API routes support either bytes
 or JSON serialized protobufs, determined by the "Content-Type" header.
 - `web` - The ScalaJS app, has a Main file which is run on page load. Makes the
 AJAX requests using protobuf messages and prints sample requests to console.
 - `shared` - Scala dependencies shared between the other two projects,
 currently just the protobuf source and the generated scala classes from the 
 protobuf.
 
 
 Running the sample:
 
 1) Download the project ```git clone https://github.com/KurToMe/play-scalajs-proto-example.git```
 2) Go to project root directory ```cd play-scalajs-proto-example```
 3) Run the server ```sbt run```
 4) Open sample page in browser (see the API requests in the js console) <http://localhost:9000>
