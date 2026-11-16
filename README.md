# loginUsers

Aplicación de escritorio en **Java (Swing)** para registro e inicio de sesión de usuarios, con base de datos **MySQL**. Incluye un panel para listar, editar y eliminar usuarios registrados.

> **Nota sobre el idioma:** la documentación está en español. El código fuente (clases, métodos, variables y comentarios) está escrito en inglés.

---

## Funcionalidades

- **Inicio de sesión** con usuario y contraseña oculta (el usuario no distingue mayúsculas/minúsculas). Si falta algún campo muestra: *"Debe ingresar su usuario y contraseña, si no está registrado debe registrarse."*
- **Registro de usuarios** con nombre de usuario, nombre, apellido, teléfono, correo, contraseña y confirmación. Todos los campos son obligatorios y el mensaje indica cuál falta. Avisa si la contraseña y la confirmación no coinciden.
- **Pantalla principal** ("Clientes Registrados") con la tabla de usuarios y los botones **Nuevo**, **Actualizar**, **Eliminar** y **Cerrar sesión**. La tabla se actualiza sola después de cada cambio. También se puede actualizar con doble clic en una fila.
- **Cerrar sesión** vuelve a la pantalla de login.
- **Contraseñas seguras:** nunca se guardan en texto plano. Se cifran con **PBKDF2 (HMAC-SHA256)**, 600 000 iteraciones y una *salt* aleatoria por usuario.
- **Validaciones:** campos obligatorios, correo válido, contraseña de mínimo 6 caracteres, usuario y correo únicos al registrar y al actualizar.
- **Mensajes de error genéricos** en el login ("usuario o contraseña inválidos") para no revelar qué cuentas existen.

---

## Estructura del proyecto

```
loginUsers/
├── .vscode/
│   ├── launch.json          # Configuraciones para ejecutar desde VS Code
│   └── settings.json        # Agrega el driver de MySQL (lib/) al proyecto
├── lib/
│   └── mysql-connector-j-9.7.0.jar   # Driver JDBC de MySQL
├── sql/
│   └── schemas.sql          # Script que crea la base de datos y la tabla
├── src/
│   ├── app/                 # Puntos de entrada (Main, prueba por consola)
│   ├── DAO/                 # Acceso a datos (interfaz DAOUser + DAOMySQL)
│   ├── models/              # Modelos: Person (abstracta) y User
│   ├── service/             # Lógica de negocio (UserService)
│   ├── utils/               # Conexión a BD y cifrado de contraseñas
│   └── view/                # Ventanas Swing
├── videos/
│   └── demostracion.mp4     # Video de demostración
├── .env.example             # Plantilla de configuración de la BD
├── docker-compose.yml       # (Opcional) MySQL en Docker
└── README.md
```

### Arquitectura en capas

```
view  →  service  →  DAO  →  MySQL
            ↓
          utils (SureKeys, ConnectionDB)
```

| Capa | Clase | Responsabilidad |
|------|-------|-----------------|
| Vista | `LoginWindow`, `UserRegistrationWindow`, `MainWindow`, `UserEditDialog`, `UiStyle` | Interfaz gráfica |
| Servicio | `UserService` (Singleton) | Validaciones, login, registro, CRUD |
| Datos | `DAOUser` (interfaz), `DAOMySQL`, `DAOFactory` (Fábrica) | Consultas SQL a la tabla `users` |
| Modelo | `Person`, `User` | Datos del usuario |
| Utilidades | `ConnectionDB` | Abre la conexión leyendo variables de entorno |
| Utilidades | `SureKeys` | Genera y verifica hashes de contraseñas |

---

## Programación orientada a objetos

| Pilar | Dónde se aplica |
|-------|-----------------|
| **Abstracción** | `Person` es una clase abstracta (no se puede instanciar). `DAOUser` es una interfaz que define *qué* operaciones existen sin decir *cómo* se hacen. |
| **Encapsulamiento** | Todos los atributos de `Person` y `User` son `private` y se acceden con getters/setters que validan los datos. `SureKeys` oculta el algoritmo de cifrado (`pbkdf2` es privado). |
| **Herencia** | `User extends Person`: hereda nombre, apellido, teléfono, correo y `getFullName()`, y agrega `id`, `userName` y `hashKey`. |
| **Polimorfismo** | `UserService` trabaja con `DAOUser`, sin saber que la implementación real es `DAOMySQL` (los métodos llevan `@Override`). Además, `updateUser` está sobrecargado: `updateUser(User)` y `updateUser(User, String)`. |

## Patrones de diseño

| Patrón | Clase | Uso |
|--------|-------|-----|
| **Singleton** | `UserService` | Constructor privado y una sola instancia compartida, que se obtiene con `UserService.getInstance()`. |
| **Fábrica (Factory)** | `DAOFactory` | `DAOFactory.createUserDAO()` crea el `DAOUser` que usa la app. Ninguna otra clase hace `new DAOMySQL()`, así que cambiar de base de datos solo requiere tocar la fábrica. |

---

## Requisitos

- **Java JDK 17** o superior
- **VS Code** con la extensión **Extension Pack for Java**
- **MySQL 8** (instalado localmente) **o** **Docker** (ver opción B)

---

## Sobre la base de datos

GitHub solo guarda el **código**, no la base de datos. MySQL corre en la computadora de cada persona, así que quien clone el proyecto debe:

1. Tener su propio servidor MySQL (instalado o en Docker).
2. Crear la base de datos con `sql/schemas.sql`.
3. Poner **sus propias** credenciales en un archivo `.env`.

Por eso el archivo `.env` (con tus credenciales reales) **no se sube**, y en su lugar se incluye `.env.example` como plantilla.

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/loginUsers.git
cd loginUsers
```

### 2. Crear el archivo `.env`

Copia la plantilla:

```bash
# Windows (PowerShell)
Copy-Item .env.example .env

# Linux / macOS
cp .env.example .env
```

Abre `.env` y escribe tus datos de MySQL:

```env
loginUsers_DB_URL=jdbc:mysql://localhost:3306/loginUsers
loginUsers_DB_USUARIO=root
loginUsers_DB_CLAVE=tu_clave
```

| Variable | Descripción |
|----------|-------------|
| `loginUsers_DB_URL` | URL JDBC de la base de datos |
| `loginUsers_DB_USUARIO` | Usuario de MySQL |
| `loginUsers_DB_CLAVE` | Contraseña de MySQL |

### 3. Preparar la base de datos

#### Opción A: MySQL instalado en tu PC

Ejecuta el script `sql/schemas.sql`, por ejemplo desde MySQL Workbench (*File → Run SQL Script*) o por terminal:

```bash
mysql -u root -p < sql/schemas.sql
```

#### Opción B: MySQL con Docker

No necesitas instalar MySQL. Con Docker Desktop abierto:

```bash
docker compose up -d
```

Esto crea un contenedor de MySQL en el **puerto 3307** (para no chocar con un MySQL local) y ejecuta `schemas.sql` automáticamente la primera vez. La contraseña de `root` será la que pusiste en `loginUsers_DB_CLAVE`.

En tu `.env`, cambia la URL al puerto 3307:

```env
loginUsers_DB_URL=jdbc:mysql://localhost:3307/loginUsers
```

Para detenerlo: `docker compose down` (los datos se conservan en un volumen).

### 4. Ejecutar la aplicación

1. Abre **la carpeta `loginUsers`** en VS Code (*File → Open Folder*). Importante: ábrela directamente, no una carpeta superior, para que se use su `.vscode/launch.json`.
2. Ve a **Run and Debug** (`Ctrl+Shift+D`).
3. Elige una configuración y presiona ▶:

| Configuración | Qué abre |
|---------------|----------|
| `Main (Login)` | Ventana de inicio de sesión (recomendado) |
| `LoginWindow` | Ventana de inicio de sesión directamente |
| `UserRegistrationWindow` | Ventana de registro |
| `LoginConsoleTest` | Prueba de login por consola |

Todas leen las credenciales desde `.env`.

---

## Esquema de la base de datos

Tabla `users`:

| Columna | Tipo | Notas |
|---------|------|-------|
| `id` | INT | Clave primaria, AUTO_INCREMENT |
| `user_name` | VARCHAR(50) | Único, obligatorio |
| `name` | VARCHAR(80) | Obligatorio |
| `last_name` | VARCHAR(80) | Obligatorio |
| `phone_number` | VARCHAR(30) | Obligatorio |
| `email` | VARCHAR(150) | Obligatorio |
| `hash_key` | VARCHAR(255) | Contraseña cifrada (`iteraciones:salt:hash`) |

---

## Solución de problemas

| Error | Causa probable | Solución |
|-------|----------------|----------|
| `Missing local MySQL configuration...` | No existe `.env` o falta alguna variable | Crea `.env` a partir de `.env.example` |
| `Communications link failure` | MySQL no está corriendo o el puerto es incorrecto | Inicia MySQL / Docker y revisa el puerto en la URL |
| `Access denied for user` | Usuario o contraseña incorrectos | Revisa `loginUsers_DB_USUARIO` y `loginUsers_DB_CLAVE` |
| `Unknown database 'loginUsers'` | No se ejecutó `schemas.sql` | Ejecuta el script (paso 3) |
| `No suitable driver found` | VS Code no encuentra el `.jar` | Abre la carpeta `loginUsers` directamente en VS Code |

---

## Videos

| Video | Contenido | Enlace |
|-------|-----------|--------|
| Demostración (2:43 min) | La aplicación en funcionamiento: login, registro, actualizar, eliminar y cerrar sesión | [Ver en Drive](https://drive.google.com/file/d/1ilTWNeOO7jpt1MO-ZyA-k7r-3wOJs2IM/view?usp=sharing) · [archivo en el repo](videos/demostracion.mp4) |
| Explicación del código (22 min) | Explicación detallada del código, la POO y los patrones de diseño | [Ver video](https://drive.google.com/file/d/1w09qn3AES74N2EXYUP_irSY1Ra-m4g_F/view?usp=sharing) |

---

## Autor

**Joel David Montilla Martínez**
Estudiante del Instituto Tecnológico de las Américas (ITLA)
