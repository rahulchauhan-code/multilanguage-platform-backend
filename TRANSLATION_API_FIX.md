# Translation API - Configuration & Testing Guide

## Issue Summary
The translation API was not properly exposed to the frontend because:
1. ❌ No `TranslationController` existed 
2. ❌ Translation API URL was pointing to an unreliable service
3. ❌ Language parameter wasn't being handled in all endpoints

## Fixed Issues

### 1. **Created TranslationController** ✅
- **File**: `src/main/java/com/blog/multilanguage_platform/controller/TranslationController.java`
- **Endpoints**:
  - `GET /api/translate/text?q=<text>&lang=<language>` - Translate text
  - `GET /api/translate/debug?q=<text>&lang=<language>` - Debug translation API

### 2. **Updated Configuration** ✅
- **File**: `src/main/resources/application.properties`
- Now uses **LibreTranslate** (free, reliable public service)
- Added debug logging for translation service

### 3. **Enhanced Endpoints** ✅
- `GET /api/posts/?lang=<language>` - Now translates post categories
- `GET /api/post_contents/?lang=<language>` - Now translates titles & content
- `GET /api/posts/user/{userId}?lang=<language>` - Already supported, enhanced

## Testing the API

### Prerequisites
- Backend running on `http://localhost:8283`
- Frontend running on `http://localhost:5173`

### Test Cases

#### 1. Direct Translation Endpoint
```bash
# Translate "Hello" to Hindi
curl "http://localhost:8283/api/translate/text?q=Hello&lang=hi"

# Expected Response:
{
  "original": "Hello",
  "translated": "नमस्ते",
  "language": "hi"
}
```

#### 2. Debug Translation Endpoint
```bash
# Debug endpoint shows detailed API calls
curl "http://localhost:8283/api/translate/debug?q=Technology&lang=es"

# Returns full debug information with API calls
```

#### 3. Get Posts with Translation
```bash
# Get all posts with Spanish translation
curl "http://localhost:8283/api/posts/?lang=es"

# Categories will be automatically translated
```

#### 4. Get User Posts with Translation
```bash
# Get user's posts translated to Hindi
curl "http://localhost:8283/api/posts/user/1?lang=hi"

# Both categories and content will be translated
```

#### 5. Get Post Contents with Translation
```bash
# Get all post contents translated to French
curl "http://localhost:8283/api/post_contents/?lang=fr"

# Titles and content will be translated
```

## Supported Languages

| Code | Language |
|------|----------|
| en   | English |
| hi   | Hindi |
| es   | Spanish |
| fr   | French |
| de   | German |
| pt   | Portuguese |
| ru   | Russian |
| ar   | Arabic |
| zh   | Chinese |
| ja   | Japanese |

## Troubleshooting

### 1. Translation Returns Original Text
**Cause**: LibreTranslate service is overloaded or unreachable
**Solution**: 
- Check your internet connection
- Try alternative API in `application.properties`:
  ```properties
  translation.api.url=https://translate.argosopentech.com/translate
  ```

### 2. "Connection Refused" Error
**Cause**: Backend not running
**Solution**: 
```bash
# From multilanguage-platform directory
./mvnw spring-boot:run
# OR
mvn spring-boot:run
```

### 3. Logs Show Translation Errors
**Check logs with**:
```bash
# Tail logs from backend to see detailed translation errors
# Look for: TranslationService - DEBUG level messages
```

## Frontend Integration

The frontend is already configured to use the translation API:

### In Profile.jsx:
```javascript
// Gets posts translated to selected language
const metaRes = await api.get(`/posts/user/${userId}?lang=${lang}`);
```

### In Home.jsx:
```javascript
// Gets all posts with translation
const postsMetaRes = await api.get(`/posts/?lang=${lang}`);
const postsContentRes = await api.get(`/post_contents/?lang=${lang}`);
```

### Language Selector (Navbar.jsx):
The navbar dropdown automatically updates the `lang` parameter in the URL, triggering re-fetch with translations.

## Configuration Options

Edit `src/main/resources/application.properties`:

```properties
# Translation API URL (currently using LibreTranslate)
translation.api.url=https://libretranslate.de/translate

# API Key (optional, leave empty for public endpoints)
translation.api.key=

# Logging level for debugging
logging.level.com.blog.multilanguage_platform.services.TranslationService=DEBUG
```

## Next Steps

1. **Restart backend** with new configuration
2. **Test translation endpoints** using curl or Postman
3. **Verify in frontend** by switching languages in navbar
4. **Check browser console** for any errors
5. **Monitor backend logs** for translation details

## Files Modified

1. ✅ **TranslationController.java** (NEW)
   - Exposes translation API endpoints

2. ✅ **Post_contentController.java** (MODIFIED)
   - Added language parameter support
   - Added automatic translation

3. ✅ **PostController.java** (MODIFIED)
   - Added language parameter to GET /api/posts/

4. ✅ **application.properties** (MODIFIED)
   - Changed API URL to LibreTranslate
   - Added logging configuration

5. ✅ **TranslationService.java** (ENHANCED)
   - Better error logging
   - Debug information

---

**For production use**, consider:
- Using a self-hosted LibreTranslate instance
- Getting an API key from a professional translation service
- Caching translations to reduce API calls
- Setting up rate limiting
