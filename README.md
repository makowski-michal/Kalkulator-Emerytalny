# Kalkulator Płacowy

Aplikacja webowa do zarządzania listą płac, generowania pasków płacowych i kalkulatora emerytalnego.

## Uruchomienie

### Wymagania
- Java 22 (`C:\Program Files\Java\jdk-22`)
- PostgreSQL 18 (`C:\Program Files\PostgreSQL\18\bin\`)
- Maven (pobierany automatycznie przez `mvnw.cmd`)

### 1. Utwórz bazę danych (tylko przy pierwszym uruchomieniu)
```powershell
& "C:\Program Files\PostgreSQL\18\bin\createdb.exe" -U postgres payroll_db
```
Hasło PostgreSQL: `1804`

### 2. Uruchom aplikację
```powershell
cd "e:\004 szkoła\rok 3\semestr 6\pwsi\projekt\kalkulator-placowy"
.\mvnw.cmd spring-boot:run
```

Aplikacja startuje na **http://localhost:8080**

---

## Domyślne konta

### Pracodawca (admin)
| Pole | Wartość |
|------|---------|
| Email | `admin@firma.pl` |
| Hasło | `Admin123!` |
| Rola | EMPLOYER |

> Konto tworzone automatycznie przy pierwszym starcie aplikacji.

### Pracownik
Pracownika dodaje pracodawca przez panel **Pracownicy → ➕ Dodaj pracownika**.  
Email i hasło są ustawiane przez pracodawcę przy tworzeniu konta.

---

## Baza danych

| Parametr | Wartość |
|----------|---------|
| Host | `localhost:5432` |
| Baza | `payroll_db` |
| Użytkownik | `postgres` |
| Hasło | `1804` |

Migracje Flyway uruchamiają się automatycznie przy starcie (V1–V5).

---

## Struktura projektu

```
kalkulator-placowy/
├── src/main/java/pl/edu/payroll/
│   ├── config/          # SecurityConfig, DataInitializer
│   ├── controller/      # REST API
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA encje
│   ├── repository/      # Spring Data repozytoria
│   └── service/         # Logika biznesowa
├── src/main/resources/
│   ├── static/          # Frontend (HTML + JS + CSS)
│   └── db/migration/    # Flyway SQL migracje
└── src/test/            # Testy jednostkowe (PayrollService)
```

---

## API — główne endpointy

| Metoda | Ścieżka | Opis |
|--------|---------|------|
| POST | `/api/v1/auth/login` | Logowanie |
| POST | `/api/v1/auth/logout` | Wylogowanie |
| GET | `/api/v1/employees` | Lista pracowników (EMPLOYER) |
| POST | `/api/v1/employees` | Dodaj pracownika (EMPLOYER) |
| GET | `/api/v1/employees/me` | Dane zalogowanego pracownika (EMPLOYEE) |
| GET | `/api/v1/payslips` | Historia pasków |
| POST | `/api/v1/payslips` | Generuj pasek (`?preview=true` = tylko podgląd) |
| GET | `/api/v1/payslips/{id}/pdf` | Pobierz PDF |
| GET | `/api/v1/retirement/{id}/current` | Bieżąca emerytura |
| POST | `/api/v1/retirement/{id}/forecast` | Prognoza emerytury |
| GET | `/api/v1/absences` | Nieobecności |
| GET | `/api/v1/company` | Dane firmy |

---

## Stawki ZUS/PIT 2025

| Składka | Pracownik | Pracodawca |
|---------|-----------|------------|
| Emerytalna | 9,76% | 9,76% |
| Rentowa | 1,50% | 6,50% |
| Chorobowa | 2,45% | — |
| Wypadkowa | — | 1,67% |
| Fundusz Pracy | — | 2,45% |
| FGŚP | — | 0,10% |
| Zdrowotna | 9,00% | — |
| PIT próg I / II | 12% / 32% | — |
| Ulga podatkowa | 300 zł/mies. | — |
| KUP | 250 zł/mies. | — |
