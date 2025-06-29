# Postman API Testing Sample Data

This document provides sample data for testing all APIs in the Inzira Ticket System using Postman.

## Base URL
```
http://localhost:8080
```

## 1. Admin APIs

### 1.1 Register Admin
**POST** `/api/admins/register`

```json
{
    "username": "admin1",
    "email": "admin@inzira.com",
    "phoneNumber": "+250788123456",
    "password": "Admin123!"
}
```

### 1.2 Create District
**POST** `/api/admin/districts`

```json
{
    "name": "Kigali"
}
```

```json
{
    "name": "Musanze"
}
```

```json
{
    "name": "Huye"
}
```

### 1.3 Add Route Points to Districts
**POST** `/api/admin/districts/1/points`

```json
{
    "name": "Nyabugogo Bus Station",
    "gpsLat": -1.9441,
    "gpsLong": 30.0619
}
```

**POST** `/api/admin/districts/1/points`

```json
{
    "name": "Kimisagara",
    "gpsLat": -1.9706,
    "gpsLong": 30.0588
}
```

**POST** `/api/admin/districts/2/points`

```json
{
    "name": "Musanze Bus Park",
    "gpsLat": -1.4991,
    "gpsLong": 29.6379
}
```

**POST** `/api/admin/districts/3/points`

```json
{
    "name": "Huye Bus Station",
    "gpsLat": -2.5967,
    "gpsLong": 29.7394
}
```

### 1.4 Create Routes
**POST** `/api/admin/routes`

```json
{
    "origin": {
        "id": 1
    },
    "destination": {
        "id": 2
    },
    "distanceKm": 116.5
}
```

```json
{
    "origin": {
        "id": 1
    },
    "destination": {
        "id": 3
    },
    "distanceKm": 135.2
}
```

### 1.5 Register Agency
**POST** `/api/admin/agencies`
**Content-Type**: `multipart/form-data`

Form Data:
- `agencyName`: "Volcano Express"
- `email`: "info@volcanoexpress.rw"
- `phoneNumber`: "+250788987654"
- `address`: "KN 3 Ave, Kigali"
- `status`: "ACTIVE"
- `password`: "Agency123!"
- `image`: [Upload a logo file]

```json
{
    "agencyName": "Ritco Express",
    "email": "contact@ritco.rw",
    "phoneNumber": "+250788876543",
    "address": "KG 11 Ave, Kigali",
    "status": "ACTIVE",
    "password": "Ritco123!"
}
```

## 2. Agency APIs

### 2.1 Create Bus
**POST** `/api/agency/buses`

```json
{
    "plateNumber": "RAD 123 A",
    "busType": "VIP",
    "capacity": 30,
    "agency": {
        "id": 1
    }
}
```

```json
{
    "plateNumber": "RAD 456 B",
    "busType": "Normal",
    "capacity": 45,
    "agency": {
        "id": 1
    }
}
```

### 2.2 Create Driver
**POST** `/api/agency/drivers`

```json
{
    "firstName": "Jean",
    "lastName": "Uwimana",
    "email": "jean.uwimana@volcanoexpress.rw",
    "phoneNumber": "+250788111222",
    "licenseNumber": "DL001234",
    "agency": {
        "id": 1
    }
}
```

```json
{
    "firstName": "Marie",
    "lastName": "Mukamana",
    "email": "marie.mukamana@volcanoexpress.rw",
    "phoneNumber": "+250788333444",
    "licenseNumber": "DL005678",
    "agency": {
        "id": 1
    }
}
```

### 2.3 Create Agency Route
**POST** `/api/agency/routes`

```json
{
    "agencyId": 1,
    "routeId": 1,
    "price": 2500.0,
    "pickupPointIds": [1, 2],
    "dropPointIds": [3]
}
```

```json
{
    "agencyId": 1,
    "routeId": 2,
    "price": 3000.0,
    "pickupPointIds": [1, 2],
    "dropPointIds": [4]
}
```

### 2.4 Create Schedule
**POST** `/api/agency/schedules`

```json
{
    "agencyRoute": {
        "id": 1
    },
    "bus": {
        "id": 1
    },
    "driver": {
        "id": 1
    },
    "departureDate": "2024-07-15",
    "departureTime": "08:00:00",
    "arrivalTime": "10:30:00"
}
```

```json
{
    "agencyRoute": {
        "id": 1
    },
    "bus": {
        "id": 2
    },
    "driver": {
        "id": 2
    },
    "departureDate": "2024-07-15",
    "departureTime": "14:00:00",
    "arrivalTime": "16:30:00"
}
```

```json
{
    "agencyRoute": {
        "id": 2
    },
    "bus": {
        "id": 1
    },
    "driver": {
        "id": 1
    },
    "departureDate": "2024-07-16",
    "departureTime": "09:00:00",
    "arrivalTime": "12:00:00"
}
```

## 3. Customer APIs

### 3.1 Register Customer
**POST** `/api/customers/register`

```json
{
    "firstName": "Alice",
    "lastName": "Uwimana",
    "email": "alice.uwimana@gmail.com",
    "phoneNumber": "+250788555666",
    "password": "Customer123!"
}
```

```json
{
    "firstName": "Bob",
    "lastName": "Nkurunziza",
    "email": "bob.nkurunziza@gmail.com",
    "phoneNumber": "+250788777888",
    "password": "Customer456!"
}
```

### 3.2 Create Booking
**POST** `/api/bookings`

```json
{
    "customer": {
        "id": 1
    },
    "schedule": {
        "id": 1
    },
    "pickupPoint": {
        "id": 1
    },
    "dropPoint": {
        "id": 3
    },
    "numberOfSeats": 2
}
```

```json
{
    "customer": {
        "id": 2
    },
    "schedule": {
        "id": 2
    },
    "pickupPoint": {
        "id": 2
    },
    "dropPoint": {
        "id": 3
    },
    "numberOfSeats": 1
}
```

## 4. Search and Query APIs

### 4.1 Search Schedules
**GET** `/api/agency/schedules/search?originId=1&destinationId=2&departureDate=2024-07-15`

### 4.2 Get All Districts
**GET** `/api/admin/districts`

### 4.3 Get Route Points by District
**GET** `/api/admin/districts/1/points`

### 4.4 Get All Routes
**GET** `/api/admin/routes`

### 4.5 Get All Agencies
**GET** `/api/admin/agencies`

### 4.6 Get Buses by Agency
**GET** `/api/agency/buses/agency/1`

### 4.7 Get Drivers by Agency
**GET** `/api/agency/drivers/agency/1`

### 4.8 Get Schedules by Agency
**GET** `/api/agency/schedules/agency/1`

### 4.9 Get Bookings by Customer
**GET** `/api/bookings/customer/1`

### 4.10 Get Booking by Reference
**GET** `/api/bookings/reference/{bookingReference}`

## 5. Update Operations

### 5.1 Update District
**PUT** `/api/admin/districts/1`

```json
{
    "name": "Kigali City"
}
```

### 5.2 Update Route Point
**PUT** `/api/admin/districts/1/points/1`

```json
{
    "name": "Nyabugogo Central Bus Station",
    "gpsLat": -1.9441,
    "gpsLong": 30.0619,
    "district": {
        "id": 1
    }
}
```

### 5.3 Update Agency
**PUT** `/api/admin/agencies/1`
**Content-Type**: `multipart/form-data`

Form Data:
- `agency`: 
```json
{
    "agencyName": "Volcano Express Ltd",
    "phoneNumber": "+250788987654",
    "address": "KN 3 Ave, Nyarugenge, Kigali",
    "status": "ACTIVE"
}
```
- `logo`: [Upload new logo file - optional]

### 5.4 Update Bus
**PUT** `/api/agency/buses/1`

```json
{
    "plateNumber": "RAD 123 A",
    "busType": "VIP Plus",
    "capacity": 32,
    "status": "ACTIVE"
}
```

### 5.5 Update Customer
**PUT** `/api/customers/1`

```json
{
    "firstName": "Alice",
    "lastName": "Uwimana-Gasana",
    "email": "alice.gasana@gmail.com",
    "phoneNumber": "+250788555666"
}
```

### 5.6 Confirm Booking
**PUT** `/api/bookings/1/confirm`

### 5.7 Cancel Booking
**PUT** `/api/bookings/1/cancel`

### 5.8 Cancel Schedule
**PUT** `/api/agency/schedules/1/cancel`

## 6. Password Reset Operations

### 6.1 Reset Agency Password
**POST** `/api/admin/agencies/1/reset-password`

### 6.2 Reset Driver Password
**POST** `/api/agency/drivers/1/reset-password`

## 7. Delete Operations

### 7.1 Delete District
**DELETE** `/api/admin/districts/1`

### 7.2 Delete Route Point
**DELETE** `/api/admin/districts/1/points/1`

### 7.3 Delete Route
**DELETE** `/api/admin/routes/1`

### 7.4 Delete Agency
**DELETE** `/api/admin/agencies/1`

### 7.5 Delete Bus
**DELETE** `/api/agency/buses/1`

### 7.6 Delete Driver
**DELETE** `/api/agency/drivers/1`

### 7.7 Delete Schedule
**DELETE** `/api/agency/schedules/1`

### 7.8 Delete Customer
**DELETE** `/api/customers/1`

## Testing Flow Recommendations

### Recommended Testing Order:

1. **Setup Phase**:
   - Register Admin
   - Create Districts (Kigali, Musanze, Huye)
   - Add Route Points to each District
   - Create Routes between Districts
   - Register Agencies

2. **Agency Setup**:
   - Create Buses for agencies
   - Create Drivers for agencies
   - Create Agency Routes (linking agencies to routes with pricing)

3. **Schedule Management**:
   - Create Schedules for different dates and times
   - Test schedule search functionality

4. **Customer Operations**:
   - Register Customers
   - Create Bookings
   - Test booking confirmation and cancellation

5. **Management Operations**:
   - Test update operations
   - Test password resets
   - Test delete operations (be careful with order due to foreign key constraints)

## Important Notes:

1. **Foreign Key Dependencies**: Always create parent entities before child entities
2. **File Uploads**: For agency registration, use `multipart/form-data` and include an image file
3. **Date Formats**: Use ISO date format (YYYY-MM-DD) for dates
4. **Time Formats**: Use HH:mm:ss format for times
5. **Status Values**: Use predefined status values like "ACTIVE", "INACTIVE", "SCHEDULED", "CANCELLED", etc.
6. **Booking References**: Are auto-generated, so use the returned reference for lookups

## Error Testing:

Test these scenarios to verify error handling:
- Duplicate email registrations
- Invalid foreign key references
- Booking with insufficient seats
- Updating non-existent entities
- Deleting entities with dependencies