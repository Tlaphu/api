    package com.ra.base_spring_boot.security.jwt;

    import com.ra.base_spring_boot.model.Admin;
    import com.ra.base_spring_boot.model.Candidate;
    import com.ra.base_spring_boot.model.AccountCompany;
    import com.ra.base_spring_boot.security.principle.MyCompanyDetails;
    import com.ra.base_spring_boot.security.principle.MyUserDetails;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;

    import java.security.Key;
    import java.util.*;

    @Component
    public class JwtProvider {
        @Value("${jwt.secret.key}")
        private String SECRET_KEY;

        @Value("${jwt.expired.access}")
        private Long EXPIRED_ACCESS;

        public String extractEmail(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }

        private Claims extractAllClaims(String token) {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

        private Boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        // ===== Candidate =====
        public Boolean validateCandidateToken(String token, Candidate candidate) {
            final String email = extractEmail(token);
            return (email.equals(candidate.getEmail()) && !isTokenExpired(token));
        }

        public String generateCandidateToken(Candidate candidate, Set<String> roles) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles);
            claims.put("type", "candidate");
            return createToken(claims, candidate.getEmail());
        }

        // ===== Company =====
        public Boolean validateCompanyToken(String token, AccountCompany accountCompany) {
            final String email = extractEmail(token);
            return (email.equals(accountCompany.getEmail()) && !isTokenExpired(token));
        }

        public String generateCompanyToken(AccountCompany accountCompany, Set<String> roles) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles);
            claims.put("type", "company");
            return createToken(claims, accountCompany.getEmail());
        }

        private String createToken(Map<String, Object> claims, String subject) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject) 
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_ACCESS))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        private Key getSignKey() {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
        }



        public Candidate getCurrentCandidate() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof com.ra.base_spring_boot.security.principle.MyUserDetails userDetails) {
                return userDetails.getCandidate();
            }
            return null;
        }

        public AccountCompany getCurrentAccountCompany() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof com.ra.base_spring_boot.security.principle.MyCompanyDetails companyDetails) {
                return companyDetails.getAccountCompany();
            }
            return null;
        }


        public String getCandidateUsername() {
            Candidate candidate = getCurrentCandidate();
            return candidate != null ? candidate.getEmail() : null;
        }

        public String getCompanyUsername() {
            AccountCompany company = getCurrentAccountCompany();
            return company != null ? company.getEmail() : null;
        }

        public Boolean validateAdminToken(String token, Admin admin) {
            final String email = extractEmail(token);
            return (email.equals(admin.getEmail()) && !isTokenExpired(token));
        }

        public String generateAdminToken(Admin admin, Set<String> roles) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles);
            claims.put("type", "admin");
            return createToken(claims, admin.getEmail());
        }

        public Admin getCurrentAdmin() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof com.ra.base_spring_boot.security.principle.MyAdminDetails adminDetails) {
                return adminDetails.getAdmin();
            }
            return null;
        }

        public String getAdminUsername() {
            Admin admin = getCurrentAdmin();
            return admin != null ? admin.getEmail() : null;
        }
    }
