# GESTIÓN DE NOVELAS CON FUNCIONES GEOGRÁFICAS

## Objetivo

El objetivo del proyecto es ampliar la funcionalidad de las prácticas anteriores de Gestion de Novelas.
Para ello se deberán incluir algunas funcionalidades geográficas, como la ubicación del usuario a tiempo real, 
un mapa para visualizar la ubicación de las novelas, y geocodificación para convertir nombres de países/ciudades en coordenadas.

## Firebase

La aplicación utiliza Firebase como base de datos para almacenar las novelas,
base de datos a la que se han añadido 20 novelas de ejemplo para probar la applicación.
`FirebaseHelper` gestiona todas las operaciones relacionadas con la base de datos,
permitiendo que las novelas se sincronicen y persistan a través de diferentes sesiones de la aplicación.

**Novelas añadidas a Firebase como ejemplo** (IMPORTANTE ESCRIBIR EL TITULO LITERAL AL AÑADIR NOVELA para probar la aplicación.)

   - Don Quijote de la Mancha
   - La Odisea
   - La Iliada
   - Crimen y Castigo
   - Orgullo y Prejuicio
   - El Retrato de Dorian Gray
   - Matar a un ruiseñor
   - 1984
   - Moby Dick
   - El Gran Gatsby
   - Ulises
   - Madame Bovary
   - La Metamorfosis
   - En busca del tiempo perdido
   - El guardián entre el centeno
   - El señor de los anillos
   - Cien Años de Soledad
   - Fahrenheit 451
   - Las uvas de la ira
   - El amor en los tiempos del cólera

## Clases JAVA

1. **MainActivity**:
    - Controla la actividad principal de la aplicación, donde se muestran las novelas.
    - Maneja la lógica para añadir y eliminar novelas, así como para mostrar detalles de cada novela o navegar al fragmento del mapa.
    - Contiene métodos para interactuar con el `ListView` de novelas y gestionar la sincronización con Firebase.

2. **FirebaseHelper**:
    - Realiza operaciones con Firebase.
    - Contiene métodos para cargar novelas desde Firebase en segundo plano.

3. **PreferencesManager**:
    - Gestiona las preferencias de la aplicación del usuario.
    - Contiene métodos para guardar y cargar novelas en `SharedPreferences`.

4. **MapaFragment**:
    - Representa el fragmento que muestra un mapa con la ubicación de las novelas y del usuario.
    - Contiene métodos para inicializar y actualizar el `MapView`.
    - Además, en este fragmento se desactivan los botones de añadir y eliminar novelas para evitar problemas.

5. **DetallesNovelasFragment**:
    - Representa el fragmento que muestra los detalles de una novela.
    - Permite a los usuarios marcar una novela como favorita y agregar una ubicación a esa novela.

6. **ListaNovelasFragment**:
    - Representa el fragmento que muestra la lista de novelas.
    - Contiene métodos para actualizar la lista de novelas y manejar eventos de clic en los elementos de la lista.

7. **ListaFavoritasFragment**:
    - Representa el fragmento que muestra la lista de novelas favoritas.
    - Contiene métodos para actualizar la lista de novelas favoritas y manejar eventos de clic en los elementos de la lista.

8. **Novela**:
    - Clase que representa una novela.
    - Implementa `Parcelable` para permitir el paso de objetos entre actividades.

9. **NovelaAdapter**:
    - Adaptador para mostrar la lista de novelas en un `RecyclerView`.
    - Contiene métodos para enlazar los datos de las novelas con los elementos de la vista.

## Archivos XML

1. **activity_main.xml**:
    - Define el diseño de la actividad principal, incluyendo el contenedor de fragmentos y los botones de navegación.

2. **agregar_novela.xml**:
    - Define el diseño del diálogo para añadir una novela a la lista.

3. **dialog_add_location.xml**:
    - Define el diseño del diálogo para agregar una ubicación a una novela.

4. **eliminar_novela.xml**:
    - Define el diseño del diálogo para eliminar una novela de la lista.

5. **fragment_detalles_novela.xml**:
    - Define el diseño del fragmento que muestra los detalles de una novela.

6. **fragment_lista_favoritas.xml**:
    - Define el diseño del fragmento que muestra la lista de novelas favoritas.

7. **fragment_lista_novelas.xml**:
    - Define el diseño del fragmento que muestra la lista de novelas.

8. **fragment_mapa.xml**:
    - Define el diseño del fragmento que muestra el mapa con la ubicación de las novelas y del usuario.

9. **item_novela.xml**:
    - Define el diseño de cada elemento de la lista de novelas en el `RecyclerView`.

## Link al repositorio
https://github.com/Samuu10/NovelasGeografico.git
