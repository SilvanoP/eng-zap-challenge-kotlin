# eng-zap-challenge-kotlin
Desafio Zap: Aplicativo de imóveis

# como rodar localmente?
* Rodar usando IDE
Baixar o fonte e compilar usando sua IDE de preferência (feito no Android Studio)
* Rodar por linha de comando
Baixar o fonte e na raíz do projeto rodar o comando:
gradlew assembleDebug

# como rodar os testes?
Os testes estão localizados na pasta <seu diretório>\eng-zap-challenge-kotlin\app\src\test\java\br\com\desafio\grupozap

* Rodar usando IDE
Baixar o fonte e na sua IDE de preferência rodar os testes de unidade
* Rodar por linha de comando
Baixar o fonte e na raíz do projeto rodar o comando:
gradlew test

# como fazer o deploy?
Ao compilar, como descrito anteriormente, será gerado um arquivo .apk na pasta <seu diretório>\eng-zap-challenge-kotlin\app\build\output\debug
* Via linha de comando na pasta acima, digitar o comando:
 adb install app-debug.apk
* Via dispositivo: copiar o arquivo apk da pasta cima no dispositivo e pelo gerenciador de arquivos do dispositivo pressionar no arquivo (é necessário habilitar arquivos de fontes desconhecidas).