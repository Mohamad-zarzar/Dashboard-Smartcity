# Smart City Dashboard

Smart City Dashboard ist ein Projekt im rahmen des IoT Testfeldes der htw saar entwickelt wurde. Dieses Testfeld besteht aus einer Modellstadt die über Sensoren Daten über Umwelt und Umfeld sammelt und über ein Backend Service diese Speichert und Aggregiert. Dieses Backend stellt die Werte dann üner eine REST API den verschiedenen Frontend Anwendungen zur Verfügung.\
Bei Smart City Dashboard handelt es sich um eine JavaFX Desktop Anwendung. Sie dient zum darstellen und interagieren mit der IoT Smart City. Ziel ist es aktuelle Sensordaten der City an zu zeigen, so wie die Auswertung von historischen Daten mit hilfe von Charts zu ermöglichen. Die Anwendung ist mithilfe einer, über Swagger implementierten, REST Schnittstelle in der Lage dazu diese Daten direkt über diese API zu beziehen und immer die richtigen Daten zu präsentieren.

## Architektur

Die Idee war eine Desktop Applikation mit JavaFX in einem material Design zu implementieren. Daher hatten wir uns entschieden JFoenix ein zu setzten, da diese JavaFX Libary viele Features implementiert und auch der gewünschten Design Sprache folgt. 
Um einfach mit dem Backend zu kommunizieren haben wir weiterhin zusammen mit der Grupper, die dafür Verantwortlich ist, entschieden einen Client über deren Swagger Frontend zu generieren. Dieser übernimmt sowhol die Kommunikation mit der REST API als auch die Zuordnung der empfangenen Daten zu Java Objekten.
Die OverviewController Klassen sind von einem Interface abhängig, dass uns erlaubt alle Controller einheitlich zu gestalten und auch zu initialisieren.


#### Use Cases / User Stories

Das Programm wurde auf Basis folgender User Stories entwickelt:

* Als User möchte ich die Luftqualität abfragen können
* Als User möchte ich wissen ob Parkplätze frei sind.
* Als User möchte ich wissen wie sich die Luftqualität in einem Zeitraum verändert hat.
* Als User möchte Ich wissen wie die Parkplatzbelegung sich über einen Tag oder anderen Zeitraum entwickelt
* Als User möchte ich wissen, wie sich die Temperatur in der City verändert hat.
* Als User möchte ich wissen, wo freie Parkplätze verfügbar sind.


#### Anforderungen

###### Funktionale Anforderungen
    
**Must-Have**
* Anzeigen der aktuellen Luftqualität
* Anzeigen der aktuellen Temperatur
* Anzeigen der aktuell freien Parkplätze

**Should-Have**
* Veränderung der Luftqualität über einen Zeitraum
* Temperautrveränderung über einen Zeitraum
* Parkplatzbelegung über einen Zeitraum
* Temperatur an verschieden Orten

**Could-Have**
* Freie Parkplätze auf einer Karte anzeigen
* Wichtige Orte auf Karte anzeigen
* Parkplatzbelegung nach Orten auswerten

###### Nichtfunktionale Anforderungen

**Must-Have**
* verwendung von JavaFX
* Daten über eine REST API entgegennehmen

**Should-Have**
* Erweiterbarkeit des Programmes sicherstellen


#### Lösungsstrategie

Wie schon erwähnt hatten wir uns entschieden JFoenix einzusetzten, da diese JavaFX Libary viele Features implementiert und auch der gewünschten Design Sprache folgt. Wie Drawer für ein Seitenmenü oder einen DateTimePicker.
Java bzw. JavaFX als Programmiersprache war vorgegeben. Die Entscheidung auf einen Generierten Client für die API Zugriffe wurde getroffen um uns mehr auf das Uster Interface konzentrieren zu können und leicht bei Änderungen anzupassen.

#### Statisches Modell

###### Bausteinsicht
![](readme-pictures/Bausteinsicht.jpg)


###### Verteilungssicht
Die Software ist eine Desktop Anwendung, sie läuft alos auf dem entsprechenden Computer auf der sie ausgeführt wird.

###### Klassendiagramme

![](readme-pictures/Package%20app.png)

![](readme-pictures/Package%20base.png)

![](readme-pictures/Package%20components.png)


![](readme-pictures/Package%20controller.png)

![](readme-pictures/Package%20implementation.png)


![](readme-pictures/Package%20interfaces.png)

![](readme-pictures/Package%20model.png)

![](readme-pictures/Package%20service.png)


#### Dynamisches Modell

![](readme-pictures/Ablaufdiagramm.jpg)

## Getting Started
Nachdem man das Projekt aus Git geklont hat muss man einfach über Maven install die Jar erzeugen. Diese ist dann lauffähig.

#### Vorraussetzungen

Es muss mindestens Java 1.8 auf dem Rechner installiert sein.
Zum starten ist sicher zu stellen, dass die Jar "aggregator-service" auf der aktuellsten Swagger API basiert. Wenn nicht muss diese mit swagger-codegen neu erstellt werden.
Dazu gibt es ein [Online Tool](https://editor.swagger.io/), dort muss man den code von der [API](http://stl-e-01.htwsaar.de/v3/api-docs) rein kopieren. Dann wird dieser zu "yaml" konvertiert. Danach kann man auf "Generate Client" einen Java Client erzeugen und lokal speicher.

#### Installation und Deployment

Wenn die JAR erzeugt wurde ist keine weiter Installation notwendig.


## Built With
Geben Sie an, welche Frameworks und Tools Sie verwendet haben. Z.B.:

* [JFoenix](https://github.com/jfoenixadmin/JFoenix) - The JavaFX library used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Swagger-Codegen](https://editor.swagger.io/) - Used to generate API Jar directly from the provided Swagger UI


## License

This project is licensed under the GNU General Public License v3.0

## Acknowledgments

* Danke an JFoenix für die zur verfügung stehende Demo, damit konnte man sich ein Bild machen wie die Elemente benutzt werden können

