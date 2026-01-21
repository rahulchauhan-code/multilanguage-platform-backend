# Render Deployment Configuration

## Backend (Spring Boot)

### Build Command:
```bash
./mvnw clean package -DskipTests
```

### Start Command:
```bash
java -Dserver.port=$PORT -jar target/multilanguage-platform-0.0.1-SNAPSHOT.jar
```

### Environment Variables:
Set these in Render dashboard (will override application.properties):

```
SPRING_DATASOURCE_URL=<Internal Database URL from Render>
SPRING_DATASOURCE_USERNAME=blogplatform_tuvn_user
SPRING_DATASOURCE_PASSWORD=Mb5sw979LEl9s4nXQeQrhmWdTyahIPZN
```

Note: Render will automatically provide the DATABASE_URL. You can use:
```
SPRING_DATASOURCE_URL=${DATABASE_URL}
```

### Important:
1. **PostgreSQL Instance**: Link your PostgreSQL database in Render dashboard
2. **Port**: Render assigns dynamic ports - use `$PORT` env variable
3. **SSL**: Internal connections don't need sslmode parameter
4. **Region**: Ensure backend and database are in the same region

## Frontend (React + Vite)

### Build Command:
```bash
cd multi-lang-blog && npm install && npm run build
```

### Publish Directory:
```
multi-lang-blog/dist
```

### Environment Variables:
```
REACT_APP_API_URL=<Your Render Backend URL>
```

Update `multi-lang-blog/src/api/axiosConfig.jsx` baseURL to use:
```javascript
baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8283/api'
```

## Deployment Checklist

- [ ] Create PostgreSQL instance on Render
- [ ] Note internal connection URL
- [ ] Create Web Service for backend
  - [ ] Connect GitHub repository
  - [ ] Set build command
  - [ ] Set start command
  - [ ] Link PostgreSQL database
  - [ ] Add environment variables
- [ ] Create Static Site for frontend
  - [ ] Connect GitHub repository
  - [ ] Set build command
  - [ ] Set publish directory
  - [ ] Update API URL to backend service
- [ ] Update CORS in backend to allow frontend domain
- [ ] Test full deployment

## CORS Configuration

Update `PostController`, `UserController`, etc. to accept your Render frontend URL:

```java
@CrossOrigin(origins = {"http://localhost:5173", "https://your-frontend.onrender.com"}, allowCredentials = "true")
```

## Database Migration

Since switching from Oracle to PostgreSQL, tables will be created automatically with `spring.jpa.hibernate.ddl-auto=update`.

No existing data will be migrated. You'll start with fresh tables.
