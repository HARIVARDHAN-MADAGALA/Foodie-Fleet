# 🔒 Security Setup - JWT Secret Configuration

## ⚠️ CRITICAL: JWT Secret Management

**The JWT secret is the most critical security component!**

### Why JWT_SECRET Must Be Secret:
- ❌ **NEVER** hard-code JWT secrets in code
- ❌ **NEVER** commit secrets to Git
- ❌ **NEVER** share secrets publicly

**If exposed:** Anyone can create forged JWT tokens with ANY role (including ADMIN)!

---

## 🔑 Setting Up JWT Secret

### Option 1: Environment Variables (Recommended)

#### Windows (IntelliJ):
1. Open **Run → Edit Configurations**
2. Select your service (e.g., UserServiceApplication)
3. Add Environment Variables:
```
JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
JWT_EXPIRATION=3600000
```

#### Mac/Linux (IntelliJ):
1. Open **Run → Edit Configurations**
2. Select your service
3. Add Environment Variables:
```
JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
JWT_EXPIRATION=3600000
```

#### Terminal/Command Line:
```bash
# Windows (CMD)
set JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
set JWT_EXPIRATION=3600000

# Windows (PowerShell)
$env:JWT_SECRET="myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator"
$env:JWT_EXPIRATION="3600000"

# Mac/Linux
export JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
export JWT_EXPIRATION=3600000
```

---

### Option 2: IntelliJ Run Configuration (For Development)

1. **Right-click** on service main class (e.g., `UserServiceApplication.java`)
2. Select **Modify Run Configuration**
3. Click **Modify options** → **Environment variables**
4. Add variables:
   - Name: `JWT_SECRET`
   - Value: `myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator`
5. Click **OK**

**Repeat for both User Service and Order Service!**

---

### Option 3: System Properties (Development Only)

Add to IntelliJ VM Options:
```
-DJWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
-DJWT_EXPIRATION=3600000
```

---

## 🔐 Generating Secure JWT Secret

### Recommended: 64+ Character Random String

#### Using Java:
```java
import java.security.SecureRandom;
import java.util.Base64;

public class SecretGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String secret = Base64.getEncoder().encodeToString(bytes);
        System.out.println("JWT_SECRET=" + secret);
    }
}
```

#### Using Online Tools:
- Generate at: https://randomkeygen.com/
- Select "CodeIgniter Encryption Keys" (256-bit)

#### Using OpenSSL:
```bash
openssl rand -base64 64
```

---

## 📋 Environment Variables for All Services

### User Service (Port 8082)
```
JWT_SECRET=<your-generated-secret>
JWT_EXPIRATION=3600000
```

### Order Service (Port 8083)
```
JWT_SECRET=<same-secret-as-user-service>
JWT_EXPIRATION=3600000
```

**IMPORTANT:** Use the **SAME** JWT secret across all services!

---

## ✅ Verification

### Check if Environment Variables are Set:

#### Windows CMD:
```cmd
echo %JWT_SECRET%
```

#### Windows PowerShell:
```powershell
echo $env:JWT_SECRET
```

#### Mac/Linux:
```bash
echo $JWT_SECRET
```

### Test in Application:
```java
// Add to any controller for testing
@GetMapping("/test-secret")
public String testSecret(@Value("${jwt.secret}") String secret) {
    return "Secret is configured: " + (secret != null && !secret.isEmpty());
}
```

---

## 🚫 What NOT To Do

### ❌ Bad Practice #1: Hard-coded in application.yml
```yaml
jwt:
  secret: myHardCodedSecret123  # DON'T DO THIS!
```

### ❌ Bad Practice #2: Committed to Git
```
.gitignore should include:
application-local.yml
application-dev.yml
.env
```

### ❌ Bad Practice #3: Same secret in production and development
```
Use different secrets for:
- Development
- Testing
- Staging
- Production
```

---

## ✅ Best Practices

### For Development:
1. Use environment variables in IntelliJ Run Configuration
2. Document required environment variables in README
3. Never commit secrets to Git

### For Production:
1. Use cloud secret managers:
   - AWS Secrets Manager
   - Azure Key Vault
   - Google Secret Manager
   - HashiCorp Vault
2. Rotate secrets regularly (every 90 days)
3. Use different secrets per environment
4. Monitor secret access logs

---

## 🔄 Secret Rotation

### When to Rotate:
- Every 90 days (recommended)
- After team member leaves
- If secret is potentially compromised
- During security audits

### How to Rotate:
1. Generate new JWT secret
2. Update environment variables
3. Restart all services
4. All existing JWT tokens become invalid
5. Users must login again

---

## 🐛 Troubleshooting

### Error: "jwt.secret could not be resolved"

**Cause:** JWT_SECRET environment variable not set

**Solution:**
1. Set JWT_SECRET environment variable
2. Restart application
3. Verify with: `echo $JWT_SECRET` (Mac/Linux) or `echo %JWT_SECRET%` (Windows)

### Error: "Key length must be at least 256 bits"

**Cause:** JWT secret too short

**Solution:** Use a secret with at least 32 characters (256 bits)

### Error: "JWT signature does not match"

**Cause:** Different secrets in User Service and Order Service

**Solution:** Ensure both services use the SAME JWT_SECRET value

---

## 📝 Example Setup Script

### For Development Team:

```bash
#!/bin/bash
# setup-secrets.sh

# Generate secure JWT secret
JWT_SECRET=$(openssl rand -base64 64)

# Export environment variables
export JWT_SECRET="$JWT_SECRET"
export JWT_EXPIRATION=3600000

echo "JWT Secret configured!"
echo "Add these to your IntelliJ Run Configuration:"
echo "JWT_SECRET=$JWT_SECRET"
echo "JWT_EXPIRATION=$JWT_EXPIRATION"
```

---

## 📚 Additional Resources

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)

---

## ⚠️ Security Checklist

Before running services:
- [ ] JWT_SECRET environment variable is set
- [ ] Secret is at least 64 characters long
- [ ] Same secret used across all services
- [ ] Secret is NOT committed to Git
- [ ] Secret is documented in team wiki (securely)
- [ ] Production secret is different from development

---

**Remember:** Security is only as strong as your weakest link. Proper secret management is CRITICAL! 🔒
