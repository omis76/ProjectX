# Security Configuration Guide

## Overview
This project implements comprehensive security measures to protect sensitive data and API communications.

## 🔐 Security Features Implemented

### 1. API Key Protection
- ✅ Google Maps API key moved to `gradle.properties` (excluded from version control)
- ✅ BuildConfig integration for secure key injection
- ✅ Environment-specific configurations

### 2. Network Security
- ✅ Cleartext traffic disabled in production builds
- ✅ Network Security Configuration with certificate pinning
- ✅ Environment-specific base URLs
- ✅ Proper TLS/SSL enforcement

### 3. Build Variants
- ✅ **Debug**: Allows local development with cleartext traffic
- ✅ **Release**: Enforces HTTPS and secure configurations

### 4. Development Tools
- ✅ Chucker integration for network debugging (debug builds only)
- ✅ HTTP logging with sensitive header redaction

## 🔧 Setup Instructions

### 1. Initial Setup
```bash
# Copy the template file
cp gradle.properties.template gradle.properties

# Update with your actual values
# Edit gradle.properties and replace placeholder values
```

### 2. Required Configuration
Edit `gradle.properties` with your actual values:
```properties
GOOGLE_MAPS_API_KEY=your_actual_api_key_here
DEV_BASE_URL="http://10.0.2.2:8000/"
STAGING_BASE_URL="https://staging-api.yourcompany.com/"
PROD_BASE_URL="https://api.yourcompany.com/"
```

### 3. Network Security (Production)
Update `app/src/main/res/xml/network_security_config.xml`:
- Replace certificate pins with your actual SSL certificate pins
- Update domain configurations for your production API

## 🌍 Environment Configurations

### Debug Build
- **Application ID**: `com.onkar.projectx.debug`
- **Base URL**: Local development server
- **Cleartext Traffic**: Allowed (for local development)
- **API Debugging**: Enabled (Chucker + HTTP logging)

### Release Build
- **Application ID**: `com.onkar.projectx`
- **Base URL**: Production API
- **Cleartext Traffic**: Disabled
- **Code Minification**: Enabled
- **API Debugging**: Disabled

## 🛡️ Security Best Practices

### 1. Secret Management
- Never commit `gradle.properties` to version control
- Use environment variables for CI/CD pipelines
- Rotate API keys regularly

### 2. Certificate Pinning
- Update certificate pins before expiration
- Monitor certificate changes
- Implement backup pins

### 3. Network Communications
- Always use HTTPS in production
- Validate SSL certificates
- Implement proper error handling for network failures

## 🚨 Security Checklist

Before deploying to production:

- [ ] API keys are not hardcoded in source code
- [ ] `gradle.properties` is in `.gitignore`
- [ ] Certificate pins are updated and valid
- [ ] Production URLs are using HTTPS
- [ ] Cleartext traffic is disabled for production
- [ ] Network security configuration is properly configured
- [ ] Debug tools are disabled in release builds

## 🔍 Debugging

### Local Development
- Use debug build variant for local development
- Chucker will show network requests in a notification
- HTTP logs will appear in Android Studio console

### Network Issues
- Check network security configuration
- Verify certificate pins are correct
- Ensure proper domain configurations

## 📱 Build Commands

```bash
# Debug build (with network debugging)
./gradlew assembleDebug

# Release build (production-ready)
./gradlew assembleRelease

# Install debug build
./gradlew installDebug
```

## 🔄 Certificate Pin Generation

To generate certificate pins for your domain:
```bash
# Using openssl
echo | openssl s_client -servername your-api.com -connect your-api.com:443 2>/dev/null | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | base64
```

## 🆘 Troubleshooting

### Build Errors
- Ensure `gradle.properties` exists and has all required properties
- Verify API key format is correct
- Check for typos in property names

### Network Errors in Production
- Verify certificate pins are correct
- Check domain configurations
- Ensure HTTPS URLs are accessible

### Local Development Issues
- Ensure local server is running
- Check if emulator can access localhost
- Verify cleartext traffic is allowed for debug builds

---

**⚠️ Important**: This configuration provides security for production while maintaining developer experience for local development.