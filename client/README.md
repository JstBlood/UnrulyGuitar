While the project can be run out of the box via Gradle, running it from within Eclipse and IntelliJ seems to require adding the following as *VM* commands:
## 🔴IMPORTANT❗🔴  
### MAKE SURE TO ADD THIS TO YOUR RUN CONFIG
    --module-path="your_path/javafx-sdk-17.0.2/lib" --add-modules=javafx.controls,javafx.fxml,javafx.media