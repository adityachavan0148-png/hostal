HOSTEL MANAGEMENT SYSTEM — JAVA + HTML + CSS
==============================================

FEATURES
- Responsive hostel management website
- Room types and monthly fee display
- Student booking form (name, phone, course, room)
- Java HTTP server and simple booking API
- Recent booking table and refresh button
- No external Java libraries required

REQUIREMENTS
- JDK 11 or newer installed
- Windows, macOS, or Linux

HOW TO RUN ON WINDOWS
1. Extract this ZIP file to a folder.
2. Open the extracted folder.
3. Double-click run.bat.
   OR open VS Code terminal in this folder and run:
       javac src\HostelServer.java
       java -cp src HostelServer
4. Open your browser and visit:
       http://localhost:8080
5. Keep the terminal open while using the website.
6. Press Ctrl+C in the terminal to stop the server.

HOW TO RUN ON MAC / LINUX
1. Open a terminal in the extracted folder.
2. Run:
       chmod +x run.sh
       ./run.sh
3. Visit http://localhost:8080

PROJECT FILES
- src/HostelServer.java : Java backend server and booking API
- web/index.html        : Website structure
- web/style.css         : Website design and responsive layout
- web/script.js         : Form handling and booking list
- run.bat               : Windows launch file
- run.sh                : macOS/Linux launch file

IMPORTANT NOTES
- This is a student demonstration project, not a production hostel system.
- Booking records are stored in memory, so they reset when the server stops.
- The phone field is validated for basic input only; this is not a secure production system.
- JavaScript is used for interactive browser behavior. The main backend is Java.
