# Classroom Management System runbook

This guide describes how to run the application locally, sign in as the seeded
administrator, and reset the development data.

## 1. Prerequisites

Install a JDK compatible with Java 8 (JDK 8, 11, or 17 is recommended), Docker
Desktop, and Git. Set `JAVA_HOME` to the JDK installation directory, not its
`bin` directory. For example:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

## 2. Start MySQL

The application connects to MySQL on port 3306. Start the development
container with the database name and credentials expected by the application:

```powershell
docker run --name cms-mysql -p 3306:3306 `
  -e MYSQL_ROOT_PASSWORD=root `
  -e MYSQL_DATABASE=cms_db `
  -e MYSQL_USER=cms_user `
  -e MYSQL_PASSWORD=cms_password `
  -d mysql:5.7.29
```

If the container already exists, use `docker start cms-mysql` instead. Wait
until `docker logs cms-mysql` reports that MySQL is ready.

## 3. Run the application

From the repository root, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8082](http://localhost:8082) once startup completes.

## 4. Administrator account

On first startup the application seeds this development administrator if it
does not already exist:

| Field | Value |
| --- | --- |
| User type | `admin` |
| Username | `azadchaudhary03@gmail.com` |
| Password | `Admin@123` |

Select **admin** on the login screen and use the credentials above. The seeded
account is meant for local development only; change the password and use a
secure secret-management mechanism before deploying anywhere public.

## 5. Sample data

The application also seeds development-only sample classrooms, class timings,
and accounts when their records do not exist. This makes it possible to try the
classroom search, timetable, and request flows immediately after startup.

## 6. Run tests

In a second terminal, with the same `JAVA_HOME` setting, run:

```powershell
.\mvnw.cmd test
```

## 7. Reset local development data

This permanently removes the local database and recreates it on the next
startup:

```powershell
docker rm -f cms-mysql
```

Then repeat step 2 and step 3.
