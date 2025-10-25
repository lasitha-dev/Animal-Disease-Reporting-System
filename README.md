# 🐄 Animal Disease Reporting System (ADRS)

The **Animal Disease Reporting System (ADRS)** is a **web-based application** developed to assist **veterinary officers** and **administrators** in Sri Lanka with the centralized collection, management, and analysis of animal disease data.  

This system enables **Field** and **District Veterinary Officers** to record disease outbreaks, link them to specific farms, and visualize disease spread through analytical dashboards and geolocation mapping.

---

## 🧩 Tech Stack

| Component | Technology |
|------------|-------------|
| **Backend Framework** | Spring Boot (Java) |
| **Frontend Template Engine** | Thymeleaf |
| **Database (Primary)** | PostgreSQL |
| **Database (Secondary)** | MySQL |
| **Operating System** | Linux (Virtual Machine) |
| **Storage** | 100 GB |
| **RAM** | 8 GB |
| **Remote Logging** | Enabled |

---

## ⚙️ Core Features

### 🧑‍💼 Role-Based Access Control
- Three roles: **Admin**, **Field Veterinary**, and **District Veterinary**.
- Access control determines what data each role can view or manage.

### 🏠 Farm Management (CRUD)
- Create, read, update, and delete farms.
- Record GPS coordinates for each farm.
- Manage farmer information and farm type.

### 🐄 Animal Management (CRUD)
- Add and manage animals within a farm.
- Each animal can have multiple disease records linked to it.

### 🦠 Disease Management (CRUD)
- Report diseases for animals.
- Dropdown disease selection — dynamically filtered by animal type.
- Admin maintains master disease lists.

### 🗺️ Interactive Map Visualization
- Central map of **Sri Lanka** with farms as markers.
- Filter map by:
  - Animal Type  
  - Disease  
  - Farm / District / Province
- Clickable markers display disease details.

### 📊 Dashboards & Analytics
- Visual representation of disease trends using charts and graphs.
- Admin overview of all farms and cases.
- District Vet summary for their region.
- Field Vet dashboard for farms under their care.

### 📁 Data Filtering & Reporting
- Generate and export reports by:
  - Animal type  
  - Disease  
  - District / Province  
  - Date range

---

## 🗺️ System Overview

User Login → Dashboard → Farm List → Farm Details → Animals → Disease Reporting → Map & Analytics


- **Admin:** Manages diseases, users, and system-wide data.  
- **Field Vet:** Records and updates farm and disease data.  
- **District Vet:** Monitors outbreaks and generates analytical reports.  

---

## 🧰 Setup Instructions

```bash
1️⃣ Clone the Repository

git clone https://github.com/<your-username>/animal-disease-reporting-system.git
cd animal-disease-reporting-system

2️⃣ Configure the Database

Update the src/main/resources/application.properties file with your database credentials.
Below are sample configurations for PostgreSQL and MySQL.

PostgreSQL Configuration

spring.datasource.url=jdbc:postgresql://localhost:5432/adrsdb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

MySQL Configuration

spring.datasource.url=jdbc:mysql://localhost:3306/adrsdb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

3️⃣ Build and Run the Application

./mvnw spring-boot:run

4️⃣ Access the Application

http://localhost:8080

```
### 🧠 Future Enhancements

- Real-time alerts for disease outbreaks.

- Predictive analytics and machine learning-based spread forecasting.

- Integration with mobile reporting app for field veterinarians.

- Automated government reporting and data export system.

