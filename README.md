# My Habits
Applicación para Android de ejemplo para mostrar como se usan y funcionas diversos servicios de Google para Android (localización, detección de actividades y comprobar si el usuario esta dentro de una zona geográfica concreta). También hay un par de ejemplos de consumo de servicios REST, entre ellos se destaca en envío de información a tu cuenta de Ubidots. Este servicio web permite envio masivo y cíclico de información que posteriormente podemos formatearla en gráficas, tablas, crear eventos para que avisen otras aplicaciones, etc.

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

Coming soon...

# Eficiencia

Como hemos visto anteriormente, podemos desactivar que información ha de enviarse. También el usuario puede elegir que modalidad de locación permitiendo jugar entre la precisión y el ahorro energético. Sería interesante añadir la funcionalidad de enviar el consumo de batería y que servicios estan en funcionamiento para estudios posteriores.

# Conclusión
Como desarrolladora el librarte de crear un backend para gestionar toda la lectura de datos ahorra mucho trabajo. Como campos de aplicación podría destacar los siguientes:

- **Domótica**
	* Dependiendo de la distancia del usuario a una hubicación concreta, activar el termoestato para adecuar la temperatura.
	* Dependiendo de la cantidad de luz exterior regular la cantidad de luz interna de  una estancia

- **Aseguradoras**


- **Estudio de hábitos**
- **Control de variables ambientales**
