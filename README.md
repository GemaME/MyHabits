# My Habits
Applicación para Android de ejemplo para mostrar como se usan y funcionas diversos servicios de Google para Android (localización, detección de actividades y comprobar si el usuario esta dentro de una zona geográfica concreta). También hay un par de ejemplos de consumo de servicios REST, entre ellos se destaca en envío de información a tu cuenta de Ubidots. Este servicio web permite envio masivo y cíclico de información que posteriormente podemos formatearla en gráficas, tablas, crear eventos para que avisen otras aplicaciones, etc.

# Requisitos alcanzados (asignatura de Optimización del Master)
- Obtener información de la localización, actividad y geocercado.
- Configurar opciones como activar o desactivar servicios, intervalos de envio de información y algunas opciones.
- Crear widgets en Ubidots para mostrar la información.
- Enviar vectores con información: Cuando se envía la información para una variable se adjunta un valor y un contexto que es un conjunto de datos clave-valor.

# Descraga APK

- [Descargue el APK desde aquí](docs/my-habits.apk)

# Servicios & Herramientas

## Google APIs

- **Location:** Permite tanto pedir en un momento determinado en que coordenadas geográficas nos encontramos (latitud y longitud) como que automñaticamente recibamos notificaciones de los cambios de nuestra ubicación actual.
- **Activity Recognition:** Detecta que posibles actividades está realizando el portador del movil (andar, quito, correr, estar en un vehículo...) y su porcentaje de probabilidad
- **Geofencing:** La API nos permite definir areas dirculares de influencia a partir de una cordenada geográfica y un radio en metros. Cuando el usuario entre o salga de estas areas se recibe una notificación.

## Ubidots

### Datasets
Cuando creamos una cuenta podemos crear dataset que serían como contendores de datos asociados a una aplicación o módulo. En cada dataset se pueden definir diversas variables. En cada una guarda un valor y el contexto que son un conjunto de clave-valor.
 
### Widgets
Permite mostrar los datos recopilados en los datasets a una forma más clara y visual mediante gráficas, tablas, mapas... de esta forma es mñs fácil extraer conclusiones.

### Publicación de la información
Dispone tanto de una API REST como de una librería Java, para esta aplicación hemos usado la primera alternativa. En ambas hace falta un token para el acceso.

# Manual de Usuario

En la pantalla principal vemos distintas tarjetas que representan los distintos tipos de información o configuración:

- **Weather**: Muestra información del tiempo  de la temperatura en función de la latitud y altitud en la que estemos. Se usa el servicio REST Openweather. También podemos configurar el intervalo de tiempo al que hace consultas o si queremos desactivar o activar la realización de peticiones.
- **Updating Data**: Hay un displayer para configurar el intervalo de tiempo que se envía la información a Ubidots. Solo se envía la información que el usuario a seleccionado para enviar. Si el usuario no quiere enviar ningñun tipo de información no se hace ninguna petición (no es eficiente enviar una petición vacia).
- **User Location**: Podemos desactivar o activar el servicio que nos indica los cambios de la posición del equipo en un mapa. También podemos elegir que modalidad de servicio queremos permitiendo jugar entre el ahorro de batería y la precisión de la localización.
- **Activity**: Permite desactivar o activar el servicio que nos indica que actividad estamos realizando ahora.
- **Places**: Igual que el anterior pero en este caso desactivar o activar el servicio que nos indica en que zonas hemos entrado.

En la barra superior vemos 3 botones que de derecha a izquierda brindan el siguiente servicio:
- **Log**: Abre una actividad con toda la información que obtiene la aplicación (localización, actividad, tiempo, lugar...)
- **Tablas**: Se abre un submenu
	* Weather info: Abre un pequeño navegador que muestra un widget tabla de Ubidots con la información recopilada.
	* Place info: Lo mismo pero indicando información de la actividad y último geocercado detectado.
- **Mapa**: Abre un pequeño navegador que accedemos a un widget mapa de Ubidots que traza los distintos puntos de localización que ha detectado el dispositvo

# Eficiencia

Como hemos visto anteriormente, podemos desactivar que información ha de enviarse. También el usuario puede elegir que modalidad de locación permitiendo jugar entre la precisión y el ahorro energético. Sería interesante añadir la funcionalidad de enviar el consumo de batería y que servicios estan en funcionamiento para estudios posteriores.

# Conclusión
Como desarrolladora el librarte de crear un backend para gestionar toda la lectura de datos ahorra mucho trabajo. Ubidots permite cómodamente recopilar y difundir información. Como campos de aplicación podría destacar los siguientes:

- **Domótica**
	* Desde Ubidots una app obteien la distancia del usuario a su residencia. De esta forma podemos encender el termoestato para que cuando llegue a casa se sienta cómodo.
	* regular la iluminación de una estancia dependiendo de la cantidad de luz exterior.

- **Aseguradoras**
	* Si una empresa tiene asegurado una cámara frigorífica puede que esta se averíe devido a uno indebido como dejar la puerta abierta y así forzar al dispositivo a mantener una misma temperatura. Se puede colocar un sensor de temperatura que envié la temperatura a Ubidots y hacer un disparador para que avise cuando la camara le cueste llegue a una temperatura.
	* Los cuadros de un museo pueden deteriorarse con la humedad e iluminación. Otro disparador podría avisar cuando empiecen haber valores peligrosos.

- **Estudio de hábitos**
	* Un usuario esta en un centro comercial y se descarga su aplicación. Con los geocercados detectar en que zonas está, enviarlo a la plataforma y tener un conocimiento de que zonas esta más tiempo o cuales no visita. Para incentivar al usuario que se lo descargue

- **Control de variables ambientales**
	* Tenemos un invernadero y hemos de asegurarnos la cantidad adecuada de iluminación, temperatura y humedad. Se envian a ubidots los datos y desde otra aplicación los consulta o se envían para regular dichas variables.
