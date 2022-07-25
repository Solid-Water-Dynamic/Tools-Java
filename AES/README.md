# Tools-Java: AES

A small tool that encrypts / decrypts files.

Cross-platform CLI,  requires Java 17+.  _I might build a GUI._

Download at https://github.com/Solid-Water-Dynamic/Tools-Java/raw/main/Downloads/AES-1.0.jar

If you prefer to build from source:
```
git clone https://github.com/Solid-Water-Dynamic/Tools-Java.git
cd Tools-Java/AES/
./gradlew clean jar

java -jar build/libs/AES-1.0.jar
Usage:
java -jar AES-1.0.jar encrypt originalFile
java -jar AES-1.0.jar decrypt encryptedFile secrets
```

When I share a file, I'll upload its encrypted copy to OneDrive<br>
then I'll send the OneDrive download link via chat software 1 (e.g., SMS)<br>
then I'll send the secrets (to decrypt),  but via chat software 2 (e.g., Slack)